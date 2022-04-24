package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.blocks.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.StackCorruptionError
import com.example.bruhdroid.model.src.Type
import java.util.*

class Interpreter(private var blocks: List<Block>? = null, val debugMode: Boolean = false): Observable() {
    var output = ""
    var input = ""
    var waitingForInput = false
    var memory = Memory(null)
    var totalLines = 0

    fun initBlocks(_blocks: List<Block>) {
        blocks = _blocks
    }

    fun run(blockchain: List<Block>? = null) {
        var currentBlockchain = blocks!!
        if (blockchain != null) {
            memory = Memory(memory)
            currentBlockchain = blockchain
        }

        var statementApplied = false // todo: check statement
        for (block in currentBlockchain) {
            try {
                parse(block, statementApplied)
            } catch (e: RuntimeError) {
                throw RuntimeError("${e.message}\nAt line ${block.line}, " +
                        "At instruction: ${block.instruction}")
            }
        }

        if (memory.prevMemory != null) {
            memory = memory.prevMemory!!
        }
    }

    private fun parse(block: Block, statementApplied: Boolean): Boolean {
        totalLines++
        block.line = totalLines
        when (block.instruction) {
            Instruction.PRINT -> {
                block as Print
                val rawList = (block.body as RawInput).input.split(',')

                for (raw in rawList) {
                    output += parseRawBlock(RawInput(raw)).value
                }
                setChanged()
                notifyObservers()
            }
            Instruction.INPUT -> {
                waitingForInput = true
                setChanged()
                notifyObservers()
            }
            Instruction.INIT -> {
                block as Init
                val raw = block.body as RawInput
                if (pushVariablesToMemory(raw.input)) {
                    parseRawBlock(block.body)
                }
            }
            Instruction.IF -> {
                return if (checkStatement(block)) {
                    block.rightBody as Container
                    run(block.rightBody.instructions)
                    true
                } else {
                    false
                }
            }
            Instruction.ELIF -> {
                if (!statementApplied && checkStatement(block)) {
                    block.rightBody as Container
                    run(block.rightBody.instructions)
                    return true
                }
            }
            Instruction.ELSE -> {
                if (!statementApplied) {
                    block.rightBody as Container
                    run(block.rightBody.instructions)
                }
            }
            else -> parseRawBlock(block)
        }

        return false
    }

    private fun pushVariablesToMemory(str: String): Boolean {
        var parsedStr = ""
        var flag = false

        for (symbol in str) {
            if (symbol == '=') {
                flag = true
                break
            }
            if (symbol == ' ') {
                continue
            }
            if (symbol == ',') {
                pushToLocalMemory(parsedStr, valueBlock=Valuable("", Type.UNDEFINED))
                parsedStr = ""
                continue
            }
            parsedStr += symbol
        }

        if (flag) {
            try {
                tryFindInMemory(memory, Variable(parsedStr))
            } catch (e: StackCorruptionError) {
                pushToLocalMemory(parsedStr, valueBlock=Valuable("", Type.UNDEFINED))
            }
            return true
        }
        pushToLocalMemory(parsedStr, valueBlock=Valuable("", Type.UNDEFINED))
        return false
    }

    private fun checkStatement(block: Block): Boolean {
        val booleanBlock = parseRawBlock(block.leftBody!!)
        if (booleanBlock.value != "") {
            return true
        }
        return false
    }

    private fun pushToLocalMemory(name: String, type: Type = Type.UNDEFINED, valueBlock: Block) {
        valueBlock as Valuable
        valueBlock.type = type

        memory.push(name, valueBlock)
    }

    private fun tryPushToAnyMemory(memory: Memory, name: String, type: Type, valueBlock: Block): Boolean {
        valueBlock as Valuable
        valueBlock.type = type

        if (memory.get(name) != null) {
            memory.push(name, valueBlock)
            return true
        }

        if (memory.prevMemory == null) {
            memory.throwStackError(name)
            return false
        }

        return tryPushToAnyMemory(memory.prevMemory, name, type, valueBlock)
    }

    private fun tryFindInMemory(memory: Memory, block: Block): Block {
        block as Variable
        val address = block.name
        val value = memory.get(address)

        if (value != null) {
            return value
        }

        if (memory.prevMemory == null) {
            memory.throwStackError(address)
            throw Exception()
        }

        return tryFindInMemory(memory.prevMemory, block)
    }

    private fun parseRawBlock(block: Block): Valuable {
        block.line = totalLines
        block as RawInput
        val data = Notation.convertToRpn(Notation.normalizeString(block.input))

        var count = 0
        val stack = mutableListOf<Block>()
        var tempStr = ""

        while (count < data.length) {
            if (data[count].isDigit() || data[count].isLetter() || data[count] == '"') {
                while (data[count].isDigit() || data[count].isLetter() || data[count] in "\".") {
                    tempStr += data[count]
                    count++
                }
                if (tempStr.last().isLetter()) {
                    stack.add(Variable(tempStr))
                } else {
                    if (tempStr.last() == '"' && tempStr.first() == '"') {
                        // Maybe substring is better solution
                        stack.add(Valuable(tempStr.replace("\"", ""), Type.STRING))
                    } else if (tempStr.contains('.')) {
                        stack.add(Valuable(tempStr, Type.FLOAT))
                    } else {
                        stack.add(Valuable(tempStr, Type.INT))
                    }
                }

                tempStr = ""
                count--
            } else if (data[count] == ' ') {
            } else {
                var operand2 = stack.removeLast()

                if (operand2 is Variable) {
                    try {
                        operand2 = tryFindInMemory(memory, operand2)
                    } catch (e: StackCorruptionError) {
                        throw RuntimeError("${e.message}\nAt line ${block.line}, " +
                                "At instruction: ${block.instruction}")
                    }
                }
                operand2 as Valuable

                if (data[count] in "∓±") {
                    stack.add(when (data[count]) {
                        '±' -> +operand2
                        '∓' -> -operand2
                        else -> throw Exception()
                    })
                    count += 2
                    continue
                }
                var operand1 = stack.removeLast()

                if (data[count] == '=') {
                    operand1 as Variable
                    tryPushToAnyMemory(memory, operand1.name, operand2.type, operand2)

                     return operand2
                }

                if (operand1 is Variable) {
                    operand1 = tryFindInMemory(memory, operand1)
                }
                operand1 as Valuable

                var result: Valuable? = when (data[count]) {
                    '+' -> operand1 + operand2
                    '-' -> operand1 - operand2
                    '*' -> operand1 * operand2
                    '/' -> operand1 / operand2

                    '&' -> operand1.and(operand2)
                    '|' -> operand1.or(operand2)

                    '<' -> Valuable(operand1 < operand2, Type.BOOL)
                    '>' -> Valuable(operand1 > operand2, Type.BOOL)
                    '=' -> {
                        pushToLocalMemory(operand1.value, Type.INT, operand2) // todo: Any memory
                        operand2
                    }
                    else -> null
                }

                stack.add(result!!)
                count++
            }
            count++
        }

        val last = stack.removeLast()
        if (last is Variable) {
            return tryFindInMemory(memory, last) as Valuable
        }
        return last as Valuable
    }

    fun popOutput(): String {
        val out = output
        output = ""
        return out
    }
}
