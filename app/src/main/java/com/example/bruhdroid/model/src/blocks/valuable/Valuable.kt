package com.example.bruhdroid.model.src.blocks.valuable

import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError
import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.blocks.IDataPresenter
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.exp

open class Valuable(varValue: Any, var type: Type) :
    Block(Instruction.VAL, ""), IDataPresenter {
    var value: String = varValue.toString()
    var array: MutableList<Valuable> = mutableListOf()

    fun clone(): Valuable {
        val valuable = Valuable(value, type)
        valuable.array = array
        return valuable
    }

    override fun getData(): Valuable {
        return this
    }

    override fun tryGetData(): Valuable {
        return getData()
    }

    open operator fun unaryPlus(): Valuable {
        return this
    }

    open fun getLength(): Valuable {
        throw TypeError("len() be applied to type $type")
    }

    open operator fun unaryMinus(): Valuable {
        throw TypeError("Unary minus can't be applied to type $type")
    }

    open operator fun plus(operand: Valuable): Valuable {
        throw TypeError("Expected $type but found ${operand.type}")
    }

    open operator fun minus(operand: Valuable): Valuable {
        throw TypeError("Expected $type but found ${operand.type}")
    }

    open operator fun times(operand: Valuable): Valuable {
        throw TypeError("Expected INT but found $type")
    }

    open operator fun div(operand: Valuable): Valuable {
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    open fun intDiv(operand: Valuable): Valuable {
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    open operator fun rem(operand: Valuable): Valuable {
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    operator fun compareTo(operand: Valuable): Int {
        val dif = try {
            value.toFloat() - operand.value.toFloat()
        } catch (e: Exception) {
            throw TypeError("Expected INT or FLOAT but found $type and ${operand.type}")
        }
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

    open fun convertToBool(valuable: Valuable): Boolean {
        return false
    }

    open fun convertToFloat(valuable: Valuable): Float {
        throw TypeError("Expected convertible to FLOAT type but ${valuable.type} was found")
    }

    open fun convertToString(valuable: Valuable): String {
        return valuable.value
    }

    open fun convertToArray(valuable: Valuable): List<Valuable> {
        throw TypeError("Expected type STRING but ${valuable.type} was found")
    }

    open fun convertToInt(valuable: Valuable): Int {
        throw TypeError("Expected convertible to INT type but ${valuable.type} was found")
    }

    open fun absolute(): Valuable {
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    open fun exponent(): Valuable {
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    open fun ceil(): Valuable {
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    open fun floor(): Valuable {
        throw TypeError("Expected INT or FLOAT but found $type")
    }

    open fun sorted(): Valuable {
        throw TypeError("Expected LIST but found $type")
    }

    open fun sort(): Valuable {
        throw TypeError("Expected LIST but found $type")
    }

    protected fun checkFloating(val1: Valuable, val2: Valuable): Boolean {
        if (val1.type == Type.FLOAT || val2.type == Type.FLOAT) {
            return true
        }
        return false
    }
}
