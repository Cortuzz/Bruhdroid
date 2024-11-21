package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class ElseInstruction:
    Instruction(BlockInstruction.ELSE, "") {

    override fun evaluate(): Boolean {
        if (interpreter.appliedConditions.last()) {
            return true
        }
        interpreter.memory = interpreter.memory.prevMemory!!
        interpreter.memory = Memory(interpreter.memory, "ELSE SCOPE")
        return false
    }
}