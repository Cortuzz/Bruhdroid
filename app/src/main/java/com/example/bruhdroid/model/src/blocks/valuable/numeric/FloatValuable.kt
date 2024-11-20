package com.example.bruhdroid.model.src.blocks.valuable.numeric

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import kotlin.math.abs
import kotlin.math.exp

open class FloatValuable(
    varValue: Any,
): NumericValuable(varValue, Type.FLOAT) {
    override operator fun unaryMinus(): Valuable {
        return Valuable(-value.toFloat(), type)
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return Valuable(value.toFloat() * operand.value.toFloat(), Type.FLOAT)
        }
        return Valuable(value.toInt() * operand.value.toInt(), Type.INT)
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.value.toFloat() != 0f
    }

    override fun convertToFloat(valuable: Valuable): Float {
        return valuable.value.toFloat()
    }

    override fun convertToInt(valuable: Valuable): Int {
        return valuable.value.toFloat().toInt()
    }

    override fun absolute(): Valuable {
        return Valuable(abs(value.toFloat()), type)
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