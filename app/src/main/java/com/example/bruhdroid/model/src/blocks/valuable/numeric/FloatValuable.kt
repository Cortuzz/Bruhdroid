package com.example.bruhdroid.model.src.blocks.valuable.numeric

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import kotlin.math.abs
import kotlin.math.exp

open class FloatValuable(
    varValue: Any,
): NumericValuable(varValue, Type.FLOAT) {
    override fun clone(): Valuable {
        return FloatValuable(value)
    }

    override operator fun unaryMinus(): Valuable {
        return FloatValuable(-value.toFloat())
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return FloatValuable(value.toFloat() * operand.value.toFloat())
        }
        return IntegerValuable(value.toInt() * operand.value.toInt())
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
        return FloatValuable(abs(value.toFloat()))
    }
}