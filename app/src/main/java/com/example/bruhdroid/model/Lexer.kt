package com.example.bruhdroid.model

import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.Init
import com.example.bruhdroid.model.blocks.RawInput

class Lexer {
    companion object {
        var totalLines = 0

        fun checkBlocks(sequence: List<Block>) {
            var errors = ""
            totalLines = 0
            for (block in sequence) {
                totalLines++
                block.line = totalLines

                try {
                    when (block.instruction) {
                        Instruction.INIT -> checkInit(block as Init)
                    }
                } catch (e: SyntaxError) {
                    errors += "${e.message}Line: ${block.line}, " +
                            "Instruction: ${block.instruction}"
                }
            }
            if (errors.isNotEmpty()) {
                throw LexerError(errors)
            }
        }

        private fun checkInit(block: Init) {
            val w = "[a-zA-Z0-9]"
            val s = "(\\s)*"
            val input = block.body as RawInput
            val str = input.input

            if (str.contains(',') && str.contains('=')) {
                throw SyntaxError("Expected initialization " +
                        "but mutually exclusive symbols '=' and ',' was found\n")
            }
            if (str.contains(',')) {
                if (!str.contains("^$s$w+$s(,$s$w+$s)+\$".toRegex())) {
                    throw SyntaxError("Expected multiply initialization but operator was found\n")
                }
                return
            }
            if (str.contains('=')) {
                var open = 0
                var closed = 0
                for (symbol in str) {
                    when (symbol) {
                        '(' -> open++
                        ')' -> closed++
                    }
                }
                // TODO: New operators
                if (!str.contains("^($s[a-zA-Z]+$s)=$s[-]?$s[(]*$s$w+$s($s[+*/-]$s[(]*$s$w+$s[)]*$s)*$s[)]*$s\$".toRegex()) ||
                    (open != closed)) {
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

        private fun checkVariable(str: String): Boolean {
            if (str.contains("[a-zA-Z]".toRegex()) && str.contains("^(\\s)*([a-zA-Z0-9])+(\\s)*$".toRegex())) {
                return true
            }
            return false
        }

        private fun checkRawInput(block: RawInput) {

        }
    }
}