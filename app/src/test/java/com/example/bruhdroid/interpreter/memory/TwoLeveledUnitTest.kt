package com.example.bruhdroid.interpreter.memory

import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.instruction.cycle.EndWhileInstruction
import com.example.bruhdroid.model.blocks.instruction.InitInstruction
import com.example.bruhdroid.model.blocks.instruction.SetInstruction
import com.example.bruhdroid.model.blocks.instruction.cycle.WhileInstruction
import org.junit.Assert
import org.junit.Test

class TwoLeveledUnitTest {
    @Test
    fun falseWhile() {
        val a = InitInstruction( "a = 1")

        val b = WhileInstruction("a > 100")
        val c = SetInstruction("a = a * 2")
        val d = EndWhileInstruction()

        val interpreter = Interpreter(listOf(a, b, c, d))
        interpreter.run()

        Assert.assertEquals("1", interpreter.memory.get("a")?.value)
    }
}