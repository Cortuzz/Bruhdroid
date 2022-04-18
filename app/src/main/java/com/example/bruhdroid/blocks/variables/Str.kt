package com.example.bruhdroid.blocks.variables

import com.example.bruhdroid.blocks.Block
import com.example.bruhdroid.Instruction
import com.example.bruhdroid.blocks.Variable

data class Str(override val name: String = "", var value: String) :
    Variable(name) {

    override operator fun plus(operand: Str): Str {
        return Str(value=value + operand.value)
    }

    override operator fun times(operand: Integer): Str {
        return Str(value=value.repeat(operand.value))
    }

    override operator fun plus(operand: Integer): Integer {
        throw Exception()
    }

    override operator fun minus(operand: Integer): Integer {
        throw Exception()
    }

    override operator fun times(operand: Str): Str {
        throw Exception()
    }

    override operator fun div(operand: Integer): Integer {
        throw Exception()
    }

    override operator fun rem(operand: Integer): Integer {
        throw Exception()
    }
}
