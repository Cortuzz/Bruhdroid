package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.Instruction

class ContinueInstruction:
    Instruction(BlockInstruction.CONTINUE, "") {

    override fun evaluate(interpreter: Interpreter) {
        try {
            interpreter.currentLine = interpreter.cycleLines.removeLast() - 1
            interpreter.memory = interpreter.memory.prevMemory!!
        } catch (e: Exception) {
            interpreter.throwOutOfCycleError("It is not possible to use CONTINUE block outside the context of a loop")
        }
    }

    override fun clone(): ContinueInstruction {
        return ContinueInstruction()
    }
}