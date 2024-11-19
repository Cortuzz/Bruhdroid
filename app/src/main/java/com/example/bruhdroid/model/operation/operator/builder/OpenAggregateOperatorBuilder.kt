package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.operation.OperationParseDto

class OpenAggregateOperatorBuilder(
    operator: String,
    priority: Int,
): AggregateOperatorBuilder(operator, priority, unaryChange = true) {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        val operator = build(inputOperator, newDto.mayUnary)
        newDto.operationStack.add(operator)

        return newDto
    }
}