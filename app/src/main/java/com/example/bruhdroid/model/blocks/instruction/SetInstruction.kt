package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction

class SetInstruction(expression: String = "", private val initialization: Boolean = false):
    Instruction(if (initialization) BlockInstruction.INIT else BlockInstruction.SET, expression) {

    override fun evaluate(): Boolean {
        val rawList = interpreter.split(expression)

        for (raw in rawList) {
            interpreter.parseRawBlock(raw, initialization)
        }
        return false
    }
}