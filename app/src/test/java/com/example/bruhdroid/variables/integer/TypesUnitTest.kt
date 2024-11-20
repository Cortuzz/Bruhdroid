package com.example.bruhdroid.variables.integer

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.valuable.StringValuable
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.IntegerValuable
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
        Assert.assertEquals(Type.INT, IntegerValuable(4).type)
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
