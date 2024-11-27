package com.example.bruhdroid.variables.integer

import com.example.bruhdroid.model.blocks.valuable.ValuableType
import com.example.bruhdroid.model.blocks.valuable.StringValuable
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable
import org.junit.Assert
import org.junit.Test

class TypesUnitTest {
    private val value1 = 56
    private val value2 = 31

    private val a = IntegerValuable(value1)
    private val b = IntegerValuable(value2)

    private val str = StringValuable("ab")

    @Test
    fun typeIsCorrect() {
        Assert.assertEquals(ValuableType.INT, IntegerValuable(4).type)
    }

    @Test
    fun intPlusInt() {
        Assert.assertEquals(ValuableType.INT, (a + b).type)
    }

    @Test
    fun intMinusInt() {
        Assert.assertEquals(ValuableType.INT, (a - b).type)
    }

    @Test
    fun intTimesInt() {
        Assert.assertEquals(ValuableType.INT, (a * b).type)
    }

    @Test
    fun intTimesString() {
        Assert.assertEquals(ValuableType.STRING, (a * str).type)
        Assert.assertEquals(ValuableType.STRING, (b * str).type)
    }

    @Test
    fun intDivisionInt() {
        Assert.assertEquals(ValuableType.INT, (a / b).type)
    }

    @Test
    fun intReminderInt() {
        Assert.assertEquals(ValuableType.INT, (a % b).type)
    }
}
