package com.example.bruhdroid.model


class Notation {
    companion object {
        private enum class Operator(val operator: Char, val priority: Int) {
            UNARY_MINUS('∓', 8), UNARY_PLUS('±', 8),
            MULTIPLY('*', 7), DIVIDE('/', 7),
            SUBTRACT('-', 6), ADD('+', 6),
            LESS('<', 5), GREATER('>', 5),
            NOT('!', 4), AND('&', 3), OR('|', 2),
            OPEN_BRACKET(')', 1), CLOSE_BRACKET(')', 1),
            DEFINE('=', 0)
        }
        private val operators = mapOf(
            '-' to Operator.SUBTRACT, '+' to Operator.ADD, '*' to Operator.MULTIPLY,
            '/' to Operator.DIVIDE, '(' to Operator.OPEN_BRACKET, ')' to Operator.CLOSE_BRACKET,
            '=' to Operator.DEFINE, '∓' to Operator.UNARY_MINUS, '±' to Operator.UNARY_PLUS,
            '!' to Operator.NOT, '&' to Operator.AND, '|' to Operator.OR, '<' to Operator.LESS,
            '>' to Operator.GREATER)

        fun convertToRpn(infixNotation: String): String {
            var mayUnary = true
            var postfixNotation = ""
            val opStack = mutableListOf<Char>()
            var count = 0

            while (count < infixNotation.length) {
                if (infixNotation[count] !in operators) {
                    while (infixNotation[count] !in operators) {
                        postfixNotation += infixNotation[count]
                        count++

                        if (count == infixNotation.length) {
                            break
                        }
                    }
                    mayUnary = false
                    postfixNotation += " "
                    count--
                } else {
                    when (operators[infixNotation[count]]) {
                        Operator.OPEN_BRACKET -> {
                            opStack.add(infixNotation[count])
                            mayUnary = true
                        }
                        Operator.CLOSE_BRACKET -> {
                            var s = opStack.removeLast()
                            while (operators[s] != Operator.OPEN_BRACKET) {
                                postfixNotation += "$s "
                                s = opStack.removeLast()
                            }
                            mayUnary = false
                        }
                        else -> {
                            if (mayUnary && infixNotation[count] in "+-") {
                                when (infixNotation[count]) {
                                    '-' -> opStack.add('∓')
                                    '+' -> opStack.add('±')
                                }
                                mayUnary = false
                                count++
                                continue
                            }
                            if (opStack.size > 0) {
                                while (opStack.last() == '±' || opStack.last() == '∓') {
                                    postfixNotation += opStack.removeLast() + " "
                                }
                                
                                if (operators[infixNotation[count]]!!.priority <=
                                    operators[opStack.last()]!!.priority) {
                                        postfixNotation += opStack.removeLast() + " "
                                }
                            }


                            opStack.add(infixNotation[count])
                            mayUnary = true
                        }
                    }
                }
                count++
            }

            for (i in opStack.reversed()) {
                postfixNotation += "$i "
            }

            return postfixNotation
        }

        fun normalizeString(str: String): String {
            var normalizedString = ""

            for (symbol in str) {
                if (symbol != ' ') {
                    normalizedString += symbol
                }
            }

            return normalizedString
        }
    }
}