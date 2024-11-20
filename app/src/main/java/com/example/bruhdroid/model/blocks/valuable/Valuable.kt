package com.example.bruhdroid.model.blocks.valuable

import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.exception.TypeError
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.IDataPresenter

abstract class Valuable protected constructor(
    varValue: Any,
    var type: ValuableType,
    var listLink: ListValuable?
) :
    Block(BlockInstruction.VAL, ""), IDataPresenter {
    var value: String = varValue.toString()
    open var array: MutableList<Valuable> = mutableListOf()

    abstract fun clone(): Valuable

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
        throw TypeError("Addition can not be applied to types $type and ${operand.type}")
    }

    open operator fun minus(operand: Valuable): Valuable {
        throw TypeError("Subtraction can not be applied to types $type and ${operand.type}")
    }

    open operator fun times(operand: Valuable): Valuable {
        throw TypeError("Multiplication can not be applied to types $type and ${operand.type}")
    }

    open operator fun div(operand: Valuable): Valuable {
        throw TypeError("Division can not be applied to types $type and ${operand.type}")
    }

    open fun intDiv(operand: Valuable): Valuable {
        throw TypeError("Integer division can not be applied to types $type and ${operand.type}")
    }

    open operator fun rem(operand: Valuable): Valuable {
        throw TypeError("Reminder can not be applied to types $type and ${operand.type}")
    }

    open operator fun compareTo(operand: Valuable): Int {
        throw TypeError("Comparing can not be applied to types $type and ${operand.type}")
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

        return BooleanValuable(value1 && value2)
    }

    fun or(operand: Valuable): Valuable {
        val value1 = convertToBool(operand)
        val value2 = convertToBool(this)

        return BooleanValuable(value1 || value2)
    }

    open fun convertToBool(valuable: Valuable): Boolean {
        return false
    }

    open fun convertToFloat(valuable: Valuable): Float {
        throw TypeError("Type ${valuable.type} can not be convereted to float")
    }

    open fun convertToString(valuable: Valuable): String {
        return valuable.value
    }

    open fun convertToArray(valuable: Valuable): List<Valuable> {
        throw TypeError("Type ${valuable.type} can not be convreted to array")
    }

    open fun convertToInt(valuable: Valuable): Int {
        throw TypeError("Type ${valuable.type} can not be convreted to int")
    }

    open fun absolute(): Valuable {
        throw TypeError("Abs can not be applied to type $type")
    }

    open fun exponent(): Valuable {
        throw TypeError("Exp can not be applied to type $type")
    }

    open fun ceil(): Valuable {
        throw TypeError("Ceil can not be applied to type $type")
    }

    open fun floor(): Valuable {
        throw TypeError("Floor can not be applied to type $type")
    }

    open fun sorted(): Valuable {
        throw TypeError("Sorted can not be applied to type $type")
    }

    open fun sort(): Valuable {
        throw TypeError("Sort can not be applied to type $type")
    }
}
