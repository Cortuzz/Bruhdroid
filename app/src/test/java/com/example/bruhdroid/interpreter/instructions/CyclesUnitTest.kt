package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Block
import org.junit.Assert
import org.junit.Test

class CyclesUnitTest {
    @Test
    fun basicWhile() {
        val a = Block(BlockInstruction.INIT, "a = 1")
        val counter = Block(BlockInstruction.INIT, "count = 0")

        val b = Block(BlockInstruction.WHILE, "a < 100")
            val c = Block(BlockInstruction.SET, "a = a * 2")
            val incCounter = Block(BlockInstruction.SET, "count = count + 1")
        val d = Block(BlockInstruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, counter, b, c, incCounter, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("128", memory.get("a")?.value)
        Assert.assertEquals("7", memory.get("count")?.value)
    }

    @Test
    fun whileNestedIntoWhile() {
        val a = Block(BlockInstruction.INIT, "a = 1")
        val counter = Block(BlockInstruction.INIT, "count = 0")

        val b = Block(BlockInstruction.WHILE, "a < 5000")
            val c = Block(BlockInstruction.SET, "a = a * 2")

            val d = Block(BlockInstruction.WHILE, "count < 3")
                val e = Block(BlockInstruction.SET, "a = a * 3")
                val e1 = Block(BlockInstruction.SET, "count = count + 1")
            val f = Block(BlockInstruction.END_WHILE)

            val incCounter = Block(BlockInstruction.SET, "count = count + 1")
        val g = Block(BlockInstruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, counter, b, c, d, e, e1, f, incCounter, g))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("6912", memory.get("a")?.value)
        Assert.assertEquals("11", memory.get("count")?.value)
    }

    @Test
    fun falseWhile() {
        val a = Block(BlockInstruction.INIT, "a = 1")

        val b = Block(BlockInstruction.WHILE, "a > 100")
            val c = Block(BlockInstruction.SET, "a = a * 2")
        val d = Block(BlockInstruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("1", memory.get("a")?.value)
    }
}
