package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.Block
import org.junit.Assert
import org.junit.Test

class TwoLeveledUnitTest {
    @Test
    fun falseWhile() {
        val a = Block(Instruction.INIT, "a = 1")

        val b = Block(Instruction.WHILE, "a > 100")
        val c = Block(Instruction.SET, "a = a * 2")
        val d = Block(Instruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        Assert.assertEquals("1", interpreter.memory.get("a")?.value)
    }
}