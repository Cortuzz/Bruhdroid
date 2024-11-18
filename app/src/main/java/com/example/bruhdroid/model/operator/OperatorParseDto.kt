package com.example.bruhdroid.model.operator

class OperatorParseDto(
    var postfixNotation: MutableList<String>,
    var operationStack: MutableList<Operator>,
    var mayUnary: Boolean,
    var arrayInitialization: Boolean
) {
    fun prototype(): OperatorParseDto {
        return OperatorParseDto(
            postfixNotation.toMutableList(),
            operationStack.toMutableList(),
            mayUnary,
            arrayInitialization
        )
    }
}