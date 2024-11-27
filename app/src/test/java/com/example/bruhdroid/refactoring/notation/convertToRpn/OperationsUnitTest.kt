package com.example.bruhdroid.refactoring.notation.convertToRpn

import com.example.bruhdroid.model.interpreter.InterpreterParser
import org.junit.Assert.assertEquals
import org.junit.Test

// Все тесты в этом классе используют неполное тестирование и угадывание ошибок
class OperationsUnitTest {
    @Test
    fun additionParsingIsCorrect() {
        // Дополнительно тестируется разделение на классы эквивалентности
        val input = listOf(
            "2", "+", "1", "+", "1123"
        )
        val expected = listOf(
            "2", "1", "+", "1123", "+"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun subtractionParsingIsCorrect() {
        // Дополнительно тестируется разделение на классы эквивалентности
        val input = listOf(
            "7", "-", "1", "-", "13"
        )
        val expected = listOf(
            "7", "1", "-", "13", "-"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun additionAndSubtractionParsingIsCorrect() {
        val input = listOf(
            "2", "+", "4", "-", "1123"
        )
        val expected = listOf(
            "2", "4", "+", "1123", "-"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun multiplicationParsingIsCorrect() {
        // Дополнительно тестируется разделение на классы эквивалентности
        val input = listOf(
            "43", "*", "32", "*", "5"
        )
        val expected = listOf(
            "43", "32", "*", "5", "*"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun divisionParsingIsCorrect() {
        // Дополнительно тестируется разделение на классы эквивалентности
        val input = listOf(
            "54", "/", "86", "/", "1123"
        )
        val expected = listOf(
            "54", "86", "/", "1123", "/"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun multiplicationAndDivisionParsingIsCorrect() {
        val input = listOf(
            "5646", "*", "546", "/", "1123"
        )
        val expected = listOf(
            "5646", "546", "*", "1123", "/"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun largeBasicArithmeticParsingIsCorrect() {
        val input = listOf(
            "56", "+", "23", "/", "2", "-", "74", "/", "2",
            "*", "3", "+", "54", "/", "3", "-", "2", "*", "7"
        )
        val expected = listOf(
            "56", "23", "2", "/", "74", "2", "/", "3", "*",
            "54","3", "/", "2", "7", "*", "-", "+", "-", "+"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun bracketsParsingIsCorrect() {
        val input = listOf(
            "(", "5", "+", "6", ")", "*", "3", "-", "(", "5", "-", "3", ")", "/", "2"
        )
        val expected = listOf(
            "5", "6", "+", "3", "*", "5", "3", "-", "2", "/", "-"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }
}
