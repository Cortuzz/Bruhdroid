package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import org.junit.Assert
import org.junit.Test

class ConditionUnitTest {
    @Test
    fun trueIf() {
        val a = Block(Instruction.INIT,"a = 3")
        val b = Block(Instruction.IF,"a < 5")
        val c = Block(Instruction.SET,"a = 2")
        val d = Block(Instruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("2", memory["a"]?.value)
    }

    @Test
    fun falseIf() {
        val a = Block(Instruction.INIT,"a = 3")
        val b = Block(Instruction.IF,"a > 5")
        val c = Block(Instruction.SET,"a = 2")
        val d = Block(Instruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("3", memory["a"]?.value)
    }

    @Test
    fun trueIfFalseElif() {
        val a = Block(Instruction.INIT,"a = 3")

        val b = Block(Instruction.IF,"a < 5")
        val c = Block(Instruction.SET,"a = 2")

        val d = Block(Instruction.ELIF,"a > 5")
        val e = Block(Instruction.SET,"a = 1")

        val f = Block(Instruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("2", memory["a"]?.value)
    }

    @Test
    fun falseIfTrueElif() {
        val a = Block(Instruction.INIT,"a = 3")

        val b = Block(Instruction.IF,"a > 5")
        val c = Block(Instruction.SET,"a = 2")

        val d = Block(Instruction.ELIF,"a < 5")
        val e = Block(Instruction.SET,"a = 1")

        val f = Block(Instruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("1", memory["a"]?.value)
    }

    @Test
    fun nestedFalseIfIntoTrueIf() {
        val a = Block(Instruction.INIT,"a = 3")

        val b = Block(Instruction.IF,"a < 5")
        val c = Block(Instruction.SET,"a = 2")

        val d = Block(Instruction.IF,"a > 5")
        val e = Block(Instruction.SET,"a = 1")

        val f = Block(Instruction.END)
        val g = Block(Instruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f, g))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("2", memory["a"]?.value)
    }

    @Test
    fun nestedTrueIfIntoFalseIf() {
        val a = Block(Instruction.INIT,"a = 3")

        val b = Block(Instruction.IF,"a > 5")
        val c = Block(Instruction.SET,"a = 2")

        val d = Block(Instruction.IF,"a < 5")
        val e = Block(Instruction.SET,"a = 1")

        val f = Block(Instruction.END)
        val g = Block(Instruction.END)

        val interpreter = Interpreter(listOf(a, b, c, d, e, f, g))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("3", memory["a"]?.value)
    }

    @Test
    fun doubleIf() {
        val a = Block(Instruction.INIT,"a = 0")

        val bTrue = Block(Instruction.IF,"a < 5")
        val bFalse = Block(Instruction.IF,"a > 5")

        val c = Block(Instruction.SET,"a = a + 4")
        val d = Block(Instruction.END)

        val eTrue = Block(Instruction.IF,"a < 5")
        val eFalse = Block(Instruction.IF,"a > 5")

        val f = Block(Instruction.SET,"a = a + 10")
        val g = Block(Instruction.END)

        val blocks = mapOf(
            listOf(a, bTrue, c, d, eTrue, f, g) to "14",
            listOf(a, bFalse, c, d, eFalse, f, g) to "0",
            listOf(a, bTrue, c, d, eFalse, f, g) to "4",
            listOf(a, bFalse, c, d, eTrue, f, g) to "10")

        for (block in blocks) {
            val interpreter = Interpreter(block.key)
            interpreter.run()
            Assert.assertEquals(block.value, interpreter.memory.stack["a"]?.value)
        }
    }

    @Test
    fun nestedIfAndElifIntoIf() {
        val a = Block(Instruction.INIT,"a = 0")

        val bTrue = Block(Instruction.IF,"a < 5")
        val bFalse = Block(Instruction.IF,"a > 5")

            val c = Block(Instruction.SET,"a = a + 4")

            val dTrue = Block(Instruction.IF,"a < 5")
            val dFalse = Block(Instruction.IF,"a > 5")

                val e = Block(Instruction.SET,"a = a - 8")

            val fTrue = Block(Instruction.ELIF,"a < 5")
            val fFalse = Block(Instruction.ELIF,"a > 5")

                val g = Block(Instruction.SET,"a = a - 2")

            val end1 = Block(Instruction.END)
            val h = Block(Instruction.SET,"a = a + 15")
        val end2 = Block(Instruction.END)

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
            Assert.assertEquals(block.value, interpreter.memory.stack["a"]?.value)
        }
    }
}