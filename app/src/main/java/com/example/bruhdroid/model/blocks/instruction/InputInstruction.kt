package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class InputInstruction(expression: String = ""):
    Instruction(BlockInstruction.INPUT, expression) {

    override fun evaluate(interpreter: Interpreter): Boolean {
        interpreter.waitingForInput = true

        return false
    }

    override fun clone(): InputInstruction {
        return InputInstruction(expression)
    }
}