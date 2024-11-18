package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.*
import org.junit.Assert.assertEquals
import org.junit.Test


class OneLeveledUnitTest {
    @Test
    fun checkTypes() {
        val a = Block(Instruction.INIT, "a = 5")
        val b = Block(Instruction.INIT,"b = 423")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals(Type.INT, interpreter.memory.get("a")?.type)
        assertEquals(Type.INT, interpreter.memory.get("b")?.type)
    }

    @Test
    fun variablesOverriding() {
        val a = Block(Instruction.INIT, "a = 5")
        val b = Block(Instruction.INIT,"a = 423")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals("423", interpreter.memory.get("a")?.value)
    }

    @Test
    fun variableInitByVariable() {
        val a = Block(Instruction.INIT, "a = 5")
        val b = Block(Instruction.INIT,"b = a + 5")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals("5", interpreter.memory.get("a")?.value)
        assertEquals("10", interpreter.memory.get("b")?.value)
    }

    @Test
    fun variableAssignToVariable() {
        val a = Block(Instruction.INIT, "a = 5")
        val b1 = Block(Instruction.INIT,"b = 7")
        val b2 = Block(Instruction.SET,"b = a - b")

        val interpreter = Interpreter(listOf(a, b1, b2))
        interpreter.run()

        assertEquals("5", interpreter.memory.get("a")?.value)
        assertEquals("-2", interpreter.memory.get("b")?.value)
    }

    @Test
    fun checkVariableReferenceAffect() {
        val a = Block(Instruction.INIT, "a = 5")
        val b1 = Block(Instruction.INIT,"b = 7")
        val a1 = Block(Instruction.SET,"a = b")
        val b2 = Block(Instruction.SET,"b = 12")

        val interpreter = Interpreter(listOf(a, b1, a1, b2))
        interpreter.run()

        assertEquals("7", interpreter.memory.get("a")?.value)
        assertEquals("12", interpreter.memory.get("b")?.value)
    }
}
