package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.exp

class Valuable(varValue: Any, var type: Type) :
    Block(Instruction.VAL,"") {
    var value: String = varValue.toString()
    var array: MutableList<Valuable> = mutableListOf()

    fun clone(): Valuable {
        val valuable = Valuable(value, type)
        valuable.array = array
        return valuable
    }

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
            return Valuable(value.toFloat() / operand.value.toFloat(), Type.FLOAT)
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    fun intDiv(operand: Valuable): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types && operand.type in types) {
            return Valuable((value.toFloat() / operand.value.toFloat()).toInt(), Type.INT)
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    operator fun rem(operand: Valuable): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types && operand.type in types) {
            val value = value.toFloat() / operand.value.toFloat()
            return if (value - floor(value) == 0f) {
                Valuable(value.toInt(), Type.INT)
            } else {
                Valuable(value, Type.FLOAT)
            }
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    operator fun compareTo(operand: Valuable): Int {
        val dif = try{value.toFloat() - operand.value.toFloat()}
        catch (e: Exception) {throw TypeError("Expected INT or FLOAT but found $type and ${operand.type}")}
        return if (dif < 0) {
            -1
        } else if (dif > 0) {
            1
        } else {
            0
        }
    }

    override fun equals(other: Any?): Boolean {
        other as Valuable
        if (other.type == type && other.value == value) {
            return true
        }
        return false
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

    fun convertToBool(valuable: Valuable): Boolean {
        return when (valuable.type) {
            Type.BOOL -> valuable.value.toBoolean()
            Type.INT -> valuable.value.toInt() != 0
            Type.FLOAT -> valuable.value.toFloat() != 0f
            Type.STRING -> valuable.value != ""
            Type.LIST -> valuable.array.isNotEmpty()
            Type.UNDEFINED -> false
        }
    }

    fun convertToFloat(valuable: Valuable): Float {
        return when (valuable.type) {
            Type.BOOL -> {
                if (valuable.value == "true") {
                    return 1f
                }
                0f
            }
            Type.INT -> valuable.value.toFloat()
            Type.FLOAT -> valuable.value.toFloat()
            Type.STRING -> {
                try {
                    valuable.value.toFloat()
                } catch (e: Exception) {
                    throw TypeError("Expected number-containing string but ${valuable.value} was found")
                }
            }
            else -> throw TypeError("Expected convertible to FLOAT type but ${valuable.type} was found")
        }
    }

    fun convertToInt(valuable: Valuable): Int {
        return when (valuable.type) {
            Type.BOOL -> {
                if (valuable.value == "true") {
                    return 1
                }
                0
            }
            Type.INT -> valuable.value.toInt()
            Type.FLOAT -> valuable.value.toFloat().toInt()
            Type.STRING -> {
                try {
                    valuable.value.toFloat().toInt()
                } catch (e: Exception) {
                    throw TypeError("Expected number-containing string but ${valuable.value} was found")
                }
            }
            else -> throw TypeError("Expected convertible to INT type but ${valuable.type} was found")
        }
    }

    fun absolute(): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types) {
            return if (type == Type.INT) {
                Valuable(abs(value.toInt()), type)
            } else {
                Valuable(abs(value.toFloat()), type)
            }
        }
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    fun exponent(): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types) {
            return Valuable(exp(value.toFloat()), Type.FLOAT)
        }
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    fun ceil(): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types) {
            return Valuable(kotlin.math.ceil(value.toFloat()), Type.INT)
        }
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    fun floor(): Valuable {
        val types = listOf(Type.INT, Type.FLOAT)

        if (type in types) {
            return Valuable(floor(value.toFloat()), Type.INT)
        }
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    private fun srt(): MutableList<Valuable> {
        return array.sortedBy { i ->
            try {
                i.value.toFloat()
            } catch (e: Exception) {
                throw TypeError("Expected INT or FLOAT but found ${i.type}")
            }
        }.toMutableList()
    }

    fun sorted(): Valuable {
        if (type == Type.LIST) {
            val valuable = Valuable(value, type)
            valuable.array = srt()
            return valuable
        }
        throw TypeError("Expected LIST but found $type")
    }

    fun sort(): Valuable {
        if (type == Type.LIST) {
            array = srt()
            return this
        }
        throw TypeError("Expected LIST but found $type")
    }

    private fun checkFloating(val1: Valuable, val2: Valuable): Boolean {
        if (val1.type == Type.FLOAT || val2.type == Type.FLOAT) {
            return true
        }
        return false
    }
}
