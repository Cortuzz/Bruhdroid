package com.example.bruhdroid.model.operation

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.src.blocks.IDataPresenter
import com.example.bruhdroid.model.src.blocks.valuable.Valuable

abstract class Operation(
    val operation: String,
) {
    abstract fun act(operand1: Valuable, operand2: Valuable?): Valuable?

    abstract fun evaluateExpressionToBlock(currentMemoryScope: Memory): IDataPresenter?
}