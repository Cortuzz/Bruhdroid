package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class ForInstruction(expression: String = ""):
    CycleInstruction(BlockInstruction.FOR, expression) {
    override fun cycleSkipChange(): Int {
        return 1
    }

    override fun evaluate(interpreter: Interpreter) {
        val raw = expression.split(",")
        if (interpreter.currentLine !in interpreter.forLines) {
            interpreter.memory = Memory(interpreter.memory, "FOR SCOPE")
            interpreter.parseRawBlock(raw[0], true)
            interpreter.forLines.add(interpreter.currentLine)
        } else {
            interpreter.parseRawBlock(raw[2])
        }

        interpreter.memory = Memory(interpreter.memory, "FOR ITERATION SCOPE")
        if (interpreter.checkStatement(raw[1])) {
            interpreter.cycleLines.add(interpreter.currentLine)
        } else {
            interpreter.memory = interpreter.memory.prevMemory!!
            interpreter.forLines.remove(interpreter.currentLine)
            skipCycle(interpreter)
        }
    }

    override fun clone(): ForInstruction {
        return ForInstruction(expression)
    }
}