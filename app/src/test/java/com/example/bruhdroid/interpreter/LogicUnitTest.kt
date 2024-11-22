package com.example.bruhdroid.interpreter

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.model.blocks.instruction.InitInstruction
import org.junit.Assert
import org.junit.Test

class LogicUnitTest {
    @Test
    fun integerLogic() {
        val a = InitInstruction("a = 5 || 0")
        val b = InitInstruction("b = 1 - 1 && 2")
        val c = InitInstruction("c = a && b")

        val interpreter = Interpreter(listOf(a, b, c))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals(ValuableType.BOOL, memory.get("a")?.type)
        Assert.assertEquals(ValuableType.BOOL, memory.get("b")?.type)
        Assert.assertEquals(ValuableType.BOOL, memory.get("c")?.type)

        Assert.assertEquals("true", memory.get("a")?.value)
        Assert.assertEquals("false", memory.get("b")?.value)
        Assert.assertEquals("false", memory.get("c")?.value)
    }

    @Test
    fun stringLogic() {
        val a = InitInstruction("a = \"453\" || \"\"")
        val b = InitInstruction("b = \"76\" && \"\"")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals(ValuableType.BOOL, memory.get("a")?.type)
        Assert.assertEquals(ValuableType.BOOL, memory.get("b")?.type)

        Assert.assertEquals("true", memory.get("a")?.value)
        Assert.assertEquals("false", memory.get("b")?.value)
    }
}
