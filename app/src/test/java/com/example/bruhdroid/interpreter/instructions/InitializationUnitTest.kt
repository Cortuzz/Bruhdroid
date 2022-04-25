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
        val a = Block(Instruction.INIT,"a")
        val b = Block(Instruction.INIT,"b")
        val c = Block(Instruction.INIT,"a")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        Assert.assertEquals(2, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals(Type.UNDEFINED, memory["a"]?.type)
        Assert.assertEquals(Type.UNDEFINED, memory["b"]?.type)
    }

    @Test
    fun multiplyVariableInit() {
        val a = Block(Instruction.INIT,"a, b, c, d")

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
        val a = Block(Instruction.INIT,"a = 5")
        val b = Block(Instruction.INIT,"b = 423")
        val c = Block(Instruction.INIT,"a = 75")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory.stack
        Assert.assertEquals(2, memory.size)

        Assert.assertEquals("75", memory["a"]?.value)
        Assert.assertEquals("423", memory["b"]?.value)
    }

    @Test
    fun variableInitByPreviousValue() {
        val a = Block(Instruction.INIT,"a = 6 * (1 + 3)")
        val b = Block(Instruction.INIT,"a =  a - 10")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        Assert.assertEquals(1, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        Assert.assertEquals("14", memory["a"]?.value)
    }

    @Test
    fun variableInitWithUnaryMinus() {
        val a = Block(Instruction.INIT,"a = -5")
        val b = Block(Instruction.INIT,"b = -a")
        val c = Block(Instruction.INIT,"c = -b * -a")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("-5", memory["a"]?.value)
        Assert.assertEquals("5", memory["b"]?.value)
        Assert.assertEquals("-25", memory["c"]?.value)
    }
}