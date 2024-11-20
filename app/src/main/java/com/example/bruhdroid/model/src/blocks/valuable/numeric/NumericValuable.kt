package com.example.bruhdroid.model.src.blocks.valuable.numeric

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import kotlin.math.abs
import kotlin.math.exp

abstract class NumericValuable(
    varValue: Any,
    type: Type
): Valuable(varValue, type) {
    override operator fun plus(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return FloatValuable(value.toFloat() + operand.value.toFloat())
        }
        return IntegerValuable(value.toInt() + operand.value.toInt())
    }

    override operator fun minus(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return FloatValuable(value.toFloat() - operand.value.toFloat())
        }
        return IntegerValuable(value.toInt() - operand.value.toInt())
    }

    override operator fun div(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        return FloatValuable(value.toFloat() / operand.value.toFloat())
    }

    override fun intDiv(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        return IntegerValuable((value.toFloat() / operand.value.toFloat()).toInt())
    }

    override operator fun rem(operand: Valuable): Valuable {
        if (operand !is NumericValuable) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        val value = value.toFloat() % operand.value.toFloat()
        return if (value - kotlin.math.floor(value) == 0f) {
            IntegerValuable(value.toInt())
        } else {
            FloatValuable(value)
        }
    }

    override operator fun compareTo(operand: Valuable): Int {
        val dif = value.toFloat() - operand.value.toFloat()

        return if (dif < 0) {
            -1
        } else if (dif > 0) {
            1
        } else {
            0
        }
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.value.toFloat() != 0f
    }

    override fun convertToFloat(valuable: Valuable): Float {
        return valuable.value.toFloat()
    }

    override fun convertToString(valuable: Valuable): String {
        return valuable.value
    }

    override fun convertToInt(valuable: Valuable): Int {
        return valuable.value.toFloat().toInt()
    }

    override fun exponent(): Valuable {
        return FloatValuable(exp(value.toFloat()))
    }

    override fun ceil(): Valuable {
        return IntegerValuable(kotlin.math.ceil(value.toFloat()))
    }

    override fun floor(): Valuable {
        return IntegerValuable(kotlin.math.floor(value.toFloat()))
    }

    protected fun checkFloating(val1: Valuable, val2: Valuable): Boolean {
        if (val1.type == Type.FLOAT || val2.type == Type.FLOAT) {
            return true
        }
        return false
    }
}