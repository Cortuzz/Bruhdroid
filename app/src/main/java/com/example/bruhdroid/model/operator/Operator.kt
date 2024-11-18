package com.example.bruhdroid.model.operator

class Operator (
    val operator: String,
    val priority: Int,
    val inputOperator: String,
    val unary: Boolean = false,
) { }