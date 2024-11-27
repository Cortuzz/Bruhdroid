package com.example.bruhdroid.model.blocks.instruction.condition

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class ElseInstruction:
    ConditionInstruction(BlockInstruction.ELSE, "") {
    override fun conditionSkipChange(count: Int, interpreter: Interpreter): ConditionSkipDto {
        if (count != 1)
            return ConditionSkipDto(0, false)
        interpreter.currentLine--
        return ConditionSkipDto(0, true)
    }

    override fun isMiddleInstruction(): Boolean {
        return true
    }

    override fun evaluate(interpreter: Interpreter) {
        if (interpreter.appliedConditions.last()) {
            skipFalseBranches(interpreter)
            return
        }
        interpreter.memory = interpreter.memory.prevMemory!!
        interpreter.memory = Memory(interpreter.memory, "ELSE SCOPE")
    }

    override fun clone(): ElseInstruction {
        return ElseInstruction()
    }
}