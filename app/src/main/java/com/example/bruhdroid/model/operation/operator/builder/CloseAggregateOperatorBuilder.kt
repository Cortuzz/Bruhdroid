package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.operation.OperationParseDto

open class CloseAggregateOperatorBuilder(
    operator: String,
    priority: Int,
    private val pairAggregateOperator: String
): AggregateOperatorBuilder(operator, priority, false) {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        newDto.mayUnary = unaryChange

        var s = newDto.operationStack.removeLast()
        while (s.operator != pairAggregateOperator) {
            newDto.operations.add(s)
            s = newDto.operationStack.removeLast()
        }

        return newDto
    }
}