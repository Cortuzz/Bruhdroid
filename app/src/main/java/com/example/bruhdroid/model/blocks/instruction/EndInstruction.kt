package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class EndInstruction:
    Instruction(BlockInstruction.END, "") {

    override fun evaluate(): Boolean {
        interpreter.appliedConditions.removeLast()
        interpreter.memory = interpreter.memory.prevMemory!!
        return false
    }

    override fun clone(): EndInstruction {
        return EndInstruction()
    }
}