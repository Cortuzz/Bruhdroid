package com.example.bruhdroid.model.blocks.instruction

import com.example.bruhdroid.model.blocks.BlockInstruction

class PrintInstruction(expression: String = ""):
    Instruction(BlockInstruction.PRINT, expression) {

    override fun evaluate(): Boolean {
        val rawList = interpreter.split(expression)
        if (interpreter.pragma["IO_MESSAGE"] == "true") {
            interpreter.output += "I/O: "
        }

        for (raw in rawList) {
            interpreter.output +=
                "${interpreter.memoryPresenter.getVisibleValue(interpreter.parseRawBlock(raw))} "
        }
        interpreter.increaseIoLines()
        interpreter.output += "\n"
        val lines = interpreter.pragma["IO_LINES"]
        if (lines != null) {
            if (lines != "inf" && interpreter.ioLines > lines.toInt()) {
                interpreter.decreaseIoLines()
                val ind = interpreter.output.indexOf("\n")
                interpreter.output = interpreter.output.substring(ind + 1)
            }
        }
        interpreter.notifyClients()
        return false
    }

    override fun clone(): PrintInstruction {
        return PrintInstruction(expression)
    }
}