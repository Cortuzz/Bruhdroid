package com.example.bruhdroid.model.operator

class OperatorBuilderFactory {
    fun getOperatorBuilders(): List<OperatorBuilder> {
        return listOf(
            OperatorBuilder("r", 10, listOf(
                ".toInt()",
                ".toString()",
                ".toFloat()",
                ".toList()",
                ".toBool()",
                ), unary = true
            ),
            OperatorBuilder("∓", 9, listOf("-"), unary = true, unaryChange = false),
            OperatorBuilder("±", 9, listOf("+"), unary = true, unaryChange = false),
            OperatorBuilder("?", 8, listOf("?")),
            OperatorBuilder("*", 7, listOf("*")),
            OperatorBuilder("/", 7, listOf("/", "//", "%")),
            OperatorBuilder("-", 6, listOf("-")),
            OperatorBuilder("+", 6, listOf("+")),
            OperatorBuilder("=", 5, listOf("==")),
            OperatorBuilder("≠", 5, listOf("!=")),
            OperatorBuilder("<", 5, listOf("<")),
            OperatorBuilder(">", 5, listOf(">")),
            OperatorBuilder("≤", 5, listOf("<=")),
            OperatorBuilder("≥", 5, listOf(">=")),
            OperatorBuilder("!", 4, listOf("!")),
            OperatorBuilder("&", 3, listOf("&&")),
            OperatorBuilder("|", 2, listOf("||")),
            OperatorBuilder("m", 10, listOf("len", "abs", "exp", "floor", "ceil", "sorted")),
            OperatorBuilder("(", 0, listOf("("), unaryChange = false),
            OperatorBuilder(")", 0, listOf(")"), unaryChange = true),
            OperatorBuilder("[", 0, listOf("["), unaryChange = false),
            OperatorBuilder("]", 0, listOf("]"), unaryChange = true),
            OperatorBuilder("#", -1, listOf("*"), unary = true),
            OperatorBuilder("≈", -2, listOf("=", "+=", "-=", "*=", "/=", "//=", "%="))
        )
    }
}
