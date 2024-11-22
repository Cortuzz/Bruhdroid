package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class PragmaInstruction(expression: String = ""):
    Instruction(BlockInstruction.PRAGMA, expression) {

    override fun evaluate(interpreter: Interpreter): Boolean {
        val rawList = interpreter.split(expression)
        for (raw in rawList) {
            interpreter.parsePragma(raw)
        }

        interpreter.pragmaUpdate()
        interpreter.notifyClients()

        return false
    }

    override fun clone(): PragmaInstruction {
        return PragmaInstruction(expression)
    }
}