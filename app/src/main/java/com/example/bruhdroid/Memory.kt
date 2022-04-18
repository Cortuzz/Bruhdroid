package com.example.bruhdroid

import com.example.bruhdroid.blocks.Block

class Memory(val prevMemory: Memory?) {
    val stack: MutableMap<String, Block> = mutableMapOf()

    fun push(address: String, value: Block) {
        stack[address] = value
    }

    fun pop(address: String) {
        stack.remove(address)
    }
}
