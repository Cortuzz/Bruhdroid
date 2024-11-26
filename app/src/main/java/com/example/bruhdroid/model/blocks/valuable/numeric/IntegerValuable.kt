package com.example.bruhdroid.model.blocks.valuable.numeric

import com.example.bruhdroid.model.blocks.valuable.ValuableType
import com.example.bruhdroid.exception.TypeError
import com.example.bruhdroid.model.blocks.valuable.ListValuable
import com.example.bruhdroid.model.blocks.valuable.StringValuable
import com.example.bruhdroid.model.blocks.valuable.Valuable
import kotlin.math.abs

class IntegerValuable(
    varValue: Any,
    listLink: ListValuable? = null
): NumericValuable(varValue, ValuableType.INT, listLink) {
    override fun clone(): Valuable {
        return IntegerValuable(value, listLink)
    }

    override operator fun unaryMinus(): Valuable {
        return IntegerValuable(-value.toInt())
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand is StringValuable) {
            return StringValuable(operand.value.repeat(value.toInt()))
        }

        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return FloatValuable(value.toFloat() * operand.value.toFloat())
        }
        return IntegerValuable(value.toInt() * operand.value.toInt())
    }

    override operator fun div(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (operand is IntegerValuable) {
            return IntegerValuable((value.toFloat() / operand.value.toFloat()).toInt())
        }

        return FloatValuable(value.toFloat() / operand.value.toFloat())
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.value.toInt() != 0
    }

    override fun convertToFloat(valuable: Valuable): Float {
        return valuable.value.toFloat()
    }

    override fun convertToInt(valuable: Valuable): Int {
        return valuable.value.toInt()
    }

    override fun absolute(): Valuable {
        return IntegerValuable(abs(value.toInt()))
    }
}