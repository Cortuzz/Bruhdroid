package com.example.bruhdroid.model.operation

import com.example.bruhdroid.model.operation.operand.OperandBuilder
import com.example.bruhdroid.model.operation.operator.builder.*
import com.example.bruhdroid.model.src.IndexOutOfRangeError
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.blocks.valuable.BooleanValuable
import com.example.bruhdroid.model.src.blocks.valuable.ListValuable
import com.example.bruhdroid.model.src.blocks.valuable.StringValuable
import com.example.bruhdroid.model.src.blocks.valuable.Valuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.FloatValuable
import com.example.bruhdroid.model.src.blocks.valuable.numeric.IntegerValuable

class OperationBuilderFactory {
    fun getOperatorBuilders(): List<IOperationBuilder> {
        val indexCheckOperatorBuilder = getIndexCheckOperator()

        return listOf(
            OperatorBuilder(".toInt()", 10, unary = true,
                action = { operand1, _ ->  IntegerValuable(operand1.convertToInt(operand1)) },
            ),
            OperatorBuilder(".toString()", 10, unary = true,
                action = { operand1, _ -> StringValuable(operand1.convertToString(operand1)) },
            ),
            OperatorBuilder(".toFloat()", 10, unary = true,
                action = { operand1, _ -> FloatValuable(operand1.convertToFloat(operand1)) },
            ),
            OperatorBuilder(".toList()", 10, unary = true,
                action = { operand1, _ ->
                    val array = operand1.convertToArray(operand1).toMutableList()
                    val listVal = ListValuable(array.size)
                    listVal.array = array
                    listVal
                 },
            ),
            OperatorBuilder(".toBool()", 10, unary = true,
                action = { operand1, _ -> BooleanValuable(operand1.convertToBool(operand1)) },
            ),
            OperatorBuilder(".sort()", 10, unary = true,
                action = { operand1, _ -> operand1.sort() },
            ),

            OperatorBuilder("∓", 9, "-",
                action = { operand1, _ -> -operand1 },
                unary = true, unaryChange = false
            ),
            OperatorBuilder("±", 9, "+",
                action = { operand1, _ -> +operand1 },
                unary = true, unaryChange = false
            ),
            indexCheckOperatorBuilder,
            OperatorBuilder("*", 7,
                action = { operand1, operand2 -> operand1 * operand2!! }
            ),
            OperatorBuilder("/", 7,
                action = { operand1, operand2 -> operand1 / operand2!! }
            ),
            OperatorBuilder("//", 7,
                action = { operand1, operand2 -> operand1.intDiv(operand2!!) }
            ),
            OperatorBuilder("%", 7,
                action = { operand1, operand2 -> operand1 % operand2!! }
            ),
            OperatorBuilder("-", 6,
                action = { operand1, operand2 -> operand1 - operand2!! }
            ),
            OperatorBuilder("+", 6,
                action = { operand1, operand2 -> operand1 + operand2!! }
            ),
            OperatorBuilder("==", 5,
                action = { operand1, operand2 -> BooleanValuable(operand1 == operand2) }
            ),
            OperatorBuilder("!=", 5,
                action = { operand1, operand2 -> BooleanValuable(operand1 != operand2) }
            ),
            OperatorBuilder("<", 5,
                action = { operand1, operand2 -> BooleanValuable(operand1 < operand2!!) }
            ),
            OperatorBuilder(">", 5,
                action = { operand1, operand2 -> BooleanValuable(operand1 > operand2!!) }
            ),
            OperatorBuilder("<=", 5,
                action = { operand1, operand2 -> BooleanValuable(operand1 <= operand2!!) }
            ),
            OperatorBuilder(">=", 5,
                action = { operand1, operand2 -> BooleanValuable(operand1 >= operand2!!) }
            ),
            OperatorBuilder("!", 4,
                action = { operand1, operand2 -> TODO() }
            ),
            OperatorBuilder("&&", 3,
                action = { operand1, operand2 -> operand1.and(operand2!!) }
            ),
            OperatorBuilder("||", 2,
                action = { operand1, operand2 -> operand1.or(operand2!!) }
            ),

            OperatorBuilder("len", 10, unary = true,
                action = { operand1, _ -> operand1.getLength() },
            ),
            OperatorBuilder("abs", 10, unary = true,
                action = { operand1, _ -> operand1.absolute() },
            ),
            OperatorBuilder("exp", 10, unary = true,
                action = { operand1, _ -> operand1.exponent() },
            ),
            OperatorBuilder("floor", 10, unary = true,
                action = { operand1, _ -> operand1.floor() },
            ),
            OperatorBuilder("ceil", 10, unary = true,
                action = { operand1, _ -> operand1.ceil() },
            ),
            OperatorBuilder("sorted", 10, unary = true,
                action = { operand1, _ -> operand1.sorted() },
            ),
            OpenAggregateOperatorBuilder("(", 0),
            CloseAggregateOperatorBuilder(")", 0, "("),
            OpenAggregateOperatorBuilder("[", 0),
            CloseIndexOperatorBuilder("]", 0, "[",
                indexCheckOperatorBuilder
            ),
            AssignmentOperatorBuilder("#", -1, "*", unary = true,
                assignment = { _, operand2 -> operand2 }
            ),
            AssignmentOperatorBuilder("=", -2,
                assignment = { _, operand2 -> operand2 },
            ),
            AssignmentOperatorBuilder("+=", -2,
                assignment = { operand1, operand2 -> operand1.getData() + operand2 },
            ),
            AssignmentOperatorBuilder("-=", -2,
                assignment = { operand1, operand2 -> operand1.getData() - operand2 },
            ),
            AssignmentOperatorBuilder("*=", -2,
                assignment = { operand1, operand2 -> operand1.getData() * operand2 },
            ),
            AssignmentOperatorBuilder("/=", -2,
                assignment = { operand1, operand2 -> operand1.getData() / operand2 },
            ),
            AssignmentOperatorBuilder("//=", -2,
                assignment = { operand1, operand2 -> operand1.getData().intDiv(operand2) },
            ),
            AssignmentOperatorBuilder("%=", -2,
                assignment = { operand1, operand2 -> operand1.getData() % operand2 },
            ),
            OperandBuilder()
        )
    }

    private fun getIndexCheckOperator(): OperatorBuilder {
        return OperatorBuilder("?", 8,
            action = { operand1, operand2 ->
                try {
                    operand1.array[operand2!!.value.toInt()]
                } catch (e: IndexOutOfBoundsException) {
                    throw IndexOutOfRangeError("${e.message}")
                }
            }
        )
    }
}
