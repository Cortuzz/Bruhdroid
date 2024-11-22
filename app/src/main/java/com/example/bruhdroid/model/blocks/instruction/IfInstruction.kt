package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class IfInstruction(expression: String = ""):
    Instruction(BlockInstruction.IF, expression) {

    override fun evaluate(): Boolean {
        val statement = interpreter.checkStatement(expression)
        interpreter.appliedConditions.add(statement)
        interpreter.memory = Memory(interpreter.memory, "IF SCOPE")
        return !statement
    }

    override fun clone(): IfInstruction {
        return IfInstruction(expression)
    }
}