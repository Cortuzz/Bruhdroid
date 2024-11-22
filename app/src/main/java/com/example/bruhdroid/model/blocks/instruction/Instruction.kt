package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.BlockInstruction

abstract class Instruction(instruction: BlockInstruction, expression: String):
    Block(instruction, expression) {
    protected lateinit var interpreter: Interpreter

    fun initInterpreter(interpreter: Interpreter) {
        this.interpreter = interpreter
    }

    abstract fun evaluate(): Boolean

    abstract fun clone(): Instruction
}