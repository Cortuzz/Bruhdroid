package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.StackCorruptionError
import com.example.bruhdroid.model.src.blocks.valuable.Valuable

class Variable(
    val name: String = "",
    private val memory: Memory
    ) : Block(Instruction.VAR, ""), IDataPresenter {
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