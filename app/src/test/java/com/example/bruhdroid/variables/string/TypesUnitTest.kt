package com.example.bruhdroid.variables.string

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.valuable.StringValuable
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.IntegerValuable
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
        Assert.assertEquals(Type.STRING, StringValuable(value).type)
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
