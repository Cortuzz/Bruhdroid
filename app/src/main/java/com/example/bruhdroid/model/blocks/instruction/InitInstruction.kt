package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class InitInstruction(expression: String = ""):
    Instruction(BlockInstruction.INIT, expression) {

    override fun evaluate(interpreter: Interpreter) {
        val rawList = interpreter.split(expression)

        for (raw in rawList) {
            interpreter.parseRawBlock(raw, true)
        }
    }

    override fun clone(): InitInstruction {
        return InitInstruction()
    }
}