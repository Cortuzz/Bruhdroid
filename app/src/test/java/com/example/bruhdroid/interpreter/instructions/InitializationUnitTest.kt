package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Init
import com.example.bruhdroid.model.src.blocks.RawInput
import org.junit.Assert
import org.junit.Test

class InitializationUnitTest {
    @Test
    fun variableInit() {
        val a = Init(RawInput("a"))
        val b = Init(RawInput("b"))
        val c = Init(RawInput("a"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        Assert.assertEquals(2, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals(Type.UNDEFINED, memory["a"]?.type)
        Assert.assertEquals(Type.UNDEFINED, memory["b"]?.type)
    }

    @Test
    fun multiplyVariableInit() {
        val a = Init(RawInput("a, b, c, d"))

        val interpreter = Interpreter(listOf(a))
        interpreter.run()

        Assert.assertEquals(4, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals(Type.UNDEFINED, memory["a"]?.type)
        Assert.assertEquals(Type.UNDEFINED, memory["b"]?.type)
        Assert.assertEquals(Type.UNDEFINED, memory["c"]?.type)
        Assert.assertEquals(Type.UNDEFINED, memory["d"]?.type)
    }

    @Test
    fun variableInitByValuable() {
        val a = Init(RawInput("a = 5"))
        val b = Init(RawInput("b = 423"))
        val c = Init(RawInput("a = 75"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory.stack
        Assert.assertEquals(2, memory.size)

        Assert.assertEquals("75", memory["a"]?.value)
        Assert.assertEquals("423", memory["b"]?.value)
    }

    @Test
    fun variableInitByPreviousValue() {
        val a = Init(RawInput("a = 6 * (1 + 3)"))
        val b = Init(RawInput("a =  a - 10"))

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        Assert.assertEquals(1, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals("14", memory["a"]?.value)
    }

    @Test
    fun variableInitWithUnaryMinus() {
        val a = Init(RawInput("a = -5"))
        val b = Init(RawInput("b = -a"))
        val c = Init(RawInput("c = -b * -a"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("-5", memory["a"]?.value)
        Assert.assertEquals("5", memory["b"]?.value)
        Assert.assertEquals("-25", memory["c"]?.value)
    }
}