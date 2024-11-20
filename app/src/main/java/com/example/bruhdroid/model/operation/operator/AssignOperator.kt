package com.example.bruhdroid.model.operation.operator

import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.model.blocks.valuable.Valuable
import com.example.bruhdroid.model.blocks.Variable
import com.example.bruhdroid.model.blocks.valuable.ListValuable

class AssignOperator(
    operator: String,
    priority: Int,
    unary: Boolean = false,
    parsedUnary: Boolean = unary,
    private val isArrayInitializer: Boolean,
    private val assignment: (operand1: IDataPresenter, operand2: Valuable) -> Valuable
): Operator(operator, priority, unary, parsedUnary, {_, _ -> null}) {
    fun assign(operand1: IDataPresenter, operand2: Valuable,
               isInitialization: Boolean,
               memory: Memory,
    ) {
        val value = assignment(operand1, operand2)

        if (operand1 is Valuable && operand1.listLink != null) {
            operand1.listLink!!.update(operand1, operand2)
            return
        }

        val valuable = if (isArrayInitializer) {
            ListValuable(value)
        } else {
            value
        }

        operand1 as Variable

        if (isInitialization) {
            memory.pushToLocalMemory(operand1.name, valuable.clone())
            return
        }

        memory.tryPushToAnyMemory(operand1.name, valuable.clone())
    }
}