package com.example.bruhdroid.interpreter

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Init
import com.example.bruhdroid.model.src.blocks.RawInput
import org.junit.Assert
import org.junit.Test

class ArithmeticUnitTest {
    @Test
    fun valuableArithmetic() {
        val a = Init(RawInput("a = 5 * 2"))
        val b = Init(RawInput("b = 5 + 1 * 2"))
        val c = Init(RawInput("c = 9 * (1 - 5)"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        Assert.assertEquals(3, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals("10", memory["a"]?.value)
        Assert.assertEquals("7", memory["b"]?.value)
        Assert.assertEquals("-36", memory["c"]?.value)
    }

    @Test
    fun variableArithmetic() {
        val a = Init(RawInput("a = 6 * (1 + 3)"))
        val b = Init(RawInput("b = a - 20"))
        val c = Init(RawInput("c = b * a - 50"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        Assert.assertEquals(3, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals("24", memory["a"]?.value)
        Assert.assertEquals("4", memory["b"]?.value)
        Assert.assertEquals("46", memory["c"]?.value)
    }

    @Test
    fun unaryArithmetic() {
        val a = Init(RawInput("a = -1 * -5"))
        val b = Init(RawInput("b = (-3 + -a) * -9"))
        val c = Init(RawInput("c = 100 * (-12 / -a * -(b)) / -(-3) / a / b"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory.stack
        Assert.assertEquals(3, memory.size)

        Assert.assertEquals("5", memory["a"]?.value)
        Assert.assertEquals("72", memory["b"]?.value)
        Assert.assertEquals("-13", memory["c"]?.value)
    }

    @Test
    fun basicInequalityArithmetic() {
        val a = Init(RawInput("a = -5"))
        val b = Init(RawInput("b = 3"))
        val c = Init(RawInput("c = a < b"))
        val d = Init(RawInput("d = a > b"))

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory.stack
        Assert.assertEquals(4, memory.size)

        Assert.assertEquals("-5", memory["a"]?.value)
        Assert.assertEquals("3", memory["b"]?.value)

        Assert.assertEquals("true", memory["c"]?.value)
        Assert.assertEquals(Type.BOOL, memory["c"]?.type)

        Assert.assertEquals("false", memory["d"]?.value)
        Assert.assertEquals(Type.BOOL, memory["d"]?.type)
    }
}
