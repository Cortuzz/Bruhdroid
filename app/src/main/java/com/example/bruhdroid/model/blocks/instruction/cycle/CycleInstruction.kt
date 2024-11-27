package com.example.bruhdroid.model.blocks.instruction.cycle

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.Instruction

abstract class CycleInstruction(instruction: BlockInstruction, expression: String):
    Instruction(instruction, expression) {
    protected fun skipCycle(interpreter: Interpreter) {
        var count = 1
        interpreter.memory = interpreter.memory.prevMemory!!
        while (interpreter.currentLine < interpreter.instructions!!.size - 1) {
            val block = interpreter.instructions!![++interpreter.currentLine]

            if (block is CycleInstruction) {
                count += block.cycleSkipChange()
            }

            if (count == 0)
                return
        }
    }

    abstract fun cycleSkipChange(): Int
}