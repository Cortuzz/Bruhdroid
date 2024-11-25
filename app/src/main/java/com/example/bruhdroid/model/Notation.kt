package com.example.bruhdroid.model

import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.operation.OperationBuilderFactory
import com.example.bruhdroid.model.operation.OperationParseDto


class Notation {
    companion object {
        private val operatorBuilders = OperationBuilderFactory().getOperatorBuilders()

        fun convertInfixToPostfixNotation(infixNotation: List<String>): List<Operation> {
            var parseDto = OperationParseDto(
                inputData = "",
                operations = mutableListOf(),
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
                    parseDto.operations.add(i)
                    continue
                }

                parseDto.operations.add(i)
            }

            return parseDto.operations
        }

        fun tokenizeString(str: String): List<String> {
            // Обрабатывает все записи вида:
            // 23.4 (float), 63 (int), vsf43dp (variable), "something" (string)
            val name = "([\\d]+\\.?[\\d]+|\\w[\\w\\d_]*|\".*\")"
            // Обрабатывает все записи зарезервированных функций: rand(), abs() и так далее
            val reserved = "(rand\\(\\)|abs|exp|floor|ceil|sorted|len)"
            // Обрабатывает все записи встроенных методов конвертации variable.toInt() и т. д.
            val convert =
                "(\\.toInt\\(\\)|\\.toFloat\\(\\)|\\.toString\\(\\)|\\.toBool\\(\\)|\\.sort\\(\\)|\\.toList\\(\\))"
            // Обрабатывает все возможные операторы, которые может использовать пользователь
            val operator = "(\\+=|-=|\\*=|/=|%=|&&|\\|\\||\\+|-|//|\\*|%|/|==|=|!=|>=|<=|<|>|)"
            // Обрабатывает скобочные символы "(, )" и символы обращения к массиву "[, ]"
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
