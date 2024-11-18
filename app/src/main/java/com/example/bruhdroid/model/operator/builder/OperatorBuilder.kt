package com.example.bruhdroid.model.operator.builder

import com.example.bruhdroid.model.operator.Operator
import com.example.bruhdroid.model.operator.OperatorParseDto

open class OperatorBuilder(
    protected val operator: String,
    protected val priority: Int,
    protected val inputOperatorMatches: List<String>,
    protected val unary: Boolean = false,
    val unaryChange: Boolean = true
) {

    open fun parse(dto: OperatorParseDto): OperatorParseDto {
        val newDto = dto.prototype()
        val operatorObj = build(inputOperatorMatches[0], newDto.mayUnary)

        if (newDto.mayUnary && unary) {
            if (operator == "#")
                newDto.arrayInitialization = true

            newDto.operationStack.add(operatorObj)
            newDto.mayUnary = unaryChange
            return newDto
        }

        while (newDto.operationStack.size > 0 && newDto.operationStack.last().unary)
            newDto.postfixNotation.add(newDto.operationStack.removeLast().operator)

        if (newDto.operationStack.size > 0 && priority <= newDto.operationStack.last().priority)
            newDto.postfixNotation.add(newDto.operationStack.removeLast().inputOperator)

        newDto.operationStack.add(operatorObj)
        newDto.mayUnary = unaryChange
        return newDto
    }

    protected open fun match(inputOperator: String, mayUnary: Boolean): Boolean {
        if (unary && !mayUnary || !unary && mayUnary)
            return false

        return inputOperatorMatches.contains(inputOperator)
    }

    fun build(inputOperator: String, mayUnary: Boolean): Operator {
        if (!match(inputOperator, mayUnary))
            throw Exception("Unable to create operator. Operator does not match signature.")

        return Operator(operator, priority, inputOperator, unary)
    }

    fun tryBuild(inputOperator: String, mayUnary: Boolean): Operator? {
        return try {
            build(inputOperator, mayUnary)
        } catch (e: Exception) {
            null
        }
    }
}