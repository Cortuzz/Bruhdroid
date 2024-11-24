package com.example.bruhdroid.model.blocks.instruction.function

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.Instruction
import com.example.bruhdroid.model.memory.Memory

class FuncInstruction(expression: String = ""):
    FunctionInstruction(BlockInstruction.FUNC, expression) {
    override fun funcSkipChange(): Int {
        return 1
    }

    override fun isStartInstruction(): Boolean {
        return true
    }

    override fun evaluate(interpreter: Interpreter) {
        val name = interpreter.parseFunc(expression)["name"]?.get(0)
        val argNames = interpreter.parseFunc(expression)["args"]!!
        interpreter.memory = Memory(interpreter.memory, "METHOD $name SCOPE")

        if (name in interpreter.functionLines) {
            interpreter.functionLines[name]!!.add(interpreter.currentLine)
        } else {
            interpreter.functionLines[name!!] = mutableListOf(interpreter.currentLine)
        }

        if (interpreter.currentFunction.last() != name) {
            skipFunc(interpreter)
            return
        }
        val args = interpreter.args.removeLast()
        for (i in args.indices) {
            val value = args[i]
            val arg = argNames[i]
            interpreter.parseRawBlock("$arg = $value", true)
        }
    }

    override fun clone(): FuncInstruction {
        return FuncInstruction(expression)
    }
}