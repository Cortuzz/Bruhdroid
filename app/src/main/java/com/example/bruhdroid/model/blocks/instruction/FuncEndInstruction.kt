package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class FuncEndInstruction:
    Instruction(BlockInstruction.FUNC_END, "") {

    override fun evaluate(): Boolean {
        return false
    }

    override fun clone(): FuncEndInstruction {
        return FuncEndInstruction()
    }
}