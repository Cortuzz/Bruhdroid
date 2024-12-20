package com.example.bruhdroid.model.blocks.variable

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.exception.RuntimeError
import com.example.bruhdroid.exception.StackCorruptionError
import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.blocks.valuable.Valuable

class Variable(
    val name: String = "",
    private val memory: Memory
    ): IDataPresenter {
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