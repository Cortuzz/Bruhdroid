package com.example.bruhdroid.model.memory

import com.example.bruhdroid.model.src.StackCorruptionError
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.blocks.Valuable
import com.example.bruhdroid.model.src.blocks.Variable

class Memory(val prevMemory: Memory?, val scope: String) {
    private val stack: MutableMap<String, Valuable> = mutableMapOf()

    fun tryFindInMemory(name: String): Valuable {
        return tryFindInMemory(name, this)
    }

    private fun tryFindInMemory(name: String, memory: Memory): Valuable {
        val value = memory.get(name)

        if (value != null) {
            return value
        }

        if (memory.prevMemory == null) {
            memory.throwStackError(name)
            throw Exception()
        }

        return tryFindInMemory(name, memory.prevMemory)
    }

    fun push(address: String, value: Block) {
        value as Valuable
        if (value.type == Type.LIST) {
            if (value.array.isEmpty()) {
                initArray(value, value.value.toInt())
            }
        }
        stack[address] = value
    }

    fun getAllVariables(): Map<String, Valuable> {
        return stack.toMap()
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
        val corruptedVar = Variable(address, this)
        throw StackCorruptionError(
            "Expected reserved memory for Variable ${address}@" +
                    "${corruptedVar.hashCode()} at address 0x" +
                    "${corruptedVar.toString().split('@').last().uppercase()} " +
                    "but stack corruption has occurred"
        )
    }
}
