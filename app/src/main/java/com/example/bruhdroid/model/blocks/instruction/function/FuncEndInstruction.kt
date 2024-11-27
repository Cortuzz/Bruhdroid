package com.example.bruhdroid.model.blocks.instruction.function

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class FuncEndInstruction:
    FunctionInstruction(BlockInstruction.FUNC_END, "") {
    override fun funcSkipChange(): Int {
        return -1
    }

    override fun isEndInstruction(): Boolean {
        return true
    }

    override fun evaluate(interpreter: Interpreter) {

    }

    override fun clone(): FuncEndInstruction {
        return FuncEndInstruction()
    }
}