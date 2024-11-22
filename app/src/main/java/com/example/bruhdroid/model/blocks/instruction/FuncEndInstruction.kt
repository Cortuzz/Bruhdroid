package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class FuncEndInstruction:
    Instruction(BlockInstruction.FUNC_END, "") {

    override fun evaluate(interpreter: Interpreter): Boolean {
        return false
    }

    override fun clone(): FuncEndInstruction {
        return FuncEndInstruction()
    }
}