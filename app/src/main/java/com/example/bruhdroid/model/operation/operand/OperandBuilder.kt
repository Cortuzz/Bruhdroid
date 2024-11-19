package com.example.bruhdroid.model.operation.operand

import com.example.bruhdroid.model.operation.IOperationBuilder
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.operation.OperationParseDto

class OperandBuilder: IOperationBuilder {
    override fun parse(dto: OperationParseDto): OperationParseDto {
        val newDto = dto.prototype()
        newDto.postfixNotation.add(dto.inputData)
        newDto.mayUnary = false

        return newDto
    }

    override fun build(inputOperator: String, mayUnary: Boolean): Operation {
        return Operand(inputOperator)
    }

    override fun tryBuild(inputOperator: String, mayUnary: Boolean): Operation {
        return Operand(inputOperator)
    }
}