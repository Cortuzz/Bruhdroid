package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class EndWhileInstruction:
    Instruction(BlockInstruction.END_WHILE, "") {

    override fun evaluate(interpreter: Interpreter): Boolean {
        interpreter.currentLine = interpreter.cycleLines.removeLast() - 1
        interpreter.memory = interpreter.memory.prevMemory!!
        return false
    }

    override fun clone(): EndWhileInstruction {
        return EndWhileInstruction()
    }
}