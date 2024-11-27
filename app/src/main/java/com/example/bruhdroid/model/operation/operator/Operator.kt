package com.example.bruhdroid.model.operation.operator

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.blocks.valuable.Valuable

open class Operator (
    val operator: String,
    val priority: Int,
    val unary: Boolean = false,
    private val parsedUnary: Boolean = unary,
    private val action: (operand1: Valuable, operand2: Valuable?) -> Valuable?,
): Operation(operator) {
    override fun act(operand1: Valuable, operand2: Valuable?): Valuable? {
        return action(operand1, operand2)
    }

    override fun evaluateExpressionToBlock(currentMemoryScope: Memory): IDataPresenter? {
        return null
    }

    fun getParsedUnary(): Boolean {
        return parsedUnary
    }
}