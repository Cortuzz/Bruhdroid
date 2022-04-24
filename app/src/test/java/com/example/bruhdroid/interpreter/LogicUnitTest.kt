package com.example.bruhdroid.interpreter

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Init
import com.example.bruhdroid.model.src.blocks.RawInput
import org.junit.Assert
import org.junit.Test

class LogicUnitTest {
    @Test
    fun integerLogic() {
        val a = Init(RawInput("a = 5 | 0"))
        val b = Init(RawInput("b = 1 - 1 & 2"))
        val c = Init(RawInput("c = a & b"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory.stack
        Assert.assertEquals(3, memory.size)

        Assert.assertEquals(Type.BOOL, memory["a"]?.type)
        Assert.assertEquals(Type.BOOL, memory["b"]?.type)
        Assert.assertEquals(Type.BOOL, memory["c"]?.type)

        Assert.assertEquals("true", memory["a"]?.value)
        Assert.assertEquals("false", memory["b"]?.value)
        Assert.assertEquals("false", memory["c"]?.value)
    }

    @Test
    fun stringLogic() {
        val a = Init(RawInput("a = \"453\" | \"\""))
        val b = Init(RawInput("b = \"76\" & \"\""))

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        val memory = interpreter.memory.stack
        Assert.assertEquals(2, memory.size)

        Assert.assertEquals(Type.BOOL, memory["a"]?.type)
        Assert.assertEquals(Type.BOOL, memory["b"]?.type)

        Assert.assertEquals("true", memory["a"]?.value)
        Assert.assertEquals("false", memory["b"]?.value)
    }

    @Test
    fun unaryLogic() {
    }
}
