package com.example.bruhdroid.model


class Notation {
    companion object {
        private enum class Operator(val operator: Char, val priority: Int) {
            CONVERT('r', 10),
            UNARY_MINUS('∓', 9), UNARY_PLUS('±', 9),
            DEFINE_BY_INDEX('?', 8),
            MULTIPLY('*', 7), DIVIDE('/', 7),
            SUBTRACT('-', 6), ADD('+', 6),
            EQUALS('=', 5), NOT_EQUALS('≠', 5),
            LESS('<', 5), GREATER('>', 5),
            LESS_OR_EQUALS('≤', 5), GREATER_OR_EQUALS('≥', 5),
            NOT('!', 4), AND('&', 3), OR('|', 2),
            MATH('m', 10),
            OPEN_BRACKET('(', 0), CLOSE_BRACKET(')', 0),
            OPEN_INDEX('[', 0), CLOSE_INDEX(']', 0),
            INIT_ARRAY('#', -1), DEFINE('≈', -2)
        }

        private val operators = mapOf(
            "-" to Operator.SUBTRACT,
            "+" to Operator.ADD,
            "*" to Operator.MULTIPLY,
            "/" to Operator.DIVIDE,
            "//" to Operator.DIVIDE,
            "%" to Operator.DIVIDE,
            "(" to Operator.OPEN_BRACKET,
            ")" to Operator.CLOSE_BRACKET,
            "=" to Operator.DEFINE,
            "∓" to Operator.UNARY_MINUS,
            "±" to Operator.UNARY_PLUS,
            "!" to Operator.NOT,
            "&&" to Operator.AND,
            "||" to Operator.OR,
            "<" to Operator.LESS,
            ">" to Operator.GREATER,
            "==" to Operator.EQUALS,
            "!=" to Operator.NOT_EQUALS,
            "<=" to Operator.LESS_OR_EQUALS,
            ">=" to Operator.GREATER_OR_EQUALS,
            "[" to Operator.OPEN_INDEX,
            "]" to Operator.CLOSE_INDEX,
            "?" to Operator.DEFINE_BY_INDEX,
            "#" to Operator.INIT_ARRAY,
            "+=" to Operator.DEFINE,
            "-=" to Operator.DEFINE,
            "*=" to Operator.DEFINE,
            "/=" to Operator.DEFINE,
            "//=" to Operator.DEFINE,
            "%=" to Operator.DEFINE,
            ".toInt()" to Operator.CONVERT,
            ".toFloat()" to Operator.CONVERT,
            ".toString()" to Operator.CONVERT,
            ".toBool()" to Operator.CONVERT,
            ".toList()" to Operator.CONVERT,
            "abs" to Operator.MATH,
            "exp" to Operator.MATH,
            "floor" to Operator.MATH,
            "ceil" to Operator.MATH,
            "sorted" to Operator.MATH,
        )

        fun convertToRpn(infixNotation: List<String>): List<String> {
            val unary = listOf("+", "-", "*")
            val convertedUnary = listOf("±", "∓", "#", ".toInt()", ".toFloat()", ".toBool()", ".toString()", ".sort()", ".toList()")
            var mayUnary = true
            var arrayInit = false
            val postfixNotation = mutableListOf<String>()
            val opStack = mutableListOf<String>()

            for (count in infixNotation.indices) {
                if (infixNotation[count] == "") {
                    continue
                }
                if (infixNotation[count] !in operators) {
                    postfixNotation.add(infixNotation[count])
                    mayUnary = false
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
                                postfixNotation.add(s)
                                s = opStack.removeLast()
                            }
                            mayUnary = false
                        }
                        Operator.CLOSE_INDEX -> {
                            var s = opStack.removeLast()
                            while (operators[s] != Operator.OPEN_INDEX) {
                                postfixNotation.add(s)
                                s = opStack.removeLast()
                            }
                            if (!arrayInit) {
                                postfixNotation.add("?")
                            }
                            arrayInit = false
                            mayUnary = false
                        }
                        else -> {
                            if (mayUnary && infixNotation[count] in unary) {
                                when (infixNotation[count]) {
                                    "-" -> opStack.add("∓")
                                    "+" -> opStack.add("±")
                                    "*" -> {
                                        opStack.add("#")
                                        arrayInit = true
                                    }
                                }
                                mayUnary = false
                                continue
                            }

                            while (opStack.size > 0 && opStack.last() in convertedUnary) {
                                postfixNotation.add(opStack.removeLast())
                            }
                            if (opStack.size > 0 && operators[infixNotation[count]]!!.priority <=
                                operators[opStack.last()]!!.priority
                            ) {
                                postfixNotation.add(opStack.removeLast())
                            }

                            opStack.add(infixNotation[count])
                            mayUnary = true
                        }
                    }
                }
            }

            for (i in opStack.reversed()) {
                postfixNotation.add(i)
            }

            return postfixNotation
        }

        fun tokenizeString(str: String): List<String> {
            val name = "([\\d]+\\.?[\\d]+|\\w[\\w\\d_]*|\".*\")"
            val reserved = "(rand\\(\\)|abs|exp|floor|ceil|sorted)"
            val convert = "(\\.toInt\\(\\)|\\.toFloat\\(\\)|\\.toString\\(\\)|\\.toBool\\(\\)|\\.sort\\(\\)|\\.toList\\(\\))"
            val operator = "(\\+=|-=|\\*=|/=|%=|&&|\\|\\||\\+|-|//|\\*|%|/|==|=|!=|>=|<=|<|>|)"
            val bracket = "(\\(|\\)|\\[|\\])"
            val exp = Regex("($convert|$reserved|$bracket|$name|$operator)")
            val strSeq = (exp.findAll(str).toList().map { it.destructured.toList()[0] })

            return strSeq
        }
    }
}
