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
            Instruction.PLUS -> leftBlock as Variable + rightBlock as Variable
            Instruction.MINUS -> leftBlock as Variable - rightBlock as Variable
            Instruction.MUL -> leftBlock as Variable * rightBlock as Variable
            Instruction.DIV -> leftBlock as Variable / rightBlock as Variable
            Instruction.MOD -> leftBlock as Variable % rightBlock as Variable

            Instruction.INIT -> {
                leftBlock as Variable
                block as Init

                memory.push(block.name, leftBlock)
                block
            }
        }
    }
}