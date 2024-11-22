package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class BreakInstruction:
    Instruction(BlockInstruction.BREAK, "") {

    override fun evaluate(): Boolean {
        try {
            interpreter.skipCycle()
        } catch (e: Exception) {
            interpreter.throwOutOfCycleError("It is not possible to use BREAK outside the context of a loop")
        }
        return false
    }

    override fun clone(): BreakInstruction {
        return BreakInstruction()
    }
}