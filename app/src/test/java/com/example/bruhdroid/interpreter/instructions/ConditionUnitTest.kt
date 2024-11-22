package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.*
import org.junit.Assert
import org.junit.Test

class ConditionUnitTest {
    @Test
    fun trueIf() {
        val a = InitInstruction("a = 3")
        val b = IfInstruction("a")
        val c = SetInstruction("a = 2")
        val d = EndInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("2", memory.get("a")?.value)
    }

    @Test
    fun falseIf() {
        val a = InitInstruction("a = 3")
        val b = IfInstruction("a > 5")
        val c = SetInstruction("a = 2")
        val d = EndInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("3", memory.get("a")?.value)
    }

    @Test
    fun trueIfFalseElif() {
        val a = InitInstruction("a = 3")

        val b = IfInstruction("a < 5")
        val c = SetInstruction("a = 2")

        val d = ElifInstruction("a > 5")
        val e = SetInstruction("a = 1")

        val f = EndInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d, e, f))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("2", memory.get("a")?.value)
    }

    @Test
    fun falseIfTrueElif() {
        val a = InitInstruction("a = 3")

        val b = IfInstruction("a > 5")
        val c = SetInstruction("a = 2")

        val d = ElifInstruction("a < 5")
        val e = SetInstruction("a = 1")

        val f = EndInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d, e, f))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("1", memory.get("a")?.value)
    }

    @Test
    fun nestedFalseIfIntoTrueIf() {
        val a = InitInstruction("a = 3")

        val b = IfInstruction("a < 5")
        val c = SetInstruction("a = 2")

        val d = IfInstruction("a > 5")
        val e = SetInstruction("a = 1")

        val f = EndInstruction()
        val g = EndInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d, e, f, g))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("2", memory.get("a")?.value)
    }

    @Test
    fun nestedTrueIfIntoFalseIf() {
        val a = InitInstruction("a = 3")

        val b = IfInstruction("a > 5")
        val c = SetInstruction("a = 2")

        val d = IfInstruction("a < 5")
        val e = SetInstruction("a = 1")

        val f = EndInstruction()
        val g = EndInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d, e, f, g))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("3", memory.get("a")?.value)
    }

    @Test
    fun doubleIf() {
        val a = InitInstruction("a = 0")

        val bTrue = IfInstruction("a < 5")
        val bFalse = IfInstruction("a > 5")

        val c = SetInstruction("a = a + 4")
        val d = EndInstruction()

        val eTrue = IfInstruction("a < 5")
        val eFalse = IfInstruction("a > 5")

        val f = SetInstruction("a = a + 10")
        val g = EndInstruction()

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
        val a = InitInstruction("a = 0")

        val bTrue = IfInstruction("a < 5")
        val bFalse = IfInstruction("a > 5")

            val c = SetInstruction("a = a + 4")

            val dTrue = IfInstruction("a < 5")
            val dFalse = IfInstruction("a > 5")

                val e = SetInstruction("a = a - 8")

            val fTrue = ElifInstruction("a < 5")
            val fFalse = ElifInstruction("a > 5")

                val g = SetInstruction("a = a - 2")

            val end1 = EndInstruction()
            val h = SetInstruction("a = a + 15")
        val end2 = EndInstruction()

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
        val a = InitInstruction("a = 0")

        val bTrue = IfInstruction("a < 5")
        val bFalse = IfInstruction("a > 5")

            val c = SetInstruction("a = a + 4")

            val dTrue = IfInstruction("a < 5")
            val dFalse = IfInstruction("a > 5")

                val e = SetInstruction("a = a - 8")

            val fTrue = ElifInstruction("a < 5")
            val fFalse = ElifInstruction("a > 5")

                val g = SetInstruction("a = a - 2")

            val else1 = ElseInstruction()
                val h = SetInstruction("a = a - 30")

            val end1 = EndInstruction()

        val else2 = ElseInstruction()
            val i = SetInstruction("a = a - 7")

            val jTrue = IfInstruction("a < 5")
            val jFalse = IfInstruction("a > 5")

                val k = SetInstruction("a = a - 3")

            val lTrue = ElifInstruction("a < 5")
            val lFalse = ElifInstruction("a > 5")

                val m = SetInstruction("a = a - 9")

            val else3 = ElseInstruction()
                val n = SetInstruction("a = a - 50")

            val end2 = EndInstruction()

            val o = SetInstruction("a = a + 15")
        val end3 = EndInstruction()

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