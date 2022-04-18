package com.example.bruhdroid
import com.example.bruhdroid.variables.Integer
import com.example.bruhdroid.variables.Str
import org.junit.Test

import org.junit.Assert.*

class IntegerUnitTest {
    private val value1 = 2
    private val value2 = 3

    private val a = Integer("a", value1)
    private val b = Integer("b", value2)
    private val str = Str("c", "ab")

    @Test
    fun intPlusInt() {
        assertEquals(value1 + value2, a + b)
    }

    @Test
    fun intMinusInt() {
        assertEquals(value1 - value2, a - b)
    }

    @Test
    fun intTimesInt() {
        assertEquals(value1 * value2, a * b)
    }

    @Test
    fun intTimesString() {
        assertEquals("abab", a * str)
        assertEquals("ababab", b * str)
    }

    @Test
    fun intDivisionInt() {
        assertEquals(value1 / value2, a / b)
    }

    @Test
    fun intReminderInt() {
        assertEquals(value1 % value2, a % b)
    }
}
