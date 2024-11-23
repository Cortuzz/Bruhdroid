package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class EndWhileInstruction:
    CycleInstruction(BlockInstruction.END_WHILE, "") {
    override fun cycleSkipChange(): Int {
        return -1
    }

    override fun evaluate(interpreter: Interpreter) {
        interpreter.currentLine = interpreter.cycleLines.removeLast() - 1
        interpreter.memory = interpreter.memory.prevMemory!!
    }

    override fun clone(): EndWhileInstruction {
        return EndWhileInstruction()
    }
}