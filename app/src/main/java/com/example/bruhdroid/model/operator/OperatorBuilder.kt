package com.example.bruhdroid.model.operator

open class OperatorBuilder(
    private val operator: String,
    private val priority: Int,
    private val inputOperatorMatches: List<String>,
    private val unary: Boolean = false,
    private val unaryChange: Boolean = true
) {
    open fun evaluate(operationStack: MutableList<Operator>): MutableList<Operator> {
        return operationStack
    }

    fun match(inputOperator: String, mayUnary: Boolean): Boolean {
        if (unary && !mayUnary)
            return false
        if (inputOperatorMatches.contains(inputOperator) && unary)
            return true

        return inputOperatorMatches.contains(inputOperator)
    }

    fun buildOperator(inputOperator: String, mayUnary: Boolean): Operator {
        if (!match(inputOperator, mayUnary))
            throw Exception("пупупу")

        return Operator(operator, priority, inputOperator, unary)
    }
}