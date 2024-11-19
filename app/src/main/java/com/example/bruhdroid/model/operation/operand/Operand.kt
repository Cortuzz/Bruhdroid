package com.example.bruhdroid.model.operation.operand

import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.src.blocks.Valuable

class Operand(value: String): Operation(value) {
    override fun act(operand1: Valuable, operand2: Valuable?): Valuable? {
        TODO("Not yet implemented")
    }
}