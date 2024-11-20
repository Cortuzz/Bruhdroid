package com.example.bruhdroid.model.blocks.valuable

import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.exception.TypeError
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable

class ListValuable(
    varValue: Any,
): Valuable(varValue, ValuableType.LIST) {
    override fun clone(): Valuable {
        val valuable = ListValuable(value)
        valuable.array = array
        return valuable
    }

    override fun getLength(): Valuable {
        return IntegerValuable(array.size)
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
        val valuable = ListValuable(value)
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