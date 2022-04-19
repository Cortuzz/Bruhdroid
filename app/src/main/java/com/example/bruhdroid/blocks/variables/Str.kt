package com.example.bruhdroid.blocks.variables

import com.example.bruhdroid.blocks.Block
import com.example.bruhdroid.Instruction
import com.example.bruhdroid.blocks.Variable

data class Str(var value: String) {
     operator fun plus(operand: Str): Str {
        return Str(value=value + operand.value)
    }

     operator fun times(operand: Integer): Str {
        return Str(value=value.repeat(operand.value))
    }

     operator fun plus(operand: Integer): Integer {
        throw Exception()
    }

     operator fun minus(operand: Integer): Integer {
        throw Exception()
    }

     operator fun times(operand: Str): Str {
        throw Exception()
    }

     operator fun div(operand: Integer): Integer {
        throw Exception()
    }

     operator fun rem(operand: Integer): Integer {
        throw Exception()
    }
}
