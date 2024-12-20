package com.example.bruhdroid.model.blocks.valuable

import com.example.bruhdroid.exception.TypeError
import com.example.bruhdroid.model.blocks.valuable.numeric.FloatValuable
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable

class StringValuable(
    varValue: Any,
    listLink: ListValuable? = null
): Valuable(varValue, ValuableType.STRING, listLink) {
    override fun getVisibleValue(): String {
        return "\"$value\""
    }

    override fun clone(): Valuable {
        return StringValuable(value, listLink)
    }

    override operator fun unaryPlus(): Valuable {
        if (value.contains('.')) {
            return FloatValuable(convertToFloat(this))
        }
        return IntegerValuable(convertToInt(this))
    }

    override fun getLength(): Valuable {
        return IntegerValuable(value.length)
    }

    override operator fun plus(operand: Valuable): Valuable {
        if (operand is StringValuable) {
            return StringValuable(value + operand.value)
        }

        throw TypeError("Expected $type but found ${operand.type}")
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand is IntegerValuable) {
            return StringValuable(value.repeat(operand.value.toInt()))
        }

        throw TypeError("Cannot multiply $type and ${operand.type}")
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.value != ""
    }

    override fun convertToFloat(valuable: Valuable): Float {
        try {
            return valuable.value.toFloat()
        } catch (e: Exception) {
            throw TypeError("Expected number-containing string but ${valuable.value} was found")
        }
    }

    override fun convertToArray(valuable: Valuable): List<Valuable> {
        val arr = valuable.value.toList()
        val valArr = mutableListOf<Valuable>()
        for (value in arr) {
            valArr.add(StringValuable(value))
        }
        return valArr
    }

    override fun convertToInt(valuable: Valuable): Int {
        try {
            return valuable.value.toFloat().toInt()
        } catch (e: Exception) {
            throw TypeError("Expected number-containing string but ${valuable.value} was found")
        }
    }

    override fun convertToString(valuable: Valuable): String {
        return value
    }
}