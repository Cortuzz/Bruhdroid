package com.example.bruhdroid.interpreter.rpn

import com.example.bruhdroid.model.Instruction
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Notation
import com.example.bruhdroid.model.Type
import com.example.bruhdroid.model.blocks.*
import org.junit.Assert.assertEquals
import org.junit.Test

class IntegerUnitTest {
    @Test
    fun addition() {
        val value = "2 + 3 + 5"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("2 3 + 5 + ", actual)
    }

    @Test
    fun subtraction() {
        val value = "7 - 1 - 13"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("7 1 - 13 - ", actual)
    }

    @Test
    fun additionAndSubtraction() {
        val value = "2 + 5 - 15 + 8 - 541"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("2 5 + 15 - 8 + 541 - ", actual)
    }

    @Test
    fun multiplying() {
        val value = "45 * 1 * 98"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("45 1 * 98 * ", actual)
    }

    @Test
    fun dividing() {
        val value = "94 / 6 / 32"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("94 6 / 32 / ", actual)
    }

    @Test
    fun multiplyingAndDividing() {
        val value = "94 * 43 * 13 / 2 / 8 * 5 / 7 * 0"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("94 43 * 13 * 2 / 8 / 5 * 7 / 0 * ", actual)
    }

    @Test
    fun basicOperators() {
        val value = "56 + 23 / 2 - 74 / 2 * 3 + 54 / 3 - 2 * 7"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("56 23 2 / 74 2 / 3 * 54 3 / 2 7 * - + - + ", actual)

    }

    @Test
    fun brackets() {
        val value = "(5 + 6) * 3 - (5 - 3) / 2"
        val value2 = "(23 + 3) * 9 - 3 * 15 * (2 - 3)"

        val actual2 = Notation.convertToRpn(Notation.normalizeString(value2))
        val actual = Notation.convertToRpn(Notation.normalizeString(value))

        assertEquals("5 6 + 3 * 5 3 - 2 / - ", actual)
        assertEquals("23 3 + 9 * 3 15 * 2 3 - * - ", actual2)
    }

    @Test
    fun define() {
        val value = "a = (5 + 6) * 3 - (5 - 3) / 2"
        val actual = Notation.convertToRpn(Notation.normalizeString(value))
        assertEquals("a 5 6 + 3 * 5 3 - 2 / - = ", actual)
    }
}
