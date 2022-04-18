package com.example.bruhdroid.blocks.variables

import com.example.bruhdroid.blocks.Block
import com.example.bruhdroid.Instruction
import com.example.bruhdroid.blocks.Variable

data class Integer(override val name: String = "", var value: Int) :
    Variable(name) {

    override operator fun plus(operand: Integer): Integer {
        return Integer(value=value + operand.value)
    }

    override operator fun minus(operand: Integer): Integer {
        return Integer(value=value - operand.value)
    }

    override operator fun times(operand: Integer): Integer {
        return Integer(value=value * operand.value)
    }

    override operator fun times(operand: Str): Str {
        return Str(value=operand.value.repeat(value))
    }

    override operator fun div(operand: Integer): Integer {
        return Integer(value=value / operand.value)
    }

    override operator fun rem(operand: Integer): Integer {
        return Integer(value=value % operand.value)
    }

    override operator fun plus(operand: Str): Str {
        throw Exception()
    }
}
