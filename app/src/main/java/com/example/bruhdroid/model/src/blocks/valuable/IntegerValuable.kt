package com.example.bruhdroid.model.src.blocks.valuable

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import kotlin.math.abs
import kotlin.math.exp

class IntegerValuable(
    varValue: Any,
): Valuable(varValue, Type.INT) {
    override operator fun unaryMinus(): Valuable {
        return Valuable(-value.toInt(), type)
    }

    override operator fun plus(operand: Valuable): Valuable {
        if (operand.type !in listOf(Type.INT, Type.FLOAT)) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return Valuable(value.toFloat() + operand.value.toFloat(), Type.FLOAT)
        }
        return Valuable(value.toInt() + operand.value.toInt(), Type.INT)
    }

    override operator fun minus(operand: Valuable): Valuable {
        if (operand.type !in listOf(Type.INT, Type.FLOAT)) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return Valuable(value.toFloat() - operand.value.toFloat(), Type.FLOAT)
        }
        return Valuable(value.toInt() - operand.value.toInt(), Type.INT)
    }

    override operator fun times(operand: Valuable): Valuable {
        if (operand.type == Type.STRING) {
            return Valuable(operand.value.repeat(value.toInt()), operand.type)
        }

        if (operand.type !in listOf(Type.INT, Type.FLOAT)) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        if (checkFloating(this, operand)) {
            return Valuable(value.toFloat() * operand.value.toFloat(), Type.FLOAT)
        }
        return Valuable(value.toInt() * operand.value.toInt(), Type.INT)
    }

    override operator fun div(operand: Valuable): Valuable {
        if (operand.type !in listOf(Type.INT, Type.FLOAT)) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        return Valuable(value.toFloat() / operand.value.toFloat(), Type.FLOAT)
    }

    override fun intDiv(operand: Valuable): Valuable {
        if (operand.type !in listOf(Type.INT, Type.FLOAT)) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        return Valuable((value.toFloat() / operand.value.toFloat()).toInt(), Type.INT)
    }

    override operator fun rem(operand: Valuable): Valuable {
        if (operand.type !in listOf(Type.INT, Type.FLOAT)) {
            throw TypeError("Expected $type but found ${operand.type}")
        }

        val value = value.toFloat() / operand.value.toFloat()
        return if (value - kotlin.math.floor(value) == 0f) {
            Valuable(value.toInt(), Type.INT)
        } else {
            Valuable(value, Type.FLOAT)
        }
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.value.toInt() != 0
    }

    override fun convertToFloat(valuable: Valuable): Float {
        return valuable.value.toFloat()
    }

    override fun convertToString(valuable: Valuable): String {
        return valuable.value
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