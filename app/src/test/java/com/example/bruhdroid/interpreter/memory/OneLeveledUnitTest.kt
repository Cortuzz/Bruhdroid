package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.*
import org.junit.Assert.assertEquals
import org.junit.Test


class OneLeveledUnitTest {
    @Test
    fun checkMemorySize() {
        val a = Init(RawInput("a = 5"))
        val b = Init(RawInput("b = 423"))
        val c = Init(RawInput("a = 75"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        assertEquals(2, interpreter.memory.stack.size)
    }

    @Test
    fun variableInit() {
        val a = Init(RawInput("a"))
        val b = Init(RawInput("b"))
        val c = Init(RawInput("a"))

        val interpreter = Interpreter(listOf(a, b, c ))
        interpreter.run()

        assertEquals(2, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals(Type.UNDEFINED, memory["a"]?.type)
        assertEquals(Type.UNDEFINED, memory["b"]?.type)
    }

    @Test
    fun multiplyVariableInit() {
        val a = Init(RawInput("a, b, c, d"))

        val interpreter = Interpreter(listOf(a))
        interpreter.run()

        assertEquals(4, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals(Type.UNDEFINED, memory["a"]?.type)
        assertEquals(Type.UNDEFINED, memory["b"]?.type)
        assertEquals(Type.UNDEFINED, memory["c"]?.type)
        assertEquals(Type.UNDEFINED, memory["d"]?.type)
    }

    @Test
    fun variableInitByValuable() {
        val a = Init(RawInput("a = 5"))
        val b = Init(RawInput("b = 423"))
        val c = Init(RawInput("a = 75"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        assertEquals(2, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals("75", memory["a"]?.value)
        assertEquals("423", memory["b"]?.value)
    }

    @Test
    fun variableInitByValuableArithmetic() {
        val a = Init(RawInput("a = 5 * 2"))
        val b = Init(RawInput("b = 5 + 1 * 2"))
        val c = Init(RawInput("c = 9 * (1 - 5)"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        assertEquals(3, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals("10", memory["a"]?.value)
        assertEquals("7", memory["b"]?.value)
        assertEquals("-36", memory["c"]?.value)
    }

    @Test
    fun variableInitByVariableArithmetic() {
        val a = Init(RawInput("a = 6 * (1 + 3)"))
        val b = Init(RawInput("b = a - 20"))
        val c = Init(RawInput("c = b * a - 50"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        assertEquals(3, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals("24", memory["a"]?.value)
        assertEquals("4", memory["b"]?.value)
        assertEquals("46", memory["c"]?.value)
    }

    @Test
    fun variableInitByPreviousValue() {
        val a = Init(RawInput("a = 6 * (1 + 3)"))
        val b = Init(RawInput("a =  a - 10"))

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals(1, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals("14", memory["a"]?.value)
    }
    //TODO
    /*
    @Test
    fun checkTypes() {
        val a = Init(RawInput("a = 5"))
        val b = Init(RawInput("b = 423"))
        val c = Init(RawInput("a = 75"))

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        assertEquals(2, interpreter.memory.stack.size)
        val memory = interpreter.memory.stack

        assertEquals(a.type, memory[name1]?.type)
        assertEquals(b.type, memory[name2]?.type)
    }

    @Test
    fun variablesOverriding() {
        val name1 = "variable1"

        val a = Valuable("543", Type.INT)
        val b = Valuable("97", Type.INT)

        val initA = Init(name1, a)
        val assertB = Assign(name1, b)

        val interpreter = Interpreter(listOf(initA, assertB))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(b.value, memory[name1]?.value)
        assertEquals(b.type, memory[name1]?.type)
    }

    @Test
    fun variableInitByVariable() {
        val name1 = "variable1"
        val name2 = "variable2"

        val b = Valuable("97", Type.INT)

        val initB = Init(name2, b)
        val initA = Init(name1, Variable(name2))

        val interpreter = Interpreter(listOf(initB, initA))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(b.value, memory[name1]?.value)
        assertEquals(b.value, memory[name2]?.value)
    }

    @Test
    fun variableAssignToVariable() {
        val name1 = "variable1"
        val name2 = "variable2"

        val a = Valuable("543", Type.INT)
        val b = Valuable("97", Type.INT)

        val initA = Init(name1, a)
        val initB = Init(name2, b)
        val assignA = Assign(name1, Variable(name2))

        val interpreter = Interpreter(listOf(initA, initB, assignA))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(b.value, memory[name1]?.value)
        assertEquals(b.value, memory[name2]?.value)
    }

    @Test
    fun checkVariableReferenceAffect() {
        val name1 = "variable1"
        val name2 = "variable2"

        val a = Valuable("543", Type.INT)
        val b = Valuable("97", Type.INT)
        val c = Valuable("871", Type.INT)

        val initA = Init(name1, a)
        val initB = Init(name2, b)
        val assignA = Assign(name1, Variable(name2))
        val assignB = Assign(name2, c)

        val interpreter = Interpreter(listOf(initA, initB, assignA, assignB))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(b.value, memory[name1]?.value)
        assertEquals(c.value, memory[name2]?.value)
    }

     */
}
