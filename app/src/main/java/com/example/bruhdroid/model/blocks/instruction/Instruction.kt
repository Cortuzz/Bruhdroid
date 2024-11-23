package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import java.io.Serializable

abstract class Instruction(
    val instruction: BlockInstruction,
    var expression: String,
) : Serializable {
    var breakpoint: Boolean = false
    abstract fun evaluate(interpreter: Interpreter)

    abstract fun clone(): Instruction

    fun getJsonEncoding(): String {
        return this.javaClass.name
    }

    fun compareJsonEncoding(name: String): Boolean {
        return getJsonEncoding() == name
    }
}