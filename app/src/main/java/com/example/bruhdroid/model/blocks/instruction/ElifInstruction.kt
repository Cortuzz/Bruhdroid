package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class ElifInstruction(expression: String = ""):
    Instruction(BlockInstruction.ELIF, expression) {

    override fun evaluate(): Boolean {
        if (interpreter.appliedConditions.last()) {
            return true
        }
        val statement = interpreter.checkStatement(expression)
        interpreter.appliedConditions[interpreter.appliedConditions.lastIndex] = statement
        interpreter.memory = interpreter.memory.prevMemory!!
        interpreter.memory = Memory(interpreter.memory, "ELIF SCOPE")
        return !statement
    }
}