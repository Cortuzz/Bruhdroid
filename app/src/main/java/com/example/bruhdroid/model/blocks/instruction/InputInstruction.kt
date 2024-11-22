package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class InputInstruction(expression: String = ""):
    Instruction(BlockInstruction.INPUT, expression) {

    override fun evaluate(): Boolean {
        interpreter.waitingForInput = true

        return false
    }

    override fun clone(): InputInstruction {
        return InputInstruction(expression)
    }
}