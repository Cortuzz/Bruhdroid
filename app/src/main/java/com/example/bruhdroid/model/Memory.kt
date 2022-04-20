package com.example.bruhdroid.model

import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.Valuable

class Memory(val prevMemory: Memory?) {
    val stack: MutableMap<String, Valuable> = mutableMapOf()

    fun push(address: String, value: Block) {
        value as Valuable
        stack[address] = value
    }

    fun pop(address: String): Valuable {
        return stack.remove(address)!!
    }
}
