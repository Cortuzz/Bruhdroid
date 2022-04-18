package com.example.bruhdroid

import android.util.Log
import com.example.bruhdroid.blocks.Block
import com.example.bruhdroid.blocks.Init
import com.example.bruhdroid.blocks.Variable
import com.example.bruhdroid.blocks.variables.Integer
import com.example.bruhdroid.blocks.variables.Str

class Interpreter {
    val memory = Memory(null)

    init {

    }

    fun parseBlock(block: Block) : Block {
        lateinit var leftBlock: Block
        lateinit var rightBlock: Block

        if (block.leftBody != null) {
            leftBlock = parseBlock(block.leftBody)
        }
        if (block.rightBody != null) {
            rightBlock = parseBlock(block.rightBody)
        }

        return when (block.instruction) {
            Instruction.VAR -> block
            Instruction.ADD -> leftBlock as Integer + rightBlock as Integer
            Instruction.INIT -> {
                leftBlock as Integer
                block as Init

                memory.push(block.name, leftBlock)
                block
            }
        }
    }

    fun parseTypes(leftBlock: Block, rightBlock: Block): Block {
        if (leftBlock is Integer && rightBlock is Integer) {
            return leftBlock + rightBlock
        } else if (leftBlock is Integer && rightBlock is Str) {
            return leftBlock + rightBlock
        } else if (leftBlock is Str && rightBlock is Integer) {
            return leftBlock + rightBlock
        } else if ((leftBlock is Str && rightBlock is Str)) {
            return leftBlock + rightBlock
        }
        throw Exception()
    }
}