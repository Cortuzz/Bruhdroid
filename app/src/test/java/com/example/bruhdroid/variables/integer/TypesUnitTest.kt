package com.example.bruhdroid.variables.integer

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Valuable
import org.junit.Assert
import org.junit.Test

class TypesUnitTest {
    private val value1 = 56
    private val value2 = 31

    private val a = Valuable(value1, Type.INT)
    private val b = Valuable(value2, Type.INT)

    private val str = Valuable("ab", Type.STRING)

    @Test
    fun typeIsCorrect() {
        Assert.assertEquals(Type.INT, Valuable(4, Type.INT).type)
    }

    @Test
    fun intPlusInt() {
        Assert.assertEquals(Type.INT, (a + b).type)
    }

    @Test
    fun intMinusInt() {
        Assert.assertEquals(Type.INT, (a - b).type)
    }

    @Test
    fun intTimesInt() {
        Assert.assertEquals(Type.INT, (a * b).type)
    }

    @Test
    fun intTimesString() {
        Assert.assertEquals(Type.STRING, (a * str).type)
        Assert.assertEquals(Type.STRING, (b * str).type)
    }

    @Test
    fun intDivisionInt() {
        Assert.assertEquals(Type.INT, (a / b).type)
    }

    @Test
    fun intReminderInt() {
        Assert.assertEquals(Type.INT, (a % b).type)
    }
}
