package com.example.bruhdroid.model.operation

import com.example.bruhdroid.model.operation.operator.Operator

class OperationParseDto(
    var inputData: String,
    var operations: MutableList<Operation>,
    var operationStack: MutableList<Operator>,
    var mayUnary: Boolean,
    var arrayInitialization: Boolean
) {
    fun prototype(): OperationParseDto {
        return OperationParseDto(
            inputData,
            operations.toMutableList(),
            operationStack.toMutableList(),
            mayUnary,
            arrayInitialization
        )
    }
}