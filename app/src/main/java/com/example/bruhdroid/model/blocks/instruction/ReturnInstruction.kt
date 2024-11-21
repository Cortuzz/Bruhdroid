package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class ReturnInstruction(expression: String = ""):
    Instruction(BlockInstruction.RETURN, expression) {

    override fun evaluate(): Boolean {
        val value = interpreter.parseRawBlock(expression)
        val funcName =  interpreter.currentFunction.removeLast()
        val varName =  interpreter.functionsVarsMap[funcName]!!.removeLast()
        interpreter.currentLine =  interpreter.funcVarsLines.removeLast()

        interpreter.removeFunctionMemory()
        interpreter.memory.pushToLocalMemory(varName, value)

        return false
    }
}