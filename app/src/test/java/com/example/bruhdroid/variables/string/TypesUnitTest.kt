package com.example.bruhdroid.variables.string

import com.example.bruhdroid.model.blocks.valuable.ValuableType
import com.example.bruhdroid.model.blocks.valuable.StringValuable
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable
import org.junit.Assert
import org.junit.Test

class TypesUnitTest {
    private val value1 = "234"
    private val value2 = "abdm,mk45"

    private val a = StringValuable(value1)
    private val b = StringValuable(value2)

    private val integer = IntegerValuable(3)

    @Test
    fun typeIsCorrect() {
        val value = "Fsf24f124%$@#"
        Assert.assertEquals(ValuableType.STRING, StringValuable(value).type)
    }

    @Test
    fun stringPlusString() {
        Assert.assertEquals(ValuableType.STRING, (a + b).type)
        Assert.assertEquals(ValuableType.STRING, (b + a).type)
    }

    @Test
    fun stringTimesInt() {
        Assert.assertEquals(ValuableType.STRING, (a * integer).type)
        Assert.assertEquals(ValuableType.STRING, (b * integer).type)
    }
}
