package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class InputInstruction(expression: String = ""):
    Instruction(BlockInstruction.INPUT, expression) {

    override fun evaluate(interpreter: Interpreter) {
        interpreter.waitingForInput = true
    }

    override fun clone(): InputInstruction {
        return InputInstruction(expression)
    }
}