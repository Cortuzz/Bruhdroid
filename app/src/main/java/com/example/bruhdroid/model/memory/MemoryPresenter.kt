package com.example.bruhdroid.model.memory

import com.example.bruhdroid.model.blocks.valuable.*

class MemoryPresenter {
    fun getVisibleValue(valuable: Valuable): String {
        return valuable.getVisibleValue()
    }

    fun getMemoryData(memory: Memory): String {
        val data = parseStack(memory).ifEmpty { "EMPTY" }

        if (memory.prevMemory == null) {
            return "${memory.scope}: $data"
        }
        return "${memory.scope}: $data\n\n${getMemoryData(memory.prevMemory)}"
    }

    private fun parseStack(memory: Memory): String {
        var data = ""
        for (pair in memory.getAllVariables()) {
            data += "\n${pair.key} = ${getVisibleValue(pair.value)}: ${pair.value.type}"
        }
        return data
    }
}