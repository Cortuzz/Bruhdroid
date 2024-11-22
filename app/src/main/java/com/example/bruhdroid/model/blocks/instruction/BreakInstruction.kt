package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class BreakInstruction:
    Instruction(BlockInstruction.BREAK, "") {

    override fun evaluate(interpreter: Interpreter): Boolean {
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