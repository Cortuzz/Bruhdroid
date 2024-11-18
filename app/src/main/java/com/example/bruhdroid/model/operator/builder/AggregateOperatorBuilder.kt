package com.example.bruhdroid.model.operator.builder

open class AggregateOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>,
    unaryChange: Boolean,
): OperatorBuilder(operator, priority, inputOperator, false, unaryChange) {
    override fun match(inputOperator: String, mayUnary: Boolean): Boolean {
        return inputOperatorMatches.contains(inputOperator)
    }
}