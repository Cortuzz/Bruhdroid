package com.example.bruhdroid.refactoring.notation.convertToRpn

import com.example.bruhdroid.model.Notation
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
        )
    }

    @Test(expected = NoSuchElementException::class)
    fun standaloneRightBracketStringThrowsError() {
        val input = listOf(
            ")",
        )

        Notation.convertInfixToPostfixNotation(input)
    }



    @Test(expected = NoSuchElementException::class)
    fun standaloneRightIndexSymbolStringThrowsError() {
        val input = listOf(
            "]",
        )

        Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
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
            Notation.convertInfixToPostfixNotation(input)
        )
    }
}
