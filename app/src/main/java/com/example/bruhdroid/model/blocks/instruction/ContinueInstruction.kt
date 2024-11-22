package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class ContinueInstruction:
    Instruction(BlockInstruction.CONTINUE, "") {

    override fun evaluate(interpreter: Interpreter): Boolean {
        try {
            interpreter.currentLine = interpreter.cycleLines.removeLast() - 1
            interpreter.memory = interpreter.memory.prevMemory!!
        } catch (e: Exception) {
            interpreter.throwOutOfCycleError("It is not possible to use CONTINUE block outside the context of a loop")
        }
        return false
    }

    override fun clone(): ContinueInstruction {
        return ContinueInstruction()
    }
}