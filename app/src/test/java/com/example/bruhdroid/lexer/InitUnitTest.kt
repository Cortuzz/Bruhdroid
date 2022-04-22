package com.example.bruhdroid.lexer

import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Lexer
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.*
import org.junit.Assert.assertEquals
import org.junit.Test


class InitUnitTest {
    @Test
    fun basicExpression() {
        val expression1 = Init(RawInput(" a   =   5 + 7   *1   - 3 "))
        val expression2 = Init(RawInput("    a= gt + 5  "))
        val expression3 = Init(RawInput(" a = f-a "))

        val blocks = listOf(expression1, expression2, expression3)

        var value = ""
        try {
            Lexer.checkBlocks(blocks)
        } catch (e: LexerError) {
            value = e.message.toString()
        }

        assertEquals("", value)
    }

    @Test
    fun multiplyInitialization() {
        val expression1 = Init(RawInput("a,b,c"))
        val expression2 = Init(RawInput("  a     , b "))
        val expression3 = Init(RawInput(" a,  b   "))

        val blocks = listOf(expression1, expression2, expression3)

        var value = ""
        try {
            Lexer.checkBlocks(blocks)
        } catch (e: LexerError) {
            value = e.message.toString()
        }

        assertEquals("", value)
    }

    @Test
    fun wrongMultiplyInitialization() {
        val correctError = "LEXER ERROR:\n" +
                "SyntaxError: Expected initialization but mutually exclusive symbols '=' and ',' was found\n" +
                "Line: 1, Instruction: INIT\n" +
                "SyntaxError: Expected multiply initialization but operator was found\n" +
                "Line: 2, Instruction: INIT\n" +
                "SyntaxError: Expected multiply initialization but operator was found\n" +
                "Line: 3, Instruction: INIT\n" +
                "SyntaxError: Expected multiply initialization but operator was found\n" +
                "Line: 4, Instruction: INIT\n" +
                "SyntaxError: Expected multiply initialization but operator was found\n" +
                "Line: 5, Instruction: INIT"

        val expression1 = Init(RawInput("a,b,c ="))
        val expression2 = Init(RawInput("  a     , b 56"))
        val expression3 = Init(RawInput(" a,  b_   "))
        val expression4 = Init(RawInput(" ,a,  b"))
        val expression5 = Init(RawInput("a,b,"))

        val blocks = listOf(expression1, expression2, expression3, expression4, expression5)

        var value = ""
        try {
            Lexer.checkBlocks(blocks)
        } catch (e: LexerError) {
            value = e.message.toString()
        }

        assertEquals(correctError, value)
    }

    @Test
    fun wrongInitialization() {
        val correctError = "LEXER ERROR:\n" +
                "SyntaxError: Expected initialization but wrong syntax was found\n" +
                "Line: 1, Instruction: INIT\n" +
                "SyntaxError: Expected initialization but wrong syntax was found\n" +
                "Line: 2, Instruction: INIT\n" +
                "SyntaxError: Expected initialization but wrong syntax was found\n" +
                "Line: 3, Instruction: INIT\n" +
                "SyntaxError: Expected initialization but wrong syntax was found\n" +
                "Line: 4, Instruction: INIT\n" +
                "SyntaxError: Expected initialization but wrong syntax was found\n" +
                "Line: 5, Instruction: INIT"

        val expression1 = Init(RawInput("a - b = c"))
        val expression2 = Init(RawInput(" a = 4 - "))
        val expression3 = Init(RawInput(" a = 4 - 2 +"))
        val expression4 = Init(RawInput(" a - = 5"))
        val expression5 = Init(RawInput("a == b"))

        val blocks = listOf(expression1, expression2, expression3, expression4, expression5)

        var value = ""
        try {
            Lexer.checkBlocks(blocks)
        } catch (e: LexerError) {
            value = e.message.toString()
        }

        assertEquals(correctError, value)
    }

    @Test
    fun emptyInitialization() {
        val correctError = "LEXER ERROR:\n" +
                "SyntaxError: Expected initialization but empty block was found\n" +
                "Line: 1, Instruction: INIT\n" +
                "SyntaxError: Expected initialization but declaration was not found\n" +
                "Line: 2, Instruction: INIT\n" +
                "SyntaxError: Expected initialization but declaration was not found\n" +
                "Line: 3, Instruction: INIT"

        val expression1 = Init(RawInput(""))
        val expression2 = Init(RawInput("       "))
        val expression3 = Init(RawInput("  b    54 + a "))

        val blocks = listOf(expression1, expression2, expression3)

        var value = ""
        try {
            Lexer.checkBlocks(blocks)
        } catch (e: LexerError) {
            value = e.message.toString()
        }

        assertEquals(correctError, value)
    }

    @Test
    fun bracketsExpression() {

    }
}