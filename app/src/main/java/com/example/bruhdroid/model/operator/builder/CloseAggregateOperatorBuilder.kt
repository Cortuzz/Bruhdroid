package com.example.bruhdroid.model.operator.builder

import com.example.bruhdroid.model.operator.OperatorParseDto

open class CloseAggregateOperatorBuilder(
    operator: String,
    priority: Int,
    inputOperator: List<String>,
    private val pairAggregateOperator: String
): AggregateOperatorBuilder(operator, priority, inputOperator, false) {
    override fun parse(dto: OperatorParseDto): OperatorParseDto {
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