package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.operation.OperationParseDto

open class CloseAggregateOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>,
    private val pairAggregateOperator: String
): AggregateOperatorBuilder(operator, priority, inputOperator, false) {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        newDto.mayUnary = unaryChange

        var s = newDto.operationStack.removeLast()
        while (s.operator != pairAggregateOperator) {
            newDto.postfixNotation.add(s.inputOperator)
            s = newDto.operationStack.removeLast()
        }

        return newDto
    }
}