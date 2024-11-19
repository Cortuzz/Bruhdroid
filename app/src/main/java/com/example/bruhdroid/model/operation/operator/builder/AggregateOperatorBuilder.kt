package com.example.bruhdroid.model.operation.operator.builder

open class AggregateOperatorBuilder(
    operator: String,
    priority: Int,
    unaryChange: Boolean,
): OperatorBuilder(
    operator,
    priority,
    action =  { _, _ -> null },
    unary = false,
    unaryChange =  unaryChange
) {
    override fun match(inputOperator: String, mayUnary: Boolean): Boolean {
        return this.inputOperator == inputOperator
    }
}