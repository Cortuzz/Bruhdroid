package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.StackCorruptionError
import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.blocks.Valuable
import com.example.bruhdroid.model.src.blocks.Variable

class Memory(val prevMemory: Memory?) {
    val stack: MutableMap<String, Valuable> = mutableMapOf()

    fun push(address: String, value: Block) {
        value as Valuable
        stack[address] = value
    }

    fun get(address: String): Valuable? {
        return stack[address]
    }

    fun pop(address: String): Valuable? {
        return stack.remove(address)
    }

    fun throwStackError(address: String) {
        val corruptedVar = Variable(address)
        throw StackCorruptionError("Expected reserved memory for Variable ${address}@" +
                "${corruptedVar.hashCode()} at address 0x" +
                "${corruptedVar.toString().split('@').last().uppercase()} " +
                "but stack corruption has occurred")
    }
}
