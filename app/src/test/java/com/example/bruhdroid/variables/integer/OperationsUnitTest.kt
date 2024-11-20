package com.example.bruhdroid.variables.integer
import com.example.bruhdroid.model.blocks.valuable.StringValuable
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable
import org.junit.Test

import org.junit.Assert.*

class OperationsUnitTest {
    private val value1 = 2
    private val value2 = 3

    private val a = IntegerValuable(value1)
    private val b = IntegerValuable(value2)

    private val str = StringValuable("ab")

    @Test
    fun valueIsCorrect() {
        val value = 4
        assertEquals(value, IntegerValuable(value).value.toInt())
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
