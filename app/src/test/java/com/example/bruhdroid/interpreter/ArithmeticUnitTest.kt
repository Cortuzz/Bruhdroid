package com.example.bruhdroid.interpreter

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.model.blocks.instruction.InitInstruction
import org.junit.Assert
import org.junit.Test

class ArithmeticUnitTest {
    @Test
    fun valuableArithmetic() {
        val a = InitInstruction("a = 5 * 2")
        val b = InitInstruction("b = 5 + 1 * 2")
        val c = InitInstruction("c = 9 * (1 - 5)")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()
        
        val memory = interpreter.memory

        Assert.assertEquals("10", memory.get("a")?.value)
        Assert.assertEquals("7", memory.get("b")?.value)
        Assert.assertEquals("-36", memory.get("c")?.value)
    }

    @Test
    fun variableArithmetic() {
        val a = InitInstruction("a = 6 * (1 + 3)")
        val b = InitInstruction("b = a - 20")
        val c = InitInstruction("c = b * a - 50")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()
        
        val memory = interpreter.memory

        Assert.assertEquals("24", memory.get("a")?.value)
        Assert.assertEquals("4", memory.get("b")?.value)
        Assert.assertEquals("46", memory.get("c")?.value)
    }

    @Test
    fun unaryArithmetic() {
        val a = InitInstruction("a = -1 * -5")
        val b = InitInstruction("b = (-3 + -a) * -9")
        val c = InitInstruction("c = 100 * (-12 / -a * -(b)) / -(-3) / a / b")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("5", memory.get("a")?.value)
        Assert.assertEquals("72", memory.get("b")?.value)
        Assert.assertEquals("-13", memory.get("c")?.value)
    }

    @Test
    fun basicInequalityArithmetic() {
        val a = InitInstruction("a = -5")
        val b = InitInstruction("b = 3")
        val c = InitInstruction("c = a < b")
        val d = InitInstruction("d = a > b")

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("-5", memory.get("a")?.value)
        Assert.assertEquals("3", memory.get("b")?.value)

        Assert.assertEquals("true", memory.get("c")?.value)
        Assert.assertEquals(ValuableType.BOOL, memory.get("c")?.type)

        Assert.assertEquals("false", memory.get("d")?.value)
        Assert.assertEquals(ValuableType.BOOL, memory.get("d")?.type)
    }
}
