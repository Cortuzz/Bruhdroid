package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.memory.Memory

class FuncInstruction(expression: String = ""):
    Instruction(BlockInstruction.FUNC, expression) {

    override fun evaluate(): Boolean {
        val name = interpreter.parseFunc(expression)["name"]?.get(0)
        val argNames = interpreter.parseFunc(expression)["args"]!!
        interpreter.memory = Memory(interpreter.memory, "METHOD $name SCOPE")

        if (name in interpreter.functionLines) {
            interpreter.functionLines[name]!!.add(interpreter.currentLine)
        } else {
            interpreter.functionLines[name!!] = mutableListOf(interpreter.currentLine)
        }

        if (interpreter.currentFunction.last() != name) {
            interpreter.skipFunc()
            return false
        }
        val args = interpreter.args.removeLast()
        for (i in args.indices) {
            val value = args[i]
            val arg = argNames[i]
            interpreter.parseRawBlock("$arg = $value", true)
        }
        return false
    }
}