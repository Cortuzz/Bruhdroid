package com.example.bruhdroid.model

import com.example.bruhdroid.model.operator.Operator
import com.example.bruhdroid.model.operator.OperatorBuilder
import com.example.bruhdroid.model.operator.OperatorBuilderFactory


class Notation {
    companion object {
        private val operatorBuilders = OperatorBuilderFactory().getOperatorBuilders()

        fun convertToRpn(infixNotation: List<String>): List<String> {
            var mayUnary = true
            var arrayInit = false
            val postfixNotation = mutableListOf<String>()
            val opStack = mutableListOf<Operator>()

            for (count in infixNotation.indices) {
                var isOperator = false
                if (infixNotation[count] == "") {
                    continue
                }
                val inputOperator = infixNotation[count]
                for (operatorBuilder in operatorBuilders) {
                    if (isOperator)
                        break
                    if (!operatorBuilder.match(inputOperator, mayUnary))
                        continue
                    isOperator = true

                    val operator = operatorBuilder.buildOperator(inputOperator, mayUnary)
                    when (operator.inputOperator) {
                        "(" -> {
                            opStack.add(operator)
                            mayUnary = true
                        }
                        "[" -> {
                            opStack.add(operator)
                            mayUnary = true
                        }
                        ")" -> {
                            var s = opStack.removeLast()
                            while (s.operator != "(") {
                                postfixNotation.add(s.inputOperator)
                                s = opStack.removeLast()
                            }
                            mayUnary = false
                        }
                        "]" -> {
                            var s = opStack.removeLast()
                            while (s.operator != "[") {
                                postfixNotation.add(s.inputOperator)
                                s = opStack.removeLast()
                            }
                            if (!arrayInit) {
                                postfixNotation.add("?")
                            }
                            arrayInit = false
                            mayUnary = false
                        }
                        else -> {
                            if (mayUnary && !operator.unary || !mayUnary && operator.unary) {
                                isOperator = false
                                continue
                            }

                            if (mayUnary && operator.unary) {
                                if (operator.inputOperator == "*")
                                    arrayInit = true

                                opStack.add(operator)
                                mayUnary = false
                                break
                            }

                            while (opStack.size > 0 && opStack.last().unary)
                                postfixNotation.add(opStack.removeLast().operator)

                            if (opStack.size > 0 && operator.priority <= opStack.last().priority)
                                postfixNotation.add(opStack.removeLast().inputOperator)

                            opStack.add(operator)
                            mayUnary = true
                        }
                    }
                }

                if (!isOperator) {
                    postfixNotation.add(inputOperator)
                    mayUnary = false
                }
            }

            for (i in opStack.reversed()) {
                if (i.unary) {
                    postfixNotation.add(i.operator)
                    continue
                }
                postfixNotation.add(i.inputOperator)
            }

            return postfixNotation
        }

        fun tokenizeString(str: String): List<String> {
            val name = "([\\d]+\\.?[\\d]+|\\w[\\w\\d_]*|\".*\")"
            val reserved = "(rand\\(\\)|abs|exp|floor|ceil|sorted|len)"
            val convert =
                "(\\.toInt\\(\\)|\\.toFloat\\(\\)|\\.toString\\(\\)|\\.toBool\\(\\)|\\.sort\\(\\)|\\.toList\\(\\))"
            val operator = "(\\+=|-=|\\*=|/=|%=|&&|\\|\\||\\+|-|//|\\*|%|/|==|=|!=|>=|<=|<|>|)"
            val bracket = "(\\(|\\)|\\[|\\])"
            val exp = Regex("($convert|$reserved|$bracket|$name|$operator)")

            return (exp.findAll(str).toList().map { it.destructured.toList()[0] })
        }
    }
}
