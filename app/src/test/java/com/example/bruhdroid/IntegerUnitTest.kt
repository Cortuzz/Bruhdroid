package com.example.bruhdroid
import com.example.bruhdroid.blocks.variables.Integer
import com.example.bruhdroid.blocks.variables.Str
import org.junit.Test

import org.junit.Assert.*

class IntegerUnitTest {
    private val a = Integer("a", 2)
    private val b = Integer("b", 3)

    private val value1 = a.value
    private val value2 = b.value

    private val str = Str("c", "ab")

    @Test
    fun valueIsCorrect() {
        val value = 4
        assertEquals(value, Integer("a", value).value)
    }

    @Test
    fun intPlusInt() {
        assertEquals(value1 + value2, (a + b).value)
    }

    @Test
    fun intMinusInt() {
        assertEquals(value1 - value2, (a - b).value)
    }

    @Test
    fun intTimesInt() {
        assertEquals(value1 * value2, (a * b).value)
    }

    @Test
    fun intTimesString() {
        assertEquals("abab", (a * str).value)
        assertEquals("ababab", (b * str).value)
    }

    @Test
    fun intDivisionInt() {
        assertEquals(value1 / value2, (a / b).value)
    }

    @Test
    fun intReminderInt() {
        assertEquals(value1 % value2, (a % b).value)
    }
}
