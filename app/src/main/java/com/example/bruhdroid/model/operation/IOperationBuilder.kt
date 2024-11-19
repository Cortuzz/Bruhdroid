package com.example.bruhdroid.model.operation

interface IOperationBuilder {
    fun parse(dto: OperationParseDto): OperationParseDto

    fun build(inputOperator: String, mayUnary: Boolean): Operation

    fun tryBuild(inputOperator: String, mayUnary: Boolean): Operation?
}