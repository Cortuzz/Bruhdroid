package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.operation.OperationParseDto

class CloseIndexOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>,
    pairAggregateOperator: String
): CloseAggregateOperatorBuilder(
    operator,
    priority,
    inputOperator, pairAggregateOperator) {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = super.parse(dto).prototype()

        if (!newDto.arrayInitialization) {
            newDto.postfixNotation.add("?")
        }
        newDto.arrayInitialization = false

        return newDto
    }
}