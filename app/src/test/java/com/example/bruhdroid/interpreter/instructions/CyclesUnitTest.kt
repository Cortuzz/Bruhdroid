package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.EndWhileInstruction
import com.example.bruhdroid.model.blocks.instruction.InitInstruction
import com.example.bruhdroid.model.blocks.instruction.SetInstruction
import com.example.bruhdroid.model.blocks.instruction.WhileInstruction
import org.junit.Assert
import org.junit.Test

class CyclesUnitTest {
    @Test
    fun basicWhile() {
        val a = InitInstruction( "a = 1")
        val counter = InitInstruction( "count = 0")

        val b = WhileInstruction( "a < 100")
            val c = SetInstruction( "a = a * 2")
            val incCounter = SetInstruction( "count = count + 1")
        val d = EndWhileInstruction()

        val interpreter = Interpreter(listOf(a, counter, b, c, incCounter, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("128", memory.get("a")?.value)
        Assert.assertEquals("7", memory.get("count")?.value)
    }

    @Test
    fun whileNestedIntoWhile() {
        val a = InitInstruction( "a = 1")
        val counter = InitInstruction( "count = 0")

        val b = WhileInstruction( "a < 5000")
            val c = SetInstruction( "a = a * 2")

            val d = WhileInstruction( "count < 3")
                val e = SetInstruction( "a = a * 3")
                val e1 = SetInstruction( "count = count + 1")
            val f = EndWhileInstruction()

            val incCounter = SetInstruction( "count = count + 1")
        val g = EndWhileInstruction()

        val interpreter = Interpreter(listOf(a, counter, b, c, d, e, e1, f, incCounter, g))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("6912", memory.get("a")?.value)
        Assert.assertEquals("11", memory.get("count")?.value)
    }

    @Test
    fun falseWhile() {
        val a = InitInstruction( "a = 1")

        val b = WhileInstruction( "a > 100")
            val c = SetInstruction( "a = a * 2")
        val d = EndWhileInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        val memory = interpreter.memory

        Assert.assertEquals("1", memory.get("a")?.value)
    }
}
