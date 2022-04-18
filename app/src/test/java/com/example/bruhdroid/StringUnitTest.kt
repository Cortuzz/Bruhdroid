package com.example.bruhdroid

import com.example.bruhdroid.variables.Integer
import com.example.bruhdroid.variables.Str
import org.junit.Assert
import org.junit.Test

class StringUnitTest {
    private val value1 = "234"
    private val value2 = "abdm,mk45"

    private val a = Str("a", value1)
    private val b = Str("b", value2)
    private val integer = Integer("c", 3)

    @Test
    fun stringPlusString() {
        Assert.assertEquals(value1 + value2, a + b)
        Assert.assertEquals(value2 + value1, b + a)
    }

    @Test
    fun stringTimesInt() {
        Assert.assertEquals("234234234", a * integer)
        Assert.assertEquals("abdm,mk45abdm,mk45abdm,mk45", b * integer)
    }
}
