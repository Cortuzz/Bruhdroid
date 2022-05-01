package com.example.bruhdroid.model


class Notation {
    companion object {
        private enum class Operator(val operator: Char, val priority: Int) {
            UNARY_MINUS('∓', 9), UNARY_PLUS('±', 9),
            DEFINE_BY_INDEX('?', 8),
            MULTIPLY('*', 7), DIVIDE('/', 7),
            SUBTRACT('-', 6), ADD('+', 6),
            EQUALS('=', 5), NOT_EQUALS('≠', 5),
            LESS('<', 5), GREATER('>', 5),
            LESS_OR_EQUALS('≤', 5), GREATER_OR_EQUALS('≥', 5),
            NOT('!', 4), AND('&', 3), OR('|', 2),
            OPEN_BRACKET('(', 0), CLOSE_BRACKET(')', 0),
            OPEN_INDEX('[', 0), CLOSE_INDEX(']', 0),
            INIT_ARRAY('#', -1), DEFINE('≈', -2)
        }
        private val operators = mapOf(
            '-' to Operator.SUBTRACT, '+' to Operator.ADD, '*' to Operator.MULTIPLY,
            '/' to Operator.DIVIDE, '(' to Operator.OPEN_BRACKET, ')' to Operator.CLOSE_BRACKET,
            '≈' to Operator.DEFINE, '∓' to Operator.UNARY_MINUS, '±' to Operator.UNARY_PLUS,
            '!' to Operator.NOT, '&' to Operator.AND, '|' to Operator.OR, '<' to Operator.LESS,
            '>' to Operator.GREATER, '=' to Operator.EQUALS, '≠' to Operator.NOT_EQUALS,
            '≤' to Operator.LESS_OR_EQUALS, '≥' to Operator.GREATER_OR_EQUALS,
            '[' to Operator.OPEN_INDEX, ']' to Operator.CLOSE_INDEX, '?' to Operator.DEFINE_BY_INDEX,
            '#' to Operator.INIT_ARRAY)

        fun convertToRpn(infixNotation: String): String {
            var mayUnary = true
            var arrayInit = false
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
                        Operator.OPEN_INDEX -> {
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
                        Operator.CLOSE_INDEX -> {
                            var s = opStack.removeLast()
                            while (operators[s] != Operator.OPEN_INDEX) {
                                postfixNotation += "$s "
                                s = opStack.removeLast()
                            }
                            postfixNotation += when(arrayInit) {
                                true -> ""
                                false -> "? "
                            }
                            arrayInit = false
                            mayUnary = false
                        }
                        else -> {
                            if (mayUnary && infixNotation[count] in "+-*") {
                                when (infixNotation[count]) {
                                    '-' -> opStack.add('∓')
                                    '+' -> opStack.add('±')
                                    '*' -> {
                                        opStack.add('#')
                                        arrayInit = true
                                    }
                                }
                                mayUnary = false
                                count++
                                continue
                            }

                            while (opStack.size > 0 && opStack.last() in "±∓#") {
                                postfixNotation += opStack.removeLast() + " "
                            }
                            if (opStack.size > 0 && operators[infixNotation[count]]!!.priority <=
                                operators[opStack.last()]!!.priority) {
                                    postfixNotation += opStack.removeLast() + " "
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
            var i = 0
            var isString = false

            while (i < str.length) {
                var symbol = str[i]
                if (symbol != ' ' || isString) {
                    if (symbol == '"') {
                        isString = !isString
                    }

                    if (symbol == '=') {
                        if (str[i + 1] == '=') {
                            symbol = '='
                            ++i
                        } else {
                            symbol = '≈'
                        }
                    }
                    if (i + 1 < str.length && str[i + 1] == '=') {
                        symbol = when (symbol) {
                            '!' -> '≠'
                            '<' -> '≤'
                            '>' -> '≥'
                            else -> {
                                ++i
                                symbol
                            }
                        }
                        ++i
                    }

                    normalizedString += symbol
                }
                ++i
            }

            return normalizedString
        }
    }
}