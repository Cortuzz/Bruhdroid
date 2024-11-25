package com.example.bruhdroid.model.blocks.instruction.condition

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class IfInstruction(expression: String = ""):
    ConditionInstruction(BlockInstruction.IF, expression) {
    override fun conditionSkipChange(count: Int, interpreter: Interpreter): ConditionSkipDto {
        return ConditionSkipDto(1, false)
    }

    override fun isStartInstruction(): Boolean {
        return true
    }

    override fun evaluate(interpreter: Interpreter) {
        val statement = interpreter.checkStatement(expression)
        interpreter.appliedConditions.add(statement)
        interpreter.memory = Memory(interpreter.memory, "IF SCOPE")
        if (!statement)
            skipFalseBranches(interpreter)
    }

    override fun clone(): IfInstruction {
        return IfInstruction(expression)
    }
}