package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.operation.operator.Operator
import com.example.bruhdroid.model.operation.OperationParseDto
import com.example.bruhdroid.model.operation.operator.AssignOperator
import com.example.bruhdroid.model.src.blocks.Valuable
import com.example.bruhdroid.model.src.blocks.Variable

open class AssignmentOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: String = operator,
    unary: Boolean = false,
    private val isArrayInitializer: Boolean = false,
    private val assignment: (operand1: Variable, operand2: Valuable) -> Valuable,
): OperatorBuilder(
    operator,
    priority,
    inputOperator,
    unary = unary,
    action = { _, _ -> null }
) {
    override fun build(inputOperator: String, mayUnary: Boolean): Operator {
        if (!match(inputOperator, mayUnary))
            throw Exception("Unable to create operator. Operator does not match signature.")

        return AssignOperator(operator, priority, unary, isArrayInitializer, assignment)
    }
}