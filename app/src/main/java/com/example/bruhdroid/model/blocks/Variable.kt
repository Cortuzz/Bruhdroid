package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.exception.RuntimeError
import com.example.bruhdroid.exception.StackCorruptionError
import com.example.bruhdroid.model.blocks.valuable.Valuable

class Variable(
    val name: String = "",
    private val memory: Memory
    ) : Block(BlockInstruction.VAR, ""), IDataPresenter {
    override fun getData(): Valuable {
        try {
            return memory.tryFindInMemory(name)
        } catch (e: StackCorruptionError) {
            throw RuntimeError("${e.message}")
        }
    }

    override fun tryGetData(): Valuable? {
        return try {
            getData()
        } catch (e: Exception) {
            null
        }
    }
}