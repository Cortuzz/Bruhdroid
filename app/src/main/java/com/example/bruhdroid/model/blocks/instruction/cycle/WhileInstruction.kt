package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class WhileInstruction(expression: String = ""):
    CycleInstruction(BlockInstruction.WHILE, expression) {
    override fun cycleSkipChange(): Int {
        return 1
    }

    override fun evaluate(interpreter: Interpreter) {
        interpreter.memory = Memory(interpreter.memory, "WHILE ITERATION SCOPE")
        if (interpreter.checkStatement(expression)) {
            interpreter.cycleLines.add(interpreter.currentLine)
        } else {
            skipCycle(interpreter)
        }
    }

    override fun clone(): WhileInstruction {
        return WhileInstruction(expression)
    }
}