package com.example.bruhdroid.interpreter

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import org.junit.Assert
import org.junit.Test

class ArithmeticUnitTest {
    @Test
    fun valuableArithmetic() {
        val a = Block(Instruction.INIT,"a = 5 * 2")
        val b = Block(Instruction.INIT,"b = 5 + 1 * 2")
        val c = Block(Instruction.INIT,"c = 9 * (1 - 5)")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()
        
        val memory = interpreter.memory

        Assert.assertEquals("10", memory.get("a")?.value)
        Assert.assertEquals("7", memory.get("b")?.value)
        Assert.assertEquals("-36", memory.get("c")?.value)
    }

    @Test
    fun variableArithmetic() {
        val a = Block(Instruction.INIT,"a = 6 * (1 + 3)")
        val b = Block(Instruction.INIT,"b = a - 20")
        val c = Block(Instruction.INIT,"c = b * a - 50")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()
        
        val memory = interpreter.memory

        Assert.assertEquals("24", memory.get("a")?.value)
        Assert.assertEquals("4", memory.get("b")?.value)
        Assert.assertEquals("46", memory.get("c")?.value)
    }

    @Test
    fun unaryArithmetic() {
        val a = Block(Instruction.INIT,"a = -1 * -5")
        val b = Block(Instruction.INIT,"b = (-3 + -a) * -9")
        val c = Block(Instruction.INIT,"c = 100 * (-12 / -a * -(b)) / -(-3) / a / b")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("5", memory.get("a")?.value)
        Assert.assertEquals("72", memory.get("b")?.value)
        Assert.assertEquals("-13", memory.get("c")?.value)
    }

    @Test
    fun basicInequalityArithmetic() {
        val a = Block(Instruction.INIT,"a = -5")
        val b = Block(Instruction.INIT,"b = 3")
        val c = Block(Instruction.INIT,"c = a < b")
        val d = Block(Instruction.INIT,"d = a > b")

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("-5", memory.get("a")?.value)
        Assert.assertEquals("3", memory.get("b")?.value)

        Assert.assertEquals("true", memory.get("c")?.value)
        Assert.assertEquals(Type.BOOL, memory.get("c")?.type)

        Assert.assertEquals("false", memory.get("d")?.value)
        Assert.assertEquals(Type.BOOL, memory.get("d")?.type)
    }
}
