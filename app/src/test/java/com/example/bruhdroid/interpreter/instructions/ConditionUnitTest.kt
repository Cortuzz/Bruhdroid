package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Block
import org.junit.Assert
import org.junit.Test

class ConditionUnitTest {
    @Test
    fun trueIf() {
        val a = Block(BlockInstruction.INIT,"a = 3")
        val b = Block(BlockInstruction.IF,"a")
        val c = Block(BlockInstruction.SET,"a = 2")
        val d = Block(BlockInstruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("2", memory.get("a")?.value)
    }

    @Test
    fun falseIf() {
        val a = Block(BlockInstruction.INIT,"a = 3")
        val b = Block(BlockInstruction.IF,"a > 5")
        val c = Block(BlockInstruction.SET,"a = 2")
        val d = Block(BlockInstruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("3", memory.get("a")?.value)
    }

    @Test
    fun trueIfFalseElif() {
        val a = Block(BlockInstruction.INIT,"a = 3")

        val b = Block(BlockInstruction.IF,"a < 5")
        val c = Block(BlockInstruction.SET,"a = 2")

        val d = Block(BlockInstruction.ELIF,"a > 5")
        val e = Block(BlockInstruction.SET,"a = 1")

        val f = Block(BlockInstruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("2", memory.get("a")?.value)
    }

    @Test
    fun falseIfTrueElif() {
        val a = Block(BlockInstruction.INIT,"a = 3")

        val b = Block(BlockInstruction.IF,"a > 5")
        val c = Block(BlockInstruction.SET,"a = 2")

        val d = Block(BlockInstruction.ELIF,"a < 5")
        val e = Block(BlockInstruction.SET,"a = 1")

        val f = Block(BlockInstruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("1", memory.get("a")?.value)
    }

    @Test
    fun nestedFalseIfIntoTrueIf() {
        val a = Block(BlockInstruction.INIT,"a = 3")

        val b = Block(BlockInstruction.IF,"a < 5")
        val c = Block(BlockInstruction.SET,"a = 2")

        val d = Block(BlockInstruction.IF,"a > 5")
        val e = Block(BlockInstruction.SET,"a = 1")

        val f = Block(BlockInstruction.END)
        val g = Block(BlockInstruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f, g))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("2", memory.get("a")?.value)
    }

    @Test
    fun nestedTrueIfIntoFalseIf() {
        val a = Block(BlockInstruction.INIT,"a = 3")

        val b = Block(BlockInstruction.IF,"a > 5")
        val c = Block(BlockInstruction.SET,"a = 2")

        val d = Block(BlockInstruction.IF,"a < 5")
        val e = Block(BlockInstruction.SET,"a = 1")

        val f = Block(BlockInstruction.END)
        val g = Block(BlockInstruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f, g))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("3", memory.get("a")?.value)
    }

    @Test
    fun doubleIf() {
        val a = Block(BlockInstruction.INIT,"a = 0")

        val bTrue = Block(BlockInstruction.IF,"a < 5")
        val bFalse = Block(BlockInstruction.IF,"a > 5")

        val c = Block(BlockInstruction.SET,"a = a + 4")
        val d = Block(BlockInstruction.END)

        val eTrue = Block(BlockInstruction.IF,"a < 5")
        val eFalse = Block(BlockInstruction.IF,"a > 5")

        val f = Block(BlockInstruction.SET,"a = a + 10")
        val g = Block(BlockInstruction.END)

        val blocks = mapOf(
            listOf(a, bTrue, c, d, eTrue, f, g) to "14",
            listOf(a, bFalse, c, d, eFalse, f, g) to "0",
            listOf(a, bTrue, c, d, eFalse, f, g) to "4",
            listOf(a, bFalse, c, d, eTrue, f, g) to "10")

        for (block in blocks) {
            val interpreter = Interpreter(block.key)
            interpreter.run()
            Assert.assertEquals(block.value, interpreter.memory.get("a")?.value)
        }
    }

    @Test
    fun nestedIfAndElifIntoIf() {
        val a = Block(BlockInstruction.INIT,"a = 0")

        val bTrue = Block(BlockInstruction.IF,"a < 5")
        val bFalse = Block(BlockInstruction.IF,"a > 5")

            val c = Block(BlockInstruction.SET,"a = a + 4")

            val dTrue = Block(BlockInstruction.IF,"a < 5")
            val dFalse = Block(BlockInstruction.IF,"a > 5")

                val e = Block(BlockInstruction.SET,"a = a - 8")

            val fTrue = Block(BlockInstruction.ELIF,"a < 5")
            val fFalse = Block(BlockInstruction.ELIF,"a > 5")

                val g = Block(BlockInstruction.SET,"a = a - 2")

            val end1 = Block(BlockInstruction.END)
            val h = Block(BlockInstruction.SET,"a = a + 15")
        val end2 = Block(BlockInstruction.END)

        val blocks = mapOf(
            listOf(a, bTrue, c, dTrue, e, fTrue, g, end1, h, end2) to "11",
            listOf(a, bTrue, c, dTrue, e, fFalse, g, end1, h, end2) to "11",
            listOf(a, bTrue, c, dFalse, e, fTrue, g, end1, h, end2) to "17",
            listOf(a, bTrue, c, dFalse, e, fFalse, g, end1, h, end2) to "19",

            listOf(a, bFalse, c, dTrue, e, fTrue, g, end1, h, end2) to "0",
            listOf(a, bFalse, c, dTrue, e, fFalse, g, end1, h, end2) to "0",
            listOf(a, bFalse, c, dFalse, e, fTrue, g, end1, h, end2) to "0",
            listOf(a, bFalse, c, dFalse, e, fTrue, g, end1, h, end2) to "0",
        )

        for (block in blocks) {
            val interpreter = Interpreter(block.key)
            interpreter.run()
            Assert.assertEquals(block.value, interpreter.memory.get("a")?.value)
        }
    }

    @Test
    fun nestedIfElifElseIntoIfElse() {
        val a = Block(BlockInstruction.INIT,"a = 0")

        val bTrue = Block(BlockInstruction.IF,"a < 5")
        val bFalse = Block(BlockInstruction.IF,"a > 5")

            val c = Block(BlockInstruction.SET,"a = a + 4")

            val dTrue = Block(BlockInstruction.IF,"a < 5")
            val dFalse = Block(BlockInstruction.IF,"a > 5")

                val e = Block(BlockInstruction.SET,"a = a - 8")

            val fTrue = Block(BlockInstruction.ELIF,"a < 5")
            val fFalse = Block(BlockInstruction.ELIF,"a > 5")

                val g = Block(BlockInstruction.SET,"a = a - 2")

            val else1 = Block(BlockInstruction.ELSE)
                val h = Block(BlockInstruction.SET,"a = a - 30")

            val end1 = Block(BlockInstruction.END)

        val else2 = Block(BlockInstruction.ELSE)
            val i = Block(BlockInstruction.SET,"a = a - 7")

            val jTrue = Block(BlockInstruction.IF,"a < 5")
            val jFalse = Block(BlockInstruction.IF,"a > 5")

                val k = Block(BlockInstruction.SET,"a = a - 3")

            val lTrue = Block(BlockInstruction.ELIF,"a < 5")
            val lFalse = Block(BlockInstruction.ELIF,"a > 5")

                val m = Block(BlockInstruction.SET,"a = a - 9")

            val else3 = Block(BlockInstruction.ELSE)
                val n = Block(BlockInstruction.SET,"a = a - 50")

            val end2 = Block(BlockInstruction.END)

            val o = Block(BlockInstruction.SET,"a = a + 15")
        val end3 = Block(BlockInstruction.END)

        val blocks = mapOf(
            listOf(a, bTrue, c, dTrue, e, fTrue, g, else1, h, end1, else2, i, jTrue, k, lTrue, m, else3, n, end2, o, end3) to "-4",
            listOf(a, bTrue, c, dTrue, e, fFalse, g, else1, h, end1, else2, i, jTrue, k, lTrue, m, else3, n, end2, o, end3) to "-4",
            listOf(a, bTrue, c, dFalse, e, fTrue, g, else1, h, end1, else2, i, jTrue, k, lTrue, m, else3, n, end2, o, end3) to "2",
            listOf(a, bTrue, c, dFalse, e, fFalse, g, else1, h, end1, else2, i, jTrue, k, lTrue, m, else3, n, end2, o, end3) to "-26",

            listOf(a, bFalse, c, dTrue, e, fTrue, g, else1, h, end1, else2, i, jTrue, k, lTrue, m, else3, n, end2, o, end3) to "5",
            listOf(a, bFalse, c, dTrue, e, fFalse, g, else1, h, end1, else2, i, jTrue, k, lFalse, m, else3, n, end2, o, end3) to "5",
            listOf(a, bFalse, c, dFalse, e, fTrue, g, else1, h, end1, else2, i, jFalse, k, lTrue, m, else3, n, end2, o, end3) to "-1",
            listOf(a, bFalse, c, dFalse, e, fTrue, g, else1, h, end1, else2, i, jFalse, k, lFalse, m, else3, n, end2, o, end3) to "-42",
        )

        for (block in blocks) {
            val interpreter = Interpreter(block.key)
            interpreter.run()
            Assert.assertEquals(block.value, interpreter.memory.get("a")?.value)
        }
    }
}