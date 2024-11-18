package com.example.bruhdroid.model.operator.builder

import com.example.bruhdroid.model.operator.OperatorParseDto

class OpenAggregateOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>
): AggregateOperatorBuilder(operator, priority, inputOperator, true) {
    override fun parse(dto: OperatorParseDto): OperatorParseDto {
        val newDto = dto.prototype()
        val operator = build(inputOperatorMatches[0], newDto.mayUnary)
        newDto.operationStack.add(operator)

        return newDto
    }
}