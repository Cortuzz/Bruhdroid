package com.example.bruhdroid.model.src.blocks.valuable

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.valuable.numeric.FloatValuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.IntegerValuable

class StringValuable(
    varValue: Any,
): Valuable(varValue, Type.STRING) {
    override fun clone(): Valuable {
        return StringValuable(value)
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
        if (operand.type == Type.STRING) {
            return StringValuable(value + operand.value)
        }

        throw TypeError("Expected $type but found ${operand.type}")
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand.type == Type.INT) {
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