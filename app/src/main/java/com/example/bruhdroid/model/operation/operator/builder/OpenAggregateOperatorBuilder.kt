package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.operation.OperationParseDto

class OpenAggregateOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>
): AggregateOperatorBuilder(operator, priority, inputOperator, true) {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        val operator = build(inputOperatorMatches[0], newDto.mayUnary)
        newDto.operationStack.add(operator)

        return newDto
    }
}