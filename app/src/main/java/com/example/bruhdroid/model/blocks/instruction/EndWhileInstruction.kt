package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class EndWhileInstruction:
    Instruction(BlockInstruction.END_WHILE, "") {

    override fun evaluate(): Boolean {
        interpreter.currentLine = interpreter.cycleLines.removeLast() - 1
        interpreter.memory = interpreter.memory.prevMemory!!
        return false
    }
}