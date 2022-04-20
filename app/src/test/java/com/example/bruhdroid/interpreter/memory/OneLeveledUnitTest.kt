package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.Instruction
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Type
import com.example.bruhdroid.model.blocks.*
import org.junit.Assert.assertEquals
import org.junit.Test

class OneLeveledUnitTest {
    @Test
    fun checkMemorySize() {
        val name1 = "variable1"
        val name2 = "variable2"
        val name3 = "variable1"

        val a = Valuable("543", Type.INT)
        val b = Valuable("fdg", Type.STRING)
        val c = Valuable("43gtrfg", Type.STRING)

        val initA = Init(name1, a)
        val initB = Init(name2, b)
        val initC = Init(name3, c)

        val interpreter = Interpreter(listOf(initA, initB, initC))
        interpreter.run()

        assertEquals(2, interpreter.memory.stack.size)
    }

    @Test
    fun variableInitByValuable() {
        val name1 = "variable1"
        val name2 = "variable2"
        val name3 = "variable3"

        val a = Valuable("543", Type.INT)
        val b = Valuable("97", Type.INT)
        val c = Valuable("8u7", Type.STRING)

        val initA = Init(name1, a)
        val initB = Init(name2, b)
        val initC = Init(name3, c)

        val interpreter = Interpreter(listOf(initA, initB, initC))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(a, memory[name1])
        assertEquals(b, memory[name2])
        assertEquals(c, memory[name3])
    }

    @Test
    fun variableInitByValuableArithmetic() {
        val a = Valuable("543", Type.INT)
        val b = Valuable("97", Type.INT)
        val c = Valuable("413", Type.INT)

        val sum = Block(Instruction.PLUS, a, b)
        val multiply = Block(Instruction.MUL, sum, c)

        val initRes = Init("variable", multiply)

        val interpreter = Interpreter(listOf(initRes))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(1, memory.size)
        assertEquals("264320", memory["variable"]?.value)
    }

    @Test
    fun variableInitByVariableArithmetic() {
        val a = Valuable("543", Type.INT)
        val b = Valuable("97", Type.INT)
        val c = Valuable("413", Type.INT)

        val initA = Init("variable1", a)
        val initC = Init("variable3", c)

        val varA = Variable("variable1")
        val varC = Variable("variable3")

        val sum = Block(Instruction.PLUS, varA, b)
        val multiply = Block(Instruction.MUL, sum, varC)

        val initRes = Init("res1", multiply)

        val interpreter = Interpreter(listOf(initA, initC, initRes))
        interpreter.run()
        val memory = interpreter.memory.stack

        assertEquals(3, memory.size)
        assertEquals("264320", memory["res1"]?.value)
    }

    @Test
    fun checkTypes() {
        val name1 = "variable1"
        val name2 = "variable2"

        val a = Valuable("543", Type.INT)
        val b = Valuable("fdg", Type.STRING)

        val initA = Init(name1, a)
        val initB = Init(name2, b)

        val interpreter = Interpreter(listOf(initA, initB))
        interpreter.run()
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
}
