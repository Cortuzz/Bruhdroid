package com.example.bruhdroid.model.operation.operand

import com.example.bruhdroid.model.operation.IOperationBuilder
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.operation.OperationParseDto
import com.example.bruhdroid.model.operation.operator.Operator

class OperandBuilder: IOperationBuilder {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        newDto.mayUnary = false
        newDto.operations.add(build(dto.inputData, false))

        return newDto
    }

    override fun build(inputOperator: String, mayUnary: Boolean): Operation {
        return Operand(inputOperator)
    }

    override fun tryBuild(inputOperator: String, mayUnary: Boolean): Operation {
        return Operand(inputOperator)
    }
}