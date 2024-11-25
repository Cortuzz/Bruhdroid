package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class ReturnInstruction(expression: String = ""):
    Instruction(BlockInstruction.RETURN, expression) {

    override fun evaluate(interpreter: Interpreter) {
        val value = interpreter.parseRawBlock(expression)
        val funcName =  interpreter.currentFunction.removeLast()
        val varName =  interpreter.functionsVarsMap[funcName]!!.removeLast()
        interpreter.currentLine =  interpreter.funcVarsLines.removeLast()

        interpreter.memory = interpreter.memory.exitMemoryFunctionStack()
        interpreter.memory.pushToLocalMemory(varName, value)
    }

    override fun clone(): ReturnInstruction {
        return ReturnInstruction(expression)
    }
}