package com.example.bruhdroid.model.operator.builder

import com.example.bruhdroid.model.operator.OperatorParseDto

class CloseIndexOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>,
    pairAggregateOperator: String
): CloseAggregateOperatorBuilder(
    operator,
    priority,
    inputOperator, pairAggregateOperator) {
    override fun parse(dto: OperatorParseDto): OperatorParseDto {
        val newDto = super.parse(dto).prototype()

        if (!newDto.arrayInitialization) {
            newDto.postfixNotation.add("?")
        }
        newDto.arrayInitialization = false

        return newDto
    }
}