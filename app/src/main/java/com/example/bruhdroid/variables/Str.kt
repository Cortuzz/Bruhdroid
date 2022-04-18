package com.example.bruhdroid.variables

data class Str(val name: String, var value: String) {
    operator fun plus(op: Str): String {
        return value + op.value
    }

    operator fun times(op: Integer): String {
        return value.repeat(op.value)
    }
}
