package com.example.bruhdroid.refactoring.memory;

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Memory
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.blocks.Valuable
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.Test

// Здесь мы применяем предугадывание ошибок и базисное тестирование
class MemoryUnitTest {
    @Test
    fun memoryVariableValueNotChanges() {
        // Предугадывание ошибок
        val memory = Memory(null, "TEST SCOPE")
        val block = Valuable("5", Type.INT)

        memory.push("test", block)

        assertEquals(
            block.value,
            memory.get("test")!!.value
        )
    }

    @Test
    fun memoryVariableTypeNotChanges() {
        // Предугадывание ошибок
        val memory = Memory(null, "TEST SCOPE")
        val block = Valuable("5", Type.INT)

        memory.push("test", block)

        assertEquals(
            block.type,
            memory.get("test")!!.type
        )
    }

    @Test
    fun memoryAccessesPrimitiveVariableType() {
        // Базисное тестирование
        val memory = Memory(null, "TEST SCOPE")
        val block = mockk<Valuable>()
        var typeAccessCounter = 0

        every { block.type } answers  {
            ++typeAccessCounter
            Type.INT
        }

        memory.push("test", block)

        assertEquals(
            true,
            typeAccessCounter > 0
        )
    }

    @Test
    fun memoryNotAccessesPrimitiveVariableValue() {
        // Базисное тестирование
        val memory = Memory(null, "TEST SCOPE")
        val block = mockk<Valuable>()
        var valueAccessCounter = 0

        every { block.value } answers  {
            ++valueAccessCounter
            "435"
        }
        every { block.type } answers  {
            Type.INT
        }

        memory.push("test", block)

        assertEquals(
            0,
            valueAccessCounter
        )
    }
}
