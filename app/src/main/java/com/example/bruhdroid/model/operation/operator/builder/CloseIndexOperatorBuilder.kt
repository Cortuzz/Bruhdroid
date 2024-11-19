package com.example.bruhdroid.model.operation.operator.builder

import com.example.bruhdroid.model.Notation
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.operation.OperationBuilderFactory
import com.example.bruhdroid.model.operation.OperationParseDto
import com.example.bruhdroid.model.operation.operator.Operator

class CloseIndexOperatorBuilder(
    operator: String,
    priority: Int,
    pairAggregateOperator: String,
    private val indexCheckOperatorBuilder: OperatorBuilder
): CloseAggregateOperatorBuilder(
    operator,
    priority,
    pairAggregateOperator) {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = super.parse(dto).prototype()

        if (!newDto.arrayInitialization) {
            newDto.operations.add(indexCheckOperatorBuilder.build(
                indexCheckOperatorBuilder.getStringOperator(),
                false
            ))
        }
        newDto.arrayInitialization = false

        return newDto
    }
}