package com.example.bruhdroid.model.operation.operator

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Valuable
import com.example.bruhdroid.model.src.blocks.Variable

class AssignOperator(
    operator: String,
    priority: Int,
    unary: Boolean = false,
    private val isArrayInitializer: Boolean,
    private val assignment: (operand1: Variable, operand2: Valuable) -> Valuable
): Operator(operator, priority, unary, {_, _ -> null}) {
    fun assign(operand1: Variable, operand2: Valuable,
               isInitialization: Boolean,
               memory: Memory,
    ) {
        val value = assignment(operand1, operand2)
        val type = if (isArrayInitializer) {
            Type.LIST
        } else {
            operand2.type
        }

        if (isInitialization) {
            memory.pushToLocalMemory(operand1.name, type, value.clone())
            return
        }

        memory.tryPushToAnyMemory(operand1.name, type, value.clone())
    }
}