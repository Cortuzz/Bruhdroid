package com.example.bruhdroid.model

import com.example.bruhdroid.model.operation.OperationBuilderFactory
import com.example.bruhdroid.model.operation.OperationParseDto


class Notation {
    companion object {
        private val operatorBuilders = OperationBuilderFactory().getOperatorBuilders()

        fun convertInfixToPostfixNotation(infixNotation: List<String>): List<String> {
            var parseDto = OperationParseDto(
                inputData = "",
                postfixNotation = mutableListOf(),
                operationStack = mutableListOf(),
                mayUnary = true,
                arrayInitialization = false
            )

            for (inputOperator in infixNotation) {
                parseDto.inputData = inputOperator
                if (inputOperator == "")
                    continue

                parseDto = parseOperator(parseDto)
            }

            for (i in parseDto.operationStack.reversed()) {
                if (i.unary) {
                    parseDto.postfixNotation.add(i.operator)
                    continue
                }
                parseDto.postfixNotation.add(i.inputOperator)
            }

            return parseDto.postfixNotation
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

        private fun parseOperator(dto: OperationParseDto): OperationParseDto {
            val parseDto = dto.prototype()

            for (operatorBuilder in operatorBuilders) {
                if (operatorBuilder.tryBuild(dto.inputData, parseDto.mayUnary) == null)
                    continue

                return operatorBuilder.parse(parseDto)
            }

            return parseDto
        }
    }
}
