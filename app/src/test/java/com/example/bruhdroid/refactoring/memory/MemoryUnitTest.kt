package com.example.bruhdroid.refactoring.memory;

import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.blocks.valuable.ValuableType
import com.example.bruhdroid.model.blocks.valuable.Valuable
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable
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
        val block = IntegerValuable("5")

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
        val block = IntegerValuable("5")

        memory.push("test", block)

        assertEquals(
            block.type,
            memory.get("test")!!.type
        )
    }

    @Test
    fun memoryNotAccessesPrimitiveVariableType() {
        // Базисное тестирование
        val memory = Memory(null, "TEST SCOPE")
        val block = mockk<Valuable>()
        var typeAccessCounter = 0

        every { block.type } answers  {
            ++typeAccessCounter
            ValuableType.INT
        }

        memory.push("test", block)

        // Изменен из-за изменения алгоритма
        assertEquals(
            0,
            typeAccessCounter
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
            ValuableType.INT
        }

        memory.push("test", block)

        assertEquals(
            0,
            valueAccessCounter
        )
    }
}
