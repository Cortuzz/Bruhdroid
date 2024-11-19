package com.example.bruhdroid.model.operation.operator.builder

open class AggregateOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>,
    unaryChange: Boolean,
): OperatorBuilder(operator, priority, inputOperator, { _, _ -> null }, false, unaryChange) {
    override fun match(inputOperator: String, mayUnary: Boolean): Boolean {
        return inputOperatorMatches.contains(inputOperator)
    }
}