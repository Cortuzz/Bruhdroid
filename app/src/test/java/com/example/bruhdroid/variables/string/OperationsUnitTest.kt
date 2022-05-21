package com.example.bruhdroid.variables.string

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Valuable
import org.junit.Assert
import org.junit.Test

class OperationsUnitTest {
    private val value1 = "234"
    private val value2 = "abdm,mk45"

    private val a = Valuable(value1, Type.STRING)
    private val b = Valuable(value2, Type.STRING)

    private val integer = Valuable(3, Type.INT)

    @Test
    fun valueIsCorrect() {
        val value = "Fsf24f124%$@#"
        Assert.assertEquals(value, Valuable(value, Type.STRING).value)
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
