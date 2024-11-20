package com.example.bruhdroid.interpreter

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.model.blocks.Block
import org.junit.Assert
import org.junit.Test

class LogicUnitTest {
    @Test
    fun integerLogic() {
        val a = Block(BlockInstruction.INIT,"a = 5 || 0")
        val b = Block(BlockInstruction.INIT,"b = 1 - 1 && 2")
        val c = Block(BlockInstruction.INIT,"c = a && b")

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
        val a = Block(BlockInstruction.INIT,"a = \"453\" || \"\"")
        val b = Block(BlockInstruction.INIT,"b = \"76\" && \"\"")

        val interpreter = Interpreter(listOf(a, b))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals(ValuableType.BOOL, memory.get("a")?.type)
        Assert.assertEquals(ValuableType.BOOL, memory.get("b")?.type)

        Assert.assertEquals("true", memory.get("a")?.value)
        Assert.assertEquals("false", memory.get("b")?.value)
    }
}
