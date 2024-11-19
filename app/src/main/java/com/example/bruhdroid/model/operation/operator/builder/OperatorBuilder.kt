package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.operation.IOperationBuilder
import com.example.bruhdroid.model.operation.operator.Operator
import com.example.bruhdroid.model.operation.OperationParseDto
import com.example.bruhdroid.model.src.blocks.valuable.Valuable

open class OperatorBuilder(
    protected val operator: String,
    protected val priority: Int,
    protected val inputOperator: String = operator,
    protected val action: (operand1: Valuable, operand2: Valuable?) -> Valuable?,
    protected val unary: Boolean = false,
    val unaryChange: Boolean = true,
): IOperationBuilder {
    fun getStringOperator(): String {
        return operator
    }

    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        val operatorObj = build(inputOperator, newDto.mayUnary)

        if (newDto.mayUnary && unary) {
            if (operator == "#")
                newDto.arrayInitialization = true

            newDto.operationStack.add(operatorObj)
            newDto.mayUnary = unaryChange
            return newDto
        }

        while (newDto.operationStack.size > 0 && newDto.operationStack.last().unary)
            newDto.operations.add(newDto.operationStack.removeLast())

        if (newDto.operationStack.size > 0 && priority <= newDto.operationStack.last().priority)
            newDto.operations.add(newDto.operationStack.removeLast())

        newDto.operationStack.add(operatorObj)
        newDto.mayUnary = unaryChange
        return newDto
    }

    protected open fun match(inputOperator: String, mayUnary: Boolean): Boolean {
        if (unary && !mayUnary || !unary && mayUnary)
            return false

        return this.inputOperator == inputOperator
    }

    override fun build(inputOperator: String, mayUnary: Boolean): Operator {
        if (!match(inputOperator, mayUnary))
            throw Exception("Unable to create operator. Operator does not match signature.")

        return Operator(operator, priority, unary, action)
    }

    override fun tryBuild(inputOperator: String, mayUnary: Boolean): Operator? {
        return try {
            build(inputOperator, mayUnary)
        } catch (e: Exception) {
            null
        }
    }
}