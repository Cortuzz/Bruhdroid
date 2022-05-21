package com.example.bruhdroid.interpreter.rpn

import com.example.bruhdroid.model.Notation
import org.junit.Test
import org.junit.Assert.assertEquals

class BasicUnitTest {
    @Test
    fun normalizing() {
        val string = " 3 +  a    +   b  *  5 - t     "
        assertEquals("3+a+b*5-t", Notation.tokenizeString(string))
    }

    @Test
    fun oneNumber() {
        val string = "3"
        assertEquals("3 ", Notation.convertToRpn(Notation.tokenizeString(string)))
    }
}