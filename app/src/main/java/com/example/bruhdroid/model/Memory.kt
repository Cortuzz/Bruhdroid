package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.StackCorruptionError
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.blocks.Valuable
import com.example.bruhdroid.model.src.blocks.Variable

class Memory(val prevMemory: Memory?, val scope: String) {
    val stack: MutableMap<String, Valuable> = mutableMapOf()

    fun push(address: String, value: Block) {
        value as Valuable
        if (value.type == Type.LIST) {
            if (value.array.isEmpty()) {
                initArray(value, value.value.toInt())
            }
        }
        stack[address] = value
    }

    private fun initArray(value: Valuable, count: Int) {
        for (i in 0 until count) {
            value.array.add(Valuable("", type = Type.UNDEFINED))
        }
    }

    fun get(address: String): Valuable? {
        return stack[address]
    }

    fun throwStackError(address: String) {
        val corruptedVar = Variable(address)
        throw StackCorruptionError(
            "Expected reserved memory for Variable ${address}@" +
                    "${corruptedVar.hashCode()} at address 0x" +
                    "${corruptedVar.toString().split('@').last().uppercase()} " +
                    "but stack corruption has occurred"
        )
    }
}
