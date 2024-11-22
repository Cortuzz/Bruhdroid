package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class SetInstruction(expression: String = ""):
    Instruction(BlockInstruction.SET, expression) {

    override fun evaluate(interpreter: Interpreter): Boolean {
        val rawList = interpreter.split(expression)

        for (raw in rawList) {
            interpreter.parseRawBlock(raw, false)
        }
        return false
    }

    override fun clone(): SetInstruction {
        return SetInstruction(expression)
    }
}