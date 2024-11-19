package com.example.bruhdroid.model.operation

import com.example.bruhdroid.model.operation.operand.OperandBuilder
import com.example.bruhdroid.model.operation.operator.builder.CloseAggregateOperatorBuilder
import com.example.bruhdroid.model.operation.operator.builder.CloseIndexOperatorBuilder
import com.example.bruhdroid.model.operation.operator.builder.OpenAggregateOperatorBuilder
import com.example.bruhdroid.model.operation.operator.builder.OperatorBuilder

class OperationBuilderFactory {
    fun getOperatorBuilders(): List<IOperationBuilder> {
        return listOf(
            OperatorBuilder("r", 10, listOf(
                ".toInt()",
                ".toString()",
                ".toFloat()",
                ".toList()",
                ".toBool()",
                ), unary = true,
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("∓", 9, listOf("-"),
                action = { operand1, _ -> -operand1 },
                unary = true, unaryChange = false
            ),
            OperatorBuilder("±", 9, listOf("+"),
                action = { operand1, _ -> +operand1 },
                unary = true, unaryChange = false
            ),
            OperatorBuilder("?", 8, listOf("?"),
                action = { operand1, _ -> TODO() }
            ),
            OperatorBuilder("*", 7, listOf("*"),
                action = { operand1, _ -> -operand1 }
            ),
            OperatorBuilder("/", 7, listOf("/", "//", "%"),
                action = { operand1, operand2 -> operand1 / operand2!! } // TODO
            ),
            OperatorBuilder("-", 6, listOf("-"),
                action = { operand1, operand2 -> operand1 - operand2!! }
            ),
            OperatorBuilder("+", 6, listOf("+"),
                action = { operand1, operand2 -> operand1 / operand2!! }
            ),
            OperatorBuilder("=", 5, listOf("=="),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("≠", 5, listOf("!="),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("<", 5, listOf("<"),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder(">", 5, listOf(">"),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("≤", 5, listOf("<="),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("≥", 5, listOf(">="),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("!", 4, listOf("!"),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("&", 3, listOf("&&"),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("|", 2, listOf("||"),
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("m", 10, listOf("len", "abs", "exp", "floor", "ceil", "sorted"),
                action = { operand1, _ -> TODO() },
            ),
            OpenAggregateOperatorBuilder("(", 0, listOf("(")),
            CloseAggregateOperatorBuilder(")", 0, listOf(")"), "("),
            OpenAggregateOperatorBuilder("[", 0, listOf("[")),
            CloseIndexOperatorBuilder("]", 0, listOf("]"), "["),
            OperatorBuilder("#", -1, listOf("*"), unary = true,
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("≈", -2, listOf("=", "+=", "-=", "*=", "/=", "//=", "%="),
                action = { operand1, _ -> TODO() },
            ),
            OperandBuilder()
        )
    }
}
