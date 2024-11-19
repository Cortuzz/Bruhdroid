package com.example.bruhdroid.model.operation.operator

import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.src.blocks.Valuable

class Operator (
    val operator: String,
    val priority: Int,
    val inputOperator: String,
    val unary: Boolean = false,
    private val action: (operand1: Valuable, operand2: Valuable?) -> Valuable?
): Operation(inputOperator) {
    override fun act(operand1: Valuable, operand2: Valuable?): Valuable? {
        return action(operand1, operand2)
    }
}