package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class WhileInstruction(expression: String = ""):
    Instruction(BlockInstruction.WHILE, expression) {

    override fun evaluate(): Boolean {
        interpreter.memory = Memory(interpreter.memory, "WHILE ITERATION SCOPE")
        if (interpreter.checkStatement(expression)) {
            interpreter.cycleLines.add(interpreter.currentLine)
        } else {
            interpreter.skipCycle()
        }
        return false
    }
}