package com.example.bruhdroid.model.src.blocks.valuable

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError

class ListValuable(
    varValue: Any,
): Valuable(varValue, Type.LIST) {
    override fun getLength(): Valuable {
        return Valuable(array.size, Type.INT)
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.array.isNotEmpty()
    }

    override fun convertToString(valuable: Valuable): String {
        return valuable.array.toString()
    }

    override fun convertToArray(valuable: Valuable): List<Valuable> {
        return array
    }

    override fun sorted(): Valuable {
        val valuable = Valuable(value, type)
        valuable.array = srt()
        return valuable
    }

    override fun sort(): Valuable {
        array = srt()
        return this
    }


    private fun srt(): MutableList<Valuable> {
        return array.sortedBy { i ->
            try {
                i.value.toFloat()
            } catch (e: Exception) {
                throw TypeError("Expected INT or FLOAT but found ${i.type}")
            }
        }.toMutableList()
    }
}