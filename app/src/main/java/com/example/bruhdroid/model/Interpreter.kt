package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.*
import com.example.bruhdroid.model.src.blocks.*
import java.util.*

class Interpreter(private var blocks: List<Block>? = null, val debugMode: Boolean = false): Observable() {
    var output = ""
    var input = ""
    var waitingForInput = false
    var memory = Memory(null)
    private var appliedConditions: MutableList<Boolean> = mutableListOf()
    private var currentLine = -1

    fun initBlocks(_blocks: List<Block>) {
        currentLine = -1
        blocks = _blocks
    }

    fun run() {
        while (currentLine < blocks!!.size - 1) {
            val block = blocks!![++currentLine]

            try {
                if (parse(block)) {
                    skipFalseBranches()
                }
            } catch (e: RuntimeError) {
                throw RuntimeError("${e.message}\nAt line: ${block.line}, " +
                        "At instruction: ${block.instruction}")
            }
        }
    }

    private fun skipFalseBranches() {
        var count = 1
        while (currentLine < blocks!!.size - 1) {

            val block = blocks!![++currentLine]

            if (block.instruction == Instruction.IF) { // todo: cycle check
                count++
            }
            if (block.instruction == Instruction.END) {
                count--
            }

            if (count == 0 || (count == 1 && (block.instruction == Instruction.ELIF ||
                        block.instruction == Instruction.ELSE))) {
                currentLine--
                return
            }
        }
    }

    private fun parse(block: Block): Boolean {
        when (block.instruction) {
            Instruction.PRINT -> {
                val rawList = block.expression.split(',')

                for (raw in rawList) {
                    output += parseRawBlock(raw).value + " "
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
                val raw = block.expression
                if (pushVariablesToMemory(raw)) {
                    parseRawBlock(raw)
                }
            }
            Instruction.SET -> {
                val raw = block.expression
                parseRawBlock(raw)
            }
            Instruction.IF -> {
                val statement = checkStatement(block.expression)
                appliedConditions.add(statement)
                return !statement
            }
            Instruction.ELIF -> {
                if (appliedConditions.last()) {
                    return true
                }
                val statement = checkStatement(block.expression)
                appliedConditions[appliedConditions.lastIndex] = statement
                return !statement
            }
            Instruction.ELSE -> {
                if (appliedConditions.last()) {
                    return true
                }
                return false
            }
            Instruction.END -> {
                appliedConditions.removeLast()
                return false
            }
            else -> parseRawBlock(block.expression)
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

    private fun checkStatement(statement: String): Boolean {
        val booleanBlock = parseRawBlock(statement)
        if (booleanBlock.value != "false") {
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

    private fun parseRawBlock(raw: String): Valuable {
        val data = Notation.convertToRpn(Notation.normalizeString(raw))

        var count = 0
        val stack = mutableListOf<Block>()
        var tempStr = ""

        while (count < data.length) {
            if (data[count].isDigit() || data[count].isLetter() || data[count] == '"') {
                while (data[count].isDigit() || data[count].isLetter() || data[count] in "\".") {
                    tempStr += data[count]
                    count++
                }
                if (tempStr.last() == '"' && tempStr.first() == '"') {
                    // Maybe substring is better solution
                    stack.add(Valuable(tempStr.replace("\"", ""), Type.STRING))
                } else if (tempStr.contains("[A-Za-z]".toRegex())) {
                    stack.add(Variable(tempStr))
                } else {
                    if (tempStr.contains('.')) {
                        stack.add(Valuable(tempStr, Type.FLOAT))
                    } else {
                        stack.add(Valuable(tempStr, Type.INT))
                    }
                }

                tempStr = ""
                count--
            } else if (data[count] == ' ') {
            } else {
                try {
                    var operand2 = stack.removeLast()

                    if (operand2 is Variable) {
                        try {
                            operand2 = tryFindInMemory(memory, operand2)
                        } catch (e: StackCorruptionError) {
                            throw RuntimeError("${e.message}\nAt expression: $raw")
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
                            tryPushToAnyMemory(memory, operand1.value, Type.INT, operand2) // todo: Any memory
                            operand2
                        }
                        else -> null
                    }

                    stack.add(result!!)
                    count++
                } catch (e: TypeError) {
                    throw RuntimeError("${e.message}\nAt expression: $raw")
                }
            }
            count++
        }

        val last = stack.removeLast()
        if (last is Variable) {
            try {
                return tryFindInMemory(memory, last) as Valuable
            } catch (e: StackCorruptionError) {
                throw RuntimeError("${e.message}\nAt expression: $raw")
            }
        }
        return last as Valuable
    }

    fun popOutput(): String {
        val out = output
        output = ""
        return out
    }
}
