package com.example.bruhdroid.model.blocks.instruction.condition

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class ElifInstruction(expression: String = ""):
    ConditionInstruction(BlockInstruction.ELIF, expression) {
    override fun conditionSkipChange(count: Int, interpreter: Interpreter): ConditionSkipDto {
        if (count != 1)
            return ConditionSkipDto(0, false)
        interpreter.currentLine--
        return ConditionSkipDto(0, true)
    }

    override fun evaluate(interpreter: Interpreter) {
        if (interpreter.appliedConditions.last()) {
            skipFalseBranches(interpreter)
            return
        }
        val statement = interpreter.checkStatement(expression)
        interpreter.appliedConditions[interpreter.appliedConditions.lastIndex] = statement
        interpreter.memory = interpreter.memory.prevMemory!!
        interpreter.memory = Memory(interpreter.memory, "ELIF SCOPE")

        if (!statement)
            skipFalseBranches(interpreter)
    }

    override fun clone(): ElifInstruction {
        return ElifInstruction(expression)
    }
}