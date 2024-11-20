package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.operation.operator.Operator
import com.example.bruhdroid.model.operation.operator.AssignOperator
import com.example.bruhdroid.model.blocks.valuable.Valuable
import com.example.bruhdroid.model.blocks.Variable

open class AssignmentOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: String = operator,
    unary: Boolean = false,
    parsedUnary: Boolean = unary,
    private val isArrayInitializer: Boolean = false,
    private val assignment: (operand1: IDataPresenter, operand2: Valuable) -> Valuable,
): OperatorBuilder(
    operator,
    priority,
    inputOperator,
    unary = unary,
    parsedUnary = parsedUnary,
    action = { _, _ -> null }
) {
    override fun build(inputOperator: String, mayUnary: Boolean): Operator {
        if (!match(inputOperator, mayUnary))
            throw Exception("Unable to create operator. Operator does not match signature.")

        return AssignOperator(operator, priority, unary, parsedUnary, isArrayInitializer, assignment)
    }
}