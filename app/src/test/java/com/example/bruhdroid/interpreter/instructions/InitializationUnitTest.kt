package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import org.junit.Assert
import org.junit.Test

class InitializationUnitTest {
    @Test
    fun variableInit() {
        val a = Block(Instruction.INIT,"a = 1")
        val b = Block(Instruction.INIT,"b = 2")
        val c = Block(Instruction.INIT,"a = 3")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("3", memory.get("a")?.value)
        Assert.assertEquals("2", memory.get("b")?.value)
    }

    @Test
    fun multiplyVariableInit() {
        val a = Block(Instruction.INIT,"a = 1, b = 2.45, c = \"45\", d = \"3,2\"")

        val interpreter = Interpreter(listOf(a))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("1", memory.get("a")?.value)
        Assert.assertEquals("2.45", memory.get("b")?.value)
        Assert.assertEquals("45", memory.get("c")?.value)
        Assert.assertEquals("3,2", memory.get("d")?.value)

        Assert.assertEquals(Type.INT, memory.get("a")?.type)
        Assert.assertEquals(Type.FLOAT, memory.get("b")?.type)
        Assert.assertEquals(Type.STRING, memory.get("c")?.type)
        Assert.assertEquals(Type.STRING, memory.get("d")?.type)
    }

    @Test
    fun variableInitByValuable() {
        val a = Block(Instruction.INIT,"a = 5")
        val b = Block(Instruction.INIT,"b = 423")
        val c = Block(Instruction.INIT,"a = 75")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("75", memory.get("a")?.value)
        Assert.assertEquals("423", memory.get("b")?.value)
    }

    @Test
    fun variableInitByPreviousValue() {
        val a = Block(Instruction.INIT,"a = 6 * (1 + 3)")
        val b = Block(Instruction.INIT,"a =  a - 10")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("14", memory.get("a")?.value)
    }

    @Test
    fun variableInitWithUnaryMinus() {
        val a = Block(Instruction.INIT,"a = -5")
        val b = Block(Instruction.INIT,"b = -a")
        val c = Block(Instruction.INIT,"c = -b * -a")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("-5", memory.get("a")?.value)
        Assert.assertEquals("5", memory.get("b")?.value)
        Assert.assertEquals("-25", memory.get("c")?.value)
    }
}