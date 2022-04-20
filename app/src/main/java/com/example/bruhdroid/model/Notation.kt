package com.example.bruhdroid.model

class Notation {
    companion object {
        private enum class Operator(val operator: Char, val priority: Int) {
            MULTIPLY('*', 4), DIVIDE('/', 4),
            SUBTRACT('-', 3), ADD('+', 3),
            OPEN_BRACKET(')', 2), CLOSE_BRACKET(')', 2)
        }
        private val operators = mapOf(
            '-' to Operator.SUBTRACT, '+' to Operator.ADD, '*' to Operator.MULTIPLY,
            '/' to Operator.DIVIDE, '(' to Operator.OPEN_BRACKET, ')' to Operator.CLOSE_BRACKET)

        fun convertToRpn(infixNotation: String): String {
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

                    postfixNotation += " "
                    count--
                } else {
                    if (operators[infixNotation[count]] == Operator.OPEN_BRACKET) {
                        opStack.add(infixNotation[count])
                    } else if (operators[infixNotation[count]] == Operator.CLOSE_BRACKET) {
                        var s = opStack.removeLast()
                        while (operators[s] != Operator.OPEN_BRACKET)
                        {
                            postfixNotation += "$s "
                            s = opStack.removeLast()
                        }
                    } else {
                        if (opStack.size > 0)
                            if (operators[infixNotation[count]]!!.priority <=
                                operators[opStack.last()]!!.priority)

                                postfixNotation += opStack.removeLast() + " "

                        opStack.add(infixNotation[count])
                    }
                }

                count++
            }

            for (i in opStack.reversed()) {
                postfixNotation += "$i "
            }

            if (postfixNotation.length != 1) {
                return postfixNotation.dropLast(1)
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