package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError

class Valuable(varValue: Any, var type: Type) :
    Block(Instruction.VAL,null, null) {
    var value: String = varValue.toString()

    operator fun unaryPlus(): Valuable {
        if (type == Type.STRING) {
            if (value.contains('.')) {
                return Valuable(convertToFloat(this), Type.FLOAT)
            }
            return Valuable(convertToInt(this), Type.INT)
        }
        if (type == Type.UNDEFINED) {
            throw TypeError("Unary plus can't be applied to type $type")
        }
        return this
    }

    operator fun unaryMinus(): Valuable {
        if (type == Type.INT) {
            return Valuable(-value.toInt(), type)
        }
        if (type == Type.FLOAT) {
            return Valuable(-value.toFloat(), type)
        }
        throw TypeError("Unary minus can't be applied to type $type")
    }

    operator fun plus(operand: Valuable): Valuable {
        if (type == Type.STRING && operand.type == Type.STRING) {
            return Valuable(value + operand.value, type)
        }
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types && operand.type in types) {
            if (checkFloating(this, operand)) {
                return Valuable(value.toFloat() + operand.value.toFloat(), Type.FLOAT)
            }
            return Valuable(value.toInt() + operand.value.toInt(), Type.INT)
        }
        throw TypeError("Expected $type but found ${operand.type}")
    }

    operator fun minus(operand: Valuable): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types && operand.type in types) {
            if (checkFloating(this, operand)) {
                return Valuable(value.toFloat() - operand.value.toFloat(), Type.FLOAT)
            }
            return Valuable(value.toInt() - operand.value.toInt(), Type.INT)
        }
        throw TypeError("Expected INT or FLOAT but found STRING")
    }

    operator fun times(operand: Valuable): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types && operand.type in types) {
            if (checkFloating(this, operand)) {
                return Valuable(value.toFloat() * operand.value.toFloat(), Type.FLOAT)
            }
            return Valuable(value.toInt() * operand.value.toInt(), Type.INT)
        }
        if (type == Type.INT && operand.type == Type.STRING) {
            return Valuable(operand.value.repeat(value.toInt()), operand.type)
        }
        if (type == Type.STRING && operand.type == Type.INT) {
            return Valuable(value.repeat(operand.value.toInt()), type)
        }

        if (type != Type.STRING) {
            throw TypeError("Expected INT but found ${operand.type}")
        }
        throw TypeError("Expected INT but found $type")
    }

    operator fun div(operand: Valuable): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types && operand.type in types) {
                if (checkFloating(this, operand)) {
                    return Valuable(value.toFloat() / operand.value.toFloat(), Type.FLOAT)
                }
            return Valuable(value.toInt() / operand.value.toInt(), Type.INT)
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    operator fun rem(operand: Valuable): Valuable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Valuable(value.toInt() % operand.value.toInt(), type)
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    operator fun compareTo(operand: Valuable): Int {
        return value.toInt() - operand.value.toInt()
    }

    fun and(operand: Valuable): Valuable {
        val value1 = convertToBool(operand)
        val value2 = convertToBool(this)

        return Valuable(value1 && value2, Type.BOOL)
    }

    fun or(operand: Valuable): Valuable {
        val value1 = convertToBool(operand)
        val value2 = convertToBool(this)

        return Valuable(value1 || value2, Type.BOOL)
    }

    private fun convertToBool(valuable: Valuable): Boolean {
        return when (valuable.type) {
            Type.BOOL -> valuable.value.toBoolean()
            Type.INT -> valuable.value.toInt() != 0
            Type.FLOAT -> valuable.value.toFloat() != 0f
            Type.STRING -> valuable.value != ""
            else -> throw TypeError("Bad type")
        }
    }

    private fun convertToFloat(valuable: Valuable): Float {
        return when (valuable.type) {
            //Type.BOOL -> valuable.value.toBoolean() todo: Boolean
            Type.INT -> valuable.value.toFloat()
            Type.FLOAT -> valuable.value.toFloat()
            Type.STRING -> {
                try {
                    valuable.value.toFloat()
                } catch (e: Exception) {
                    throw TypeError("Expected number-containing string but ${valuable.value} was found")
                }
            }
            else -> throw TypeError("Bad type")
        }
    }

    private fun convertToInt(valuable: Valuable): Int {
        return when (valuable.type) {
            //Type.BOOL -> valuable.value.toBoolean() todo: Boolean
            Type.INT -> valuable.value.toInt()
            Type.FLOAT -> valuable.value.toInt()
            Type.STRING -> {
                try {
                    valuable.value.toInt()
                } catch (e: Exception) {
                    throw TypeError("Expected number-containing string but ${valuable.value} was found")
                }
            }
            else -> throw TypeError("Bad type")
        }
    }

    private fun checkFloating(val1: Valuable, val2: Valuable): Boolean {
        if (val1.type == Type.FLOAT || val2.type == Type.FLOAT) {
            return true
        }
        return false
    }
}
