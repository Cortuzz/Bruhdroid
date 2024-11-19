package com.example.bruhdroid.model.memory

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.valuable.Valuable

class MemoryPresentor {
    fun getVisibleValue(valuable: Valuable): String {
        val rawValue = valuable.value

        return when (valuable.type) {
            Type.STRING -> "\"$rawValue\""
            Type.BOOL -> rawValue.uppercase()
            Type.UNDEFINED -> "NULL"
            Type.LIST -> {
                val str = mutableListOf<String>()
                valuable.array.forEach { el -> str.add(getVisibleValue(el)) }
                str.toString()
            }
            else -> rawValue
        }
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