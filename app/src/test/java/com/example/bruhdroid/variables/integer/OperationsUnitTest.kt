package com.example.bruhdroid.variables.integer
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Valuable
import org.junit.Test

import org.junit.Assert.*

class OperationsUnitTest {
    private val value1 = 2
    private val value2 = 3

    private val a = Valuable(value1, Type.INT)
    private val b = Valuable(value2, Type.INT)

    private val str = Valuable("ab", Type.STRING)

    @Test
    fun valueIsCorrect() {
        val value = 4
        assertEquals(value, Valuable(value, Type.INT).value.toInt())
    }

    @Test
    fun intPlusInt() {
        assertEquals(value1 + value2, (a + b).value.toInt())
    }

    @Test
    fun intMinusInt() {
        assertEquals(value1 - value2, (a - b).value.toInt())
    }

    @Test
    fun intTimesInt() {
        assertEquals(value1 * value2, (a * b).value.toInt())
    }

    @Test
    fun intTimesString() {
        assertEquals("abab", (a * str).value)
        assertEquals("ababab", (b * str).value)
    }

    @Test
    fun intDivisionInt() {
        assertEquals(value1 / value2, (a / b).value.toInt())
    }

    @Test
    fun intReminderInt() {
        assertEquals(value1 % value2, (a % b).value.toInt())
    }
}
