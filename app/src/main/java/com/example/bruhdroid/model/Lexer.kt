package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.blocks.Block
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.SyntaxError

class Lexer {
    companion object {
        private var totalLines = 0
        private const val s = "(\\s)*"
        private const val w = "(([a-zA-Z0-9]+)|([\"].*[\"])|([0-9]+[.][0-9]+))" // todo: not "
        private const val rawInputRegex = "$s[-+]?$s[(]*[-+]?$s[-]?$w$s" +
                "($s(([&|<>+*/-])|([!=<>]=))$s[-+]?$s[(]*$s[-+]?$s[-+]?$s$w$s[)]*$s)*$s[)]*$s"

        fun checkBlocks(sequence: List<Block>) {
            var errors = ""
            totalLines = 0
            for (block in sequence) {
                totalLines++
                block.line = totalLines

                try {
                    when (block.instruction) {
                        Instruction.INIT -> checkInit(block.expression)
                        Instruction.PRINT -> checkPrint(block.expression)
                    }
                } catch (e: SyntaxError) {
                    errors += "${e.message}Line: ${block.line}, " +
                            "Instruction: ${block.instruction}\n\n"
                }
            }
            if (errors.isNotEmpty()) {
                throw LexerError(errors)
            }
        }

        private fun checkInit(str: String) {
            if (str.contains(',') && str.contains('=')) {
                throw SyntaxError("Expected initialization " +
                        "but mutually exclusive symbols = and , was found\n")
            }
            if (str.contains(',')) {
                if (!str.contains("^$s$w$s(,$s$w$s)+\$".toRegex())) {
                    throw SyntaxError("Expected multiply initialization but operator was found\n")
                }
                return
            }
            if (str.contains('=')) {

                // TODO: New operators
                if (!str.contains("^($s[a-zA-Z]+$s)=$rawInputRegex$".toRegex()) &&
                    (checkBrackets(str))) {
                    throw SyntaxError("Expected initialization but wrong syntax was found\n")
                }

                return
            }
            if (str.isNotEmpty()) {
                if (!checkVariable(str)) {
                    throw SyntaxError("Expected initialization but declaration was not found\n")
                }
                return
            }
            throw SyntaxError("Expected initialization but empty block was found\n")
        }

        private fun checkBrackets(str: String): Boolean {
            var openBrackets = 0
            var closedBrackets = 0

            var apostrophes = 0

            for (symbol in str) {
                when (symbol) {
                    '(' -> openBrackets++
                    ')' -> closedBrackets++
                    '"' -> apostrophes++
                }
            }

            return ((openBrackets == closedBrackets) && apostrophes % 2 == 0)
        }

        private fun checkVariable(str: String): Boolean {
            if (str.contains("[a-zA-Z]".toRegex()) && str.contains("^(\\s)*([a-zA-Z0-9])+(\\s)*$".toRegex())) {
                return true
            }
            return false
        }

        private fun checkPrint(str: String) {
            if (str.contains("^$rawInputRegex($s,$rawInputRegex)*\$".toRegex()) && checkBrackets(str)) {
                return
            }
            if (str.isNotEmpty()) {
                throw SyntaxError("Expected output but wrong syntax was found\n")
            }
            throw SyntaxError("Expected output but empty expression was found\n")
        }
    }
}