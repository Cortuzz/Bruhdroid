package com.example.bruhdroid.model.blocks.instruction.condition

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class EndInstruction:
    ConditionInstruction(BlockInstruction.END, "") {
    override fun conditionSkipChange(count: Int, interpreter: Interpreter): ConditionSkipDto {
        return ConditionSkipDto(-1, false)
    }

    override fun evaluate(interpreter: Interpreter) {
        interpreter.appliedConditions.removeLast()
        interpreter.memory = interpreter.memory.prevMemory!!
    }

    override fun clone(): EndInstruction {
        return EndInstruction()
    }
}