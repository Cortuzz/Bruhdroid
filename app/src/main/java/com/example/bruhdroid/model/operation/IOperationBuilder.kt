package com.example.bruhdroid.model.operation

import com.example.bruhdroid.model.operation.operator.Operator

interface IOperationBuilder {
    fun parse(dto: OperationParseDto): OperationParseDto

    fun build(inputOperator: String, mayUnary: Boolean): Operation

    fun tryBuild(inputOperator: String, mayUnary: Boolean): Operation?
}