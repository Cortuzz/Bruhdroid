package com.example.bruhdroid.model.src.blocks.valuable.numeric

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import kotlin.math.abs
import kotlin.math.exp

class IntegerValuable(
    varValue: Any,
): NumericValuable(varValue, Type.INT) {
    override operator fun unaryMinus(): Valuable {
        return Valuable(-value.toInt(), type)
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand.type == Type.STRING) {
            return Valuable(operand.value.repeat(value.toInt()), operand.type)
        }

        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return Valuable(value.toFloat() * operand.value.toFloat(), Type.FLOAT)
        }
        return Valuable(value.toInt() * operand.value.toInt(), Type.INT)
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
        return Valuable(abs(value.toInt()), type)
    }

    override fun exponent(): Valuable {
        return Valuable(exp(value.toFloat()), Type.FLOAT)
    }

    override fun ceil(): Valuable {
        return Valuable(kotlin.math.ceil(value.toFloat()), Type.INT)
    }

    override fun floor(): Valuable {
        return Valuable(kotlin.math.floor(value.toFloat()), Type.INT)
    }
}