package com.example.bruhdroid.variables.string

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.Valuable
import org.junit.Assert
import org.junit.Test

class TypesUnitTest {
    private val value1 = "234"
    private val value2 = "abdm,mk45"

    private val a = Valuable(value1, Type.STRING)
    private val b = Valuable(value2, Type.STRING)

    private val integer = Valuable(3, Type.INT)

    @Test
    fun typeIsCorrect() {
        val value = "Fsf24f124%$@#"
        Assert.assertEquals(Type.STRING, Valuable(value, Type.STRING).type)
    }

    @Test
    fun stringPlusString() {
        Assert.assertEquals(Type.STRING, (a + b).type)
        Assert.assertEquals(Type.STRING, (b + a).type)
    }

    @Test
    fun stringTimesInt() {
        Assert.assertEquals(Type.STRING, (a * integer).type)
        Assert.assertEquals(Type.STRING, (b * integer).type)
    }
}
