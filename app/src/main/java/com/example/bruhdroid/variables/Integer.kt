package com.example.bruhdroid.variables

data class Integer(val name: String, var value: Int) {
    operator fun plus(increment: Integer): Int {
        return increment.value + value
    }

    operator fun minus(op: Integer): Int {
        return value - op.value
    }

    operator fun times(op: Integer): Int {
        return op.value * value
    }

    operator fun times(op: Str): String {
        return op.value.repeat(value)
    }

    operator fun div(op: Integer): Int {
        return value / op.value
    }

    operator fun rem(op: Integer): Int {
        return value % op.value
    }
}
