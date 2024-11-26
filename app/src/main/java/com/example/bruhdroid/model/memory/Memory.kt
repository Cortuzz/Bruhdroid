package com.example.bruhdroid.model.memory

import com.example.bruhdroid.exception.StackCorruptionError
import com.example.bruhdroid.model.blocks.valuable.Valuable
import com.example.bruhdroid.model.blocks.variable.Variable
import com.example.bruhdroid.model.blocks.valuable.ListValuable

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

    fun push(address: String, value: Valuable) {
        stack[address] = value
    }

    fun getAllVariables(): Map<String, Valuable> {
        return stack.toMap()
    }

    fun get(address: String): Valuable? {
        return stack[address]
    }

    fun exitMemoryFunctionStack(): Memory {
        var tMemory: Memory = this

        while (true) {
            if (tMemory.scope.contains("METHOD")) {
                break
            }
            tMemory = prevMemory!!
        }
        return tMemory.prevMemory!!
    }

    private fun throwStackError(address: String) {
        val corruptedVar = Variable(address, this)
        throw StackCorruptionError(
            "Expected reserved memory for Variable ${address}@" +
                    "${corruptedVar.hashCode()} at address 0x" +
                    "${corruptedVar.toString().split('@').last().uppercase()} " +
                    "but stack corruption has occurred"
        )
    }

    fun pushToLocalMemory(name: String, valueBlock: Valuable) {
        valueBlock.listLink = null

        if (valueBlock is ListValuable) {
            val block = valueBlock.clone()
            push(name, block)
            return
        }

        push(name, valueBlock)
    }

    fun tryPushToAnyMemory(name: String, valueBlock: Valuable): Boolean {
        valueBlock.listLink = null
       return tryPushToAnyMemory(this, name, valueBlock)
    }

    private fun tryPushToAnyMemory(
        memory: Memory,
        name: String,
        valueBlock: Valuable
    ): Boolean {
        if (memory.get(name) != null) {
            memory.push(name, valueBlock)
            return true
        }

        if (memory.prevMemory == null) {
            memory.throwStackError(name)
            return false
        }

        return tryPushToAnyMemory(memory.prevMemory, name, valueBlock)
    }
}
