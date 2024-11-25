package com.example.bruhdroid.refactoring.notation.convertToRpn

import com.example.bruhdroid.model.interpreter.InterpreterParser
import org.junit.Assert.assertEquals
import org.junit.Test

// Тесты, предназначенные для базисного тестирования
class StructuredBasisUnitTest {

    @Test
    fun emptyArrayNotChanges() {
        val input = listOf<String>()
        val expected = listOf<String>()

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun emptyElementRemoves() {
        val input = listOf(
            "", "4", " "
        )
        val expected = listOf(
            "4", " "
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun nonOperatorElementStringNotChanges() {
        val input = listOf(
            "fdg34gh", "$$#$(sdf43", "3t4greg", "546", "false", "54.2"
        )
        val expected = listOf(
            "fdg34gh", "$$#$(sdf43", "3t4greg", "546", "false", "54.2"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun leftBracketStringNotChanges() {
        val input = listOf(
            "(", "(",
        )
        val expected = listOf(
            "(", "("
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun leftIndexSymbolStringNotChanges() {
        val input = listOf(
            "[", "[",
        )
        val expected = listOf(
            "[", "["
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test(expected = NoSuchElementException::class)
    fun standaloneRightBracketStringThrowsError() {
        val input = listOf(
            ")",
        )

        InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
    }



    @Test(expected = NoSuchElementException::class)
    fun standaloneRightIndexSymbolStringThrowsError() {
        val input = listOf(
            "]",
        )

        InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
    }

    @Test
    fun rightBracketAddsAllPreviousOperatorsAfterLeftBracket() {
        // Дополнительно тестируются и потоки данных
        val input = listOf(
            "1", "(", "a", "/", "b", ")"
        )
        val expected = listOf(
            "1", "a", "b", "/"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun rightIndexSymbolAddsAllPreviousOperatorsAndDefineByIndexSymbolAfterLeftIndexSymbol() {
        // Дополнительно тестируются и потоки данных
        val input = listOf(
            "1", "[", "a", "/", "b", "]"
        )
        val expected = listOf(
            "1", "a", "b", "/", "?"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun rightIndexSymbolWithArrayInitAddsAllPreviousOperatorsAfterLeftIndexSymbol() {
        // Дополнительно тестируются и потоки данных
        val input = listOf(
            "*", "[", "a", "/", "b", "]"
        )
        val expected = listOf(
            "a", "b", "/", "#"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun minusWithOneOperandConvertsToUnaryMinus() {
        val input = listOf(
            "-", "4"
        )
        val expected = listOf(
            "4", "∓"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun plusWithOneOperandConvertsToUnaryPlus() {
        val input = listOf(
            "+", "4"
        )
        val expected = listOf(
            "4", "±"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun asteriskWithOneOperandConvertsArrayInitialization() {
        val input = listOf(
            "*", "4"
        )
        val expected = listOf(
            "4", "#"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }

    @Test
    fun higherPrioritySymbolToTheLeftOfAnotherSymbol() {
        // Дополнительно тестируются и потоки данных
        val input = listOf(
            "4", "+", "1", "*", "2"
        )
        val expected = listOf(
            "4", "1", "2", "*", "+"
        )

        assertEquals(
            expected,
            InterpreterParser.convertInfixToPostfixNotation(input).map { op -> op.operation }
        )
    }
}
