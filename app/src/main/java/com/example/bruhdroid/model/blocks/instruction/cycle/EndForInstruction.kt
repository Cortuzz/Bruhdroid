package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class EndForInstruction:
    CycleInstruction(BlockInstruction.END_FOR, "") {
    override fun cycleSkipChange(): Int {
        return -1
    }

    override fun isEndInstruction(): Boolean {
        return true
    }

    override fun evaluate(interpreter: Interpreter) {
        interpreter.currentLine = interpreter.cycleLines.removeLast() - 1
        interpreter.memory = interpreter.memory.prevMemory!!
    }

    override fun clone(): EndForInstruction {
        return EndForInstruction()
    }
}