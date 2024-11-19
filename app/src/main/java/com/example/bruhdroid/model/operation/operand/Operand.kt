package com.example.bruhdroid.model.operation.operand

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.blocks.IDataPresenter
import com.example.bruhdroid.model.src.blocks.Valuable
import com.example.bruhdroid.model.src.blocks.Variable

class Operand(value: String): Operation(value) {
    override fun act(operand1: Valuable, operand2: Valuable?): Valuable {
        TODO("Not yet implemented")
    }

    override fun evaluateExpressionToBlock(currentMemoryScope: Memory): IDataPresenter? {
        return when {
            operation == "rand()" -> Valuable(Math.random(), Type.FLOAT)
            operation in listOf("true", "false") -> Valuable(operation, Type.BOOL)
            operation.last() == '"' && operation.first() == '"' -> Valuable(operation.replace("\"", ""), Type.STRING)
            operation.contains("^[A-Za-z]+\$".toRegex()) -> Variable(operation, currentMemoryScope)
            operation.contains("[\\d]+\\.[\\d]+".toRegex()) -> Valuable(operation, Type.FLOAT)
            operation.contains("[\\d]+".toRegex()) -> Valuable(operation, Type.INT)
            else -> null
        }
    }
}