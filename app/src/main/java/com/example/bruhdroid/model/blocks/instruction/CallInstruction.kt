package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction

class CallInstruction(expression: String = ""):
    Instruction(BlockInstruction.FUNC_CALL, expression) {

    override fun evaluate(interpreter: Interpreter) {
        val exp = expression.split("=").toMutableList()

        val name = exp.removeFirst().replace(" ", "")
        val data = interpreter.parseFunc(exp.joinToString())

        if (exp.isEmpty()) {
            val parsed = interpreter.parseFunc(name)
            val parsedName = parsed["name"]!![0]
            interpreter.args.add(parsed["args"]!!)

            interpreter.funcVarsLines.add(interpreter.currentLine)
            interpreter.currentFunction.add(parsedName)
            interpreter.currentLine = interpreter.functionLines[parsedName]!!.removeLast() - 1
        } else {
            val funcName = data["name"]!![0]
            interpreter.args.add(data["args"]!!)

            interpreter.funcVarsLines.add(interpreter.currentLine)
            if (funcName in interpreter.functionsVarsMap) {
                interpreter.functionsVarsMap[funcName]!!.add(name)
            } else {
                interpreter.functionsVarsMap[funcName] = mutableListOf(name)
            }

            interpreter.currentFunction.add(funcName)
            interpreter.currentLine = interpreter.functionLines[funcName]!!.removeLast() - 1
        }
    }

    override fun clone(): CallInstruction {
        return CallInstruction(expression)
    }
}