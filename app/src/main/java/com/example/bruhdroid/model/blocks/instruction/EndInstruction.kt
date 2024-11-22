package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class EndInstruction:
    Instruction(BlockInstruction.END, "") {

    override fun evaluate(interpreter: Interpreter): Boolean {
        interpreter.appliedConditions.removeLast()
        interpreter.memory = interpreter.memory.prevMemory!!
        return false
    }

    override fun clone(): EndInstruction {
        return EndInstruction()
    }
}