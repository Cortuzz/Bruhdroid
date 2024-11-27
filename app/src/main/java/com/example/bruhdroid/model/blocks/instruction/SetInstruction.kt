package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class SetInstruction(expression: String = ""):
    Instruction(BlockInstruction.SET, expression) {

    override fun evaluate(interpreter: Interpreter) {
        val rawList = interpreter.split(expression)

        for (raw in rawList) {
            interpreter.parseRawBlock(raw, false)
        }
    }

    override fun clone(): SetInstruction {
        return SetInstruction(expression)
    }
}