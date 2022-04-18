package com.example.bruhdroid

import com.example.bruhdroid.blocks.variables.Integer
import com.example.bruhdroid.blocks.variables.Str
import org.junit.Assert
import org.junit.Test

class StringUnitTest {
    private val a = Str("a", "234")
    private val b = Str("b", "abdm,mk45")

    private val value1 = a.value
    private val value2 = b.value

    private val integer = Integer("c", 3)

    @Test
    fun valueIsCorrect() {
        val value = "Fsf24f124%$@#"
        Assert.assertEquals(value, Str("a", value).value)
    }

    @Test
    fun stringPlusString() {
        Assert.assertEquals(value1 + value2, (a + b).value)
        Assert.assertEquals(value2 + value1, (b + a).value)
    }

    @Test
    fun stringTimesInt() {
        Assert.assertEquals("234234234", (a * integer).value)
        Assert.assertEquals("abdm,mk45abdm,mk45abdm,mk45", (b * integer).value)
    }
}
