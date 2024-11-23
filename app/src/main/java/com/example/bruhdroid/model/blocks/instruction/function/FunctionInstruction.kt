package com.example.bruhdroid.model.blocks.instruction.function

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.Instruction

abstract class FunctionInstruction(instruction: BlockInstruction, expression: String):
    Instruction(instruction, expression) {
    fun skipFunc(interpreter: Interpreter) {
        var count = 1
        interpreter.memory = interpreter.memory.prevMemory!!
        while (interpreter.currentLine < interpreter.instructions!!.size - 1) {
            val block = interpreter.instructions!![++interpreter.currentLine]

            if (block is FunctionInstruction)
                count += block.funcSkipChange()

            if (count == 0)
                return
        }
    }

    abstract fun funcSkipChange(): Int
}