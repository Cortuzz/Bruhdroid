package com.example.bruhdroid.model.src.blocks.valuable.numeric

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.valuable.StringValuable
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import kotlin.math.abs
import kotlin.math.exp

class IntegerValuable(
    varValue: Any,
): NumericValuable(varValue, Type.INT) {
    override fun clone(): Valuable {
        return IntegerValuable(value)
    }

    override operator fun unaryMinus(): Valuable {
        return IntegerValuable(-value.toInt())
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand.type == Type.STRING) {
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