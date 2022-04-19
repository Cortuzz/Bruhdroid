package com.example.bruhdroid.model

import com.example.bruhdroid.model.blocks.*

class Interpreter(val blocks: List<Block>) {
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

    private fun checkStatement(block: Block) : Boolean {
        val booleanBlock = parseBlock(block.leftBody!!) as Valuable
        if (booleanBlock.value != "") {
            return true
        }
        return false
    }

    private fun parseBlock(block: Block) : Block {
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
            Instruction.PLUS -> leftBlock as Valuable + rightBlock as Valuable
            Instruction.MINUS -> leftBlock as Valuable - rightBlock as Valuable
            Instruction.MUL -> leftBlock as Valuable * rightBlock as Valuable
            Instruction.DIV -> leftBlock as Valuable / rightBlock as Valuable
            Instruction.MOD -> leftBlock as Valuable % rightBlock as Valuable
            Instruction.SET -> {
                tryPushToAnyMemory(memory, block, Type.INT, leftBlock)
                block
            }
            Instruction.INIT -> {
                pushToLocalMemory(block, Type.INT, leftBlock)
                block
            }
            else -> throw Exception("Bad Instruction")
        }
    }

    private fun pushToLocalMemory(block: Block, type: Type, valueBlock: Block) {
        valueBlock as Valuable
        block as Init

        memory.push(block.name, valueBlock)
    }

    private fun tryPushToAnyMemory(memory: Memory, block: Block, type: Type, valueBlock: Block): Boolean {
        valueBlock as Valuable
        block as Assign

        if (memory.stack[block.name] != null) {
            memory.push(block.name, valueBlock)
            return true
        }

        if (memory.prevMemory == null) {
            return false
        }

        return tryPushToAnyMemory(memory.prevMemory, block, type, valueBlock)
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
}
