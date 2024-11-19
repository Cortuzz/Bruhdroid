package com.example.bruhdroid.model.operation

import com.example.bruhdroid.model.src.blocks.Valuable

abstract class Operation(
    val operation: String
) {
    abstract fun act(operand1: Valuable, operand2: Valuable?): Valuable?
}