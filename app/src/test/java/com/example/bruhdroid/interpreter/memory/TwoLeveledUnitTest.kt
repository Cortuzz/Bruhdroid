package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Block
import org.junit.Assert
import org.junit.Test

class TwoLeveledUnitTest {
    @Test
    fun falseWhile() {
        val a = Block(BlockInstruction.INIT, "a = 1")

        val b = Block(BlockInstruction.WHILE, "a > 100")
        val c = Block(BlockInstruction.SET, "a = a * 2")
        val d = Block(BlockInstruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        Assert.assertEquals("1", interpreter.memory.get("a")?.value)
    }
}