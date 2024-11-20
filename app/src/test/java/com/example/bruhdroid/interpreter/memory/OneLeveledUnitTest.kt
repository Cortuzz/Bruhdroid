package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.ValuableType
import org.junit.Assert.assertEquals
import org.junit.Test


class OneLeveledUnitTest {
    @Test
    fun checkTypes() {
        val a = Block(BlockInstruction.INIT, "a = 5")
        val b = Block(BlockInstruction.INIT,"b = 423")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals(ValuableType.INT, interpreter.memory.get("a")?.type)
        assertEquals(ValuableType.INT, interpreter.memory.get("b")?.type)
    }

    @Test
    fun variablesOverriding() {
        val a = Block(BlockInstruction.INIT, "a = 5")
        val b = Block(BlockInstruction.INIT,"a = 423")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals("423", interpreter.memory.get("a")?.value)
    }

    @Test
    fun variableInitByVariable() {
        val a = Block(BlockInstruction.INIT, "a = 5")
        val b = Block(BlockInstruction.INIT,"b = a + 5")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        assertEquals("5", interpreter.memory.get("a")?.value)
        assertEquals("10", interpreter.memory.get("b")?.value)
    }

    @Test
    fun variableAssignToVariable() {
        val a = Block(BlockInstruction.INIT, "a = 5")
        val b1 = Block(BlockInstruction.INIT,"b = 7")
        val b2 = Block(BlockInstruction.SET,"b = a - b")

        val interpreter = Interpreter(listOf(a, b1, b2))
        interpreter.run()

        assertEquals("5", interpreter.memory.get("a")?.value)
        assertEquals("-2", interpreter.memory.get("b")?.value)
    }

    @Test
    fun checkVariableReferenceAffect() {
        val a = Block(BlockInstruction.INIT, "a = 5")
        val b1 = Block(BlockInstruction.INIT,"b = 7")
        val a1 = Block(BlockInstruction.SET,"a = b")
        val b2 = Block(BlockInstruction.SET,"b = 12")

        val interpreter = Interpreter(listOf(a, b1, a1, b2))
        interpreter.run()

        assertEquals("7", interpreter.memory.get("a")?.value)
        assertEquals("12", interpreter.memory.get("b")?.value)
    }
}
