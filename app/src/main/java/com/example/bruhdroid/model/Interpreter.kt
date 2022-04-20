package com.example.bruhdroid.model

import com.example.bruhdroid.model.blocks.*

class Interpreter(val blocks: List<Block>, val debugMode: Boolean = false) {
    var memory = Memory(null)

    fun run(blockchain: List<Block>? = null) {
        var statementApplied = false

        var currentBlockchain = blocks
        if (blockchain != null) {
            memory = Memory(memory)
            currentBlockchain = blockchain
        }

        for (block in currentBlockchain) {
            when (block.instruction) {
                Instruction.INIT -> {
                    block as Init
                    val raw = block.body as RawInput
                    val name = parseVarName(raw.input)

                    pushToLocalMemory(name, valueBlock=Valuable("", Type.UNDEFINED))
                    parseBlock(block.body)
                }
                Instruction.IF -> {
                    statementApplied = if (checkStatement(block)) {
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
                        statementApplied = true
                    }
                }
                Instruction.ELSE -> {
                    if (!statementApplied) {
                        block.rightBody as Container
                        run(block.rightBody.instructions)
                    }
                }
                else -> parseBlock(block)
            }

        }

        if (memory.prevMemory != null) {
            memory = memory.prevMemory!!
        }
    }

    private fun parseVarName(str: String): String {
        var parsedStr = ""

        for (symbol in str) {
            if (symbol == '=') {
                break
            }
            parsedStr += symbol
        }
        return parsedStr
    }

    private fun checkStatement(block: Block): Boolean {
        val booleanBlock = parseBlock(block.leftBody!!) as Valuable
        if (booleanBlock.value != "") {
            return true
        }
        return false
    }

    private fun parseBlock(block: Block): Block {
        lateinit var leftBlock: Block
        lateinit var rightBlock: Block

        if (block.leftBody != null) {
            leftBlock = parseBlock(block.leftBody)
        }
        if (block.rightBody != null) {
            rightBlock = parseBlock(block.rightBody)
        }

        return when (block.instruction) {
            Instruction.VAL -> block
            Instruction.VAR -> tryFindInMemory(memory, block)
            Instruction.RAW -> parseRawBlock(block)

            Instruction.PLUS -> leftBlock as Valuable + rightBlock as Valuable
            Instruction.MINUS -> leftBlock as Valuable - rightBlock as Valuable
            Instruction.MUL -> leftBlock as Valuable * rightBlock as Valuable
            Instruction.DIV -> leftBlock as Valuable / rightBlock as Valuable
            Instruction.MOD -> leftBlock as Valuable % rightBlock as Valuable
            else -> throw Exception("Bad Instruction")
        }
    }

    private fun pushToLocalMemory(name: String, type: Type = Type.UNDEFINED, valueBlock: Block) {
        valueBlock as Valuable
        valueBlock.type = type

        memory.push(name, valueBlock)
    }

    private fun tryPushToAnyMemory(memory: Memory, name: String, type: Type, valueBlock: Block): Boolean {
        valueBlock as Valuable

        if (memory.stack[name] != null) {
            memory.push(name, valueBlock)
            return true
        }

        if (memory.prevMemory == null) {
            return false
        }

        return tryPushToAnyMemory(memory.prevMemory, name, type, valueBlock)
    }

    private fun tryFindInMemory(memory: Memory, block: Block): Block {
        block as Variable
        val address = block.name
        val value = memory.stack[address]

        if (value != null) {
            return value
        }

        if (memory.prevMemory == null) {
            throw Exception()
        }

        return tryFindInMemory(memory.prevMemory, block)
    }

    fun parseRawBlock(block: Block): Valuable {
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
                    operand2 = tryFindInMemory(memory, operand2)
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
