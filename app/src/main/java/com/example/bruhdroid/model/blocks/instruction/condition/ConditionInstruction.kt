package com.example.bruhdroid.model.blocks.instruction.condition

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.Instruction

abstract class ConditionInstruction(instruction: BlockInstruction, expression: String):
    Instruction(instruction, expression) {
    data class ConditionSkipDto(
        val countChange: Int,
        val exit: Boolean
    )

    protected fun skipFalseBranches(interpreter: Interpreter) {
        var count = 1
        while (interpreter.currentLine < interpreter.instructions!!.size - 1) {
            val block = interpreter.instructions!![++interpreter.currentLine]

            if (block is ConditionInstruction) {
                val conditionSkipDto = block.conditionSkipChange(count, interpreter)
                count += conditionSkipDto.countChange
                if (conditionSkipDto.exit)
                    return
            }


            if (count == 0) {
                interpreter.currentLine--
                return
            }
        }
    }

    abstract fun conditionSkipChange(count: Int, interpreter: Interpreter): ConditionSkipDto
}