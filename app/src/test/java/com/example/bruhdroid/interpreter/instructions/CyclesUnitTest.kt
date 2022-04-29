package com.example.bruhdroid.interpreter.instructions

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import org.junit.Assert
import org.junit.Test

class CyclesUnitTest {
    @Test
    fun basicWhile() {
        val a = Block(Instruction.INIT, "a = 1")
        val counter = Block(Instruction.INIT, "count = 0")

        val b = Block(Instruction.WHILE, "a < 100")
            val c = Block(Instruction.SET, "a = a * 2")
            val incCounter = Block(Instruction.SET, "count = count + 1")
        val d = Block(Instruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, counter, b, c, incCounter, d))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("128", memory["a"]?.value)
        Assert.assertEquals("7", memory["count"]?.value)
    }

    @Test
    fun whileNestedIntoWhile() {
        val a = Block(Instruction.INIT, "a = 1")
        val counter = Block(Instruction.INIT, "count = 0")

        val b = Block(Instruction.WHILE, "a < 5000")
            val c = Block(Instruction.SET, "a = a * 2")

            val d = Block(Instruction.WHILE, "count < 3")
                val e = Block(Instruction.SET, "a = a * 3")
                val e1 = Block(Instruction.SET, "count = count + 1")
            val f = Block(Instruction.END_WHILE)

            val incCounter = Block(Instruction.SET, "count = count + 1")
        val g = Block(Instruction.END_WHILE)

        val interpreter = Interpreter(listOf(a, counter, b, c, d, e, e1, f, incCounter, g))
        interpreter.run()

        val memory = interpreter.memory.stack

        Assert.assertEquals("6912", memory["a"]?.value)
        Assert.assertEquals("11", memory["count"]?.value)
    }
}
