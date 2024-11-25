package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.valuable.ValuableType
import com.example.bruhdroid.model.blocks.instruction.InitInstruction
import com.example.bruhdroid.model.blocks.instruction.SetInstruction
import org.junit.Assert.assertEquals
import org.junit.Test


class OneLeveledUnitTest {
    @Test
    fun checkTypes() {
        val a = InitInstruction( "a = 5")
        val b = InitInstruction("b = 423")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals(ValuableType.INT, interpreter.memory.get("a")?.type)
        assertEquals(ValuableType.INT, interpreter.memory.get("b")?.type)
    }

    @Test
    fun variablesOverriding() {
        val a = InitInstruction( "a = 5")
        val b = InitInstruction("a = 423")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals("423", interpreter.memory.get("a")?.value)
    }

    @Test
    fun variableInitByVariable() {
        val a = InitInstruction( "a = 5")
        val b = InitInstruction("b = a + 5")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals("5", interpreter.memory.get("a")?.value)
        assertEquals("10", interpreter.memory.get("b")?.value)
    }

    @Test
    fun variableAssignToVariable() {
        val a = InitInstruction( "a = 5")
        val b1 = InitInstruction("b = 7")
        val b2 = SetInstruction("b = a - b")

        val interpreter = Interpreter(listOf(a, b1, b2))
        interpreter.run()

        assertEquals("5", interpreter.memory.get("a")?.value)
        assertEquals("-2", interpreter.memory.get("b")?.value)
    }

    @Test
    fun checkVariableReferenceAffect() {
        val a = InitInstruction( "a = 5")
        val b1 = InitInstruction("b = 7")
        val a1 = SetInstruction("a = b")
        val b2 = SetInstruction("b = 12")

        val interpreter = Interpreter(listOf(a, b1, a1, b2))
        interpreter.run()

        assertEquals("7", interpreter.memory.get("a")?.value)
        assertEquals("12", interpreter.memory.get("b")?.value)
    }
}
