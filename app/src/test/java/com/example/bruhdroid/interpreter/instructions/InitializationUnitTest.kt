package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.model.blocks.instruction.InitInstruction
import org.junit.Assert
import org.junit.Test

class InitializationUnitTest {
    @Test
    fun variableInit() {
        val a = InitInstruction("a = 1")
        val b = InitInstruction("b = 2")
        val c = InitInstruction("a = 3")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("3", memory.get("a")?.value)
        Assert.assertEquals("2", memory.get("b")?.value)
    }

    @Test
    fun multiplyVariableInit() {
        val a = InitInstruction("a = 1, b = 2.45, c = \"45\", d = \"3,2\"")

        val interpreter = Interpreter(listOf(a))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("1", memory.get("a")?.value)
        Assert.assertEquals("2.45", memory.get("b")?.value)
        Assert.assertEquals("45", memory.get("c")?.value)
        Assert.assertEquals("3,2", memory.get("d")?.value)

        Assert.assertEquals(ValuableType.INT, memory.get("a")?.type)
        Assert.assertEquals(ValuableType.FLOAT, memory.get("b")?.type)
        Assert.assertEquals(ValuableType.STRING, memory.get("c")?.type)
        Assert.assertEquals(ValuableType.STRING, memory.get("d")?.type)
    }

    @Test
    fun variableInitByValuable() {
        val a = InitInstruction("a = 5")
        val b = InitInstruction("b = 423")
        val c = InitInstruction("a = 75")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("75", memory.get("a")?.value)
        Assert.assertEquals("423", memory.get("b")?.value)
    }

    @Test
    fun variableInitByPreviousValue() {
        val a = InitInstruction("a = 6 * (1 + 3)")
        val b = InitInstruction("a =  a - 10")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("14", memory.get("a")?.value)
    }

    @Test
    fun variableInitWithUnaryMinus() {
        val a = InitInstruction("a = -5")
        val b = InitInstruction("b = -a")
        val c = InitInstruction("c = -b * -a")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("-5", memory.get("a")?.value)
        Assert.assertEquals("5", memory.get("b")?.value)
        Assert.assertEquals("-25", memory.get("c")?.value)
    }
}