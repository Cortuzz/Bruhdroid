package com.example.bruhdroid.model.operation.operand

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.IDataPresenter
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import com.example.bruhdroid.model.src.blocks.Variable
import com.example.bruhdroid.model.src.blocks.valuable.BooleanValuable
import com.example.bruhdroid.model.src.blocks.valuable.StringValuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.FloatValuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.IntegerValuable

class Operand(value: String): Operation(value) {
    override fun act(operand1: Valuable, operand2: Valuable?): Valuable {
        TODO("Not yet implemented")
    }

    override fun evaluateExpressionToBlock(currentMemoryScope: Memory): IDataPresenter? {
        return when {
            operation == "rand()" -> FloatValuable(Math.random())
            operation in listOf("true", "false") -> BooleanValuable(operation)
            operation.last() == '"' && operation.first() == '"' -> StringValuable(operation.replace("\"", ""))
            operation.contains("^[A-Za-z]+\$".toRegex()) -> Variable(operation, currentMemoryScope)
            operation.contains("[\\d]+\\.[\\d]+".toRegex()) -> FloatValuable(operation)
            operation.contains("[\\d]+".toRegex()) -> IntegerValuable(operation)
            else -> null
        }
    }
}