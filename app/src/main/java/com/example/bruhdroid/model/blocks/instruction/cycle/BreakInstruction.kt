package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class BreakInstruction:
    CycleInstruction(BlockInstruction.BREAK, "") {
    override fun cycleSkipChange(): Int {
        return 0
    }

    override fun evaluate(interpreter: Interpreter) {
        try {
            skipCycle(interpreter)
        } catch (e: Exception) {
            interpreter.throwOutOfCycleError("It is not possible to use BREAK outside the context of a loop")
        }
    }

    override fun clone(): BreakInstruction {
        return BreakInstruction()
    }
}