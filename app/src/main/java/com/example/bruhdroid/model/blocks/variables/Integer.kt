package com.example.bruhdroid.model.blocks.variables

data class Integer(var value: Int) {
     operator fun plus(operand: Integer): Integer {
        return Integer(value=value + operand.value)
    }

     operator fun minus(operand: Integer): Integer {
        return Integer(value=value - operand.value)
    }

     operator fun times(operand: Integer): Integer {
        return Integer(value=value * operand.value)
    }

     operator fun times(operand: Str): Str {
        return Str(value=operand.value.repeat(value))
    }

     operator fun div(operand: Integer): Integer {
        return Integer(value=value / operand.value)
    }

     operator fun rem(operand: Integer): Integer {
        return Integer(value=value % operand.value)
    }

     operator fun plus(operand: Str): Str {
        throw Exception()
    }
}
