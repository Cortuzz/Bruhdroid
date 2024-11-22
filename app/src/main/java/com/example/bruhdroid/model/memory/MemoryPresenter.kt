package com.example.bruhdroid.model.memory

import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.model.blocks.valuable.Valuable

class MemoryPresenter {
    fun getVisibleValue(valuable: Valuable): String {
        val rawValue = valuable.value

        return when (valuable.type) {
            ValuableType.STRING -> "\"$rawValue\""
            ValuableType.BOOL -> rawValue.uppercase()
            ValuableType.UNDEFINED -> "NULL"
            ValuableType.LIST -> {
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