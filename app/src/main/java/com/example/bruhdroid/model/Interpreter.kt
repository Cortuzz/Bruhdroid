package com.example.bruhdroid.model

import com.example.bruhdroid.model.blocks.*

class Interpreter(val blocks: List<Block>, val debugMode: Boolean = false) {
    var memory = Memory(null)
    var totalLines = 0

    fun run(blockchain: List<Block>? = null) {
        var currentBlockchain = blocks
        if (blockchain != null) {
            memory = Memory(memory)
            currentBlockchain = blockchain
        }

        var statementApplied = false
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
            Instruction.INIT -> {
                block as Init
                val raw = block.body as RawInput
                val name = parseVarName(raw.input)

                pushToLocalMemory(name, valueBlock=Valuable("", Type.UNDEFINED))
                parseRawBlock(block.body)
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

    private fun parseVarName(str: String): String {
        var parsedStr = ""

        for (symbol in str) {
            if (symbol == '=') {
                break
            }
            if (symbol == ' ') {
                continue
            }
            parsedStr += symbol
        }
        return parsedStr
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
            if (data[count].isDigit() || data[count].isLetter()) {
                while (data[count].isDigit() || data[count].isLetter()) {
                    tempStr += data[count]
                    count++
                }
                if (tempStr.last().isLetter()) {
                    stack.add(Variable(tempStr))
                } else {
                    stack.add(Valuable(tempStr, Type.INT))
                }

                tempStr = ""
                count--
            } else if (data[count] == ' ') {
            } else {
                var operand2 = stack.removeLast()
                var operand1 = stack.removeLast()

                if (operand2 is Variable) {
                    try {
                        operand2 = tryFindInMemory(memory, operand2)
                    } catch (e: StackCorruptionError) {
                        throw RuntimeError("${e.message}\nAt line ${block.line}, " +
                                "At instruction: ${block.instruction}")
                    }
                }
                operand2 as Valuable

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
                    '=' -> {
                        pushToLocalMemory(operand1.value, Type.INT, operand2)
                        operand2
                    }
                    else -> null
                }

                stack.add(result!!)
                count++
            }
            count++
        }

        return stack.removeLast() as Valuable
    }
}
