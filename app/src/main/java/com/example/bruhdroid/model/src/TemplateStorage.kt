package com.example.bruhdroid.model.src

import com.example.bruhdroid.model.src.blocks.Block


class TemplateStorage {
    companion object {
        enum class Template {
            BUBBLE_SORT, INFINITY_LOOP, AHEGAO, FACTORIAL, FIBONACCI
        }

        private val templates = mutableMapOf(
            Template.BUBBLE_SORT to arrayOf(
                Block(Instruction.INIT, "n = 10, *arr[n]"),

                Block(Instruction.FOR, "i = 0, i < n, i += 1"),
                    Block(Instruction.SET, "arr[i] = (100 * rand() - 50).toInt()"),
                Block(Instruction.END_FOR),

                Block(Instruction.FOR, "i = 0, i < n, i += 1"),
                    Block(Instruction.FOR, "j = i + 1, j < n, j += 1"),
                        Block(Instruction.IF, "arr[i] > arr[j]"),
                            Block(Instruction.INIT, "t = arr[i]"),
                            Block(Instruction.SET, "arr[i] = arr[j], arr[j] = t"),
                        Block(Instruction.END),
                    Block(Instruction.END_FOR),

                Block(Instruction.END_FOR),
                Block(Instruction.PRINT, "arr"),
            ),
        Template.INFINITY_LOOP to arrayOf(
            Block(Instruction.INIT, "count = 0"),
            Block(Instruction.WHILE, "1"),
                Block(Instruction.PRINT, "count"),
                Block(Instruction.SET, "count += 1"),
            Block(Instruction.END_WHILE)
        ),
        Template.AHEGAO to arrayOf(
            Block(Instruction.PRAGMA, "IO_LINES = 20, INIT_MESSAGE = false, IO_MESSAGE = false"),
            Block(Instruction.PRINT, "\"⠄⠄⠄⢰⣧⣼⣯⠄⣸⣠⣶⣶⣦⣾⠄⠄⠄⠄⡀⠄⢀⣿⣿⠄⠄⠄⢸⡇⠄⠄\""),
            Block(Instruction.PRINT, "\"⠄⠄⠄⣾⣿⠿⠿⠶⠿⢿⣿⣿⣿⣿⣦⣤⣄⢀⡅⢠⣾⣛⡉⠄⠄⠄⠸⢀⣿⠄\""),
            Block(Instruction.PRINT, "\"⠄⠄⢀⡋⣡⣴⣶⣶⡀⠄⠄⠙⢿⣿⣿⣿⣿⣿⣴⣿⣿⣿⢃⣤⣄⣀⣥⣿⣿⠄\""),
            Block(Instruction.PRINT, "\"⠄⠄⢸⣇⠻⣿⣿⣿⣧⣀⢀⣠⡌⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠿⣿⣿⣿⠄\""),
            Block(Instruction.PRINT, "\"⠄⢀⢸⣿⣷⣤⣤⣤⣬⣙⣛⢿⣿⣿⣿⣿⣿⣿⡿⣿⣿⡍⠄⠄⢀⣤⣄⠉⠋⣰\""),
            Block(Instruction.PRINT, "\"⠄⣼⣖⣿⣿⣿⣿⣿⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⢇⣿⣿⡷⠶⠶⢿⣿⣿⠇⢀⣤\""),
            Block(Instruction.PRINT, "\"⠘⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣽⣿⣿⣿⡇⣿⣿⣿⣿⣿⣿⣷⣶⣥⣴⣿⡗\""),
            Block(Instruction.PRINT, "\"⢀⠈⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⠄\""),
            Block(Instruction.PRINT, "\"⢸⣿⣦⣌⣛⣻⣿⣿⣧⠙⠛⠛⡭⠅⠒⠦⠭⣭⡻⣿⣿⣿⣿⣿⣿⣿⣿⡿⠃⠄\""),
            Block(Instruction.PRINT, "\"⠘⣿⣿⣿⣿⣿⣿⣿⣿⡆⠄⠄⠄⠄⠄⠄⠄⠄⠹⠈⢋⣽⣿⣿⣿⣿⣵⣾⠃⠄\""),
            Block(Instruction.PRINT, "\"⠄⠘⣿⣿⣿⣿⣿⣿⣿⣿⠄⣴⣿⣶⣄⠄⣴⣶⠄⢀⣾⣿⣿⣿⣿⣿⣿⠃⠄⠄\""),
            Block(Instruction.PRINT, "\"⠄⠄⠈⠻⣿⣿⣿⣿⣿⣿⡄⢻⣿⣿⣿⠄⣿⣿⡀⣾⣿⣿⣿⣿⣛⠛⠁⠄⠄⠄\""),
            Block(Instruction.PRINT, "\"⠄⠄⠄⠄⠈⠛⢿⣿⣿⣿⠁⠞⢿⣿⣿⡄⢿⣿⡇⣸⣿⣿⠿⠛⠁⠄⠄⠄⠄⠄\""),
            Block(Instruction.PRINT, "\"⠄⠄⠄⠄⠄⠄⠄⠉⠻⣿⣿⣾⣦⡙⠻⣷⣾⣿⠃⠿⠋⠁⠄⠄⠄⠄⠄⢀⣠⣴\""),
            Block(Instruction.PRINT, "\"⣿⣿⣿⣶⣶⣮⣥⣒⠲⢮⣝⡿⣿⣿⡆⣿⡿⠃⠄⠄⠄⠄⠄⠄⠄⣠⣴⣿⣿⣿\""),
        ),
        Template.FACTORIAL to arrayOf(
            Block(Instruction.INIT, "value = 0"),
            Block(Instruction.INPUT, "value"),
            Block(Instruction.SET, "value = value.toInt()"),

            Block(Instruction.FUNC, "fact(x)"),
                Block(Instruction.IF, "x <= 1"),
                    Block(Instruction.RETURN, "1"),
                Block(Instruction.END),

                Block(Instruction.FUNC_CALL, "t = fact(x - 1)"),
                Block(Instruction.RETURN, "x * t"),
            Block(Instruction.FUNC_END),

            Block(Instruction.FUNC_CALL, "result = fact(value)"),
            Block(Instruction.PRINT, "result")
        ),
        Template.FIBONACCI to arrayOf(
            Block(Instruction.INIT, "value = 0"),
            Block(Instruction.INPUT, "value"),
            Block(Instruction.SET, "value = value.toInt()"),

            Block(Instruction.FUNC, "fibonacci(x)"),
                Block(Instruction.IF, "x <= 2"),
                    Block(Instruction.RETURN, "1"),
                Block(Instruction.END),

                Block(Instruction.FUNC_CALL, "a = fibonacci(x - 1)"),
                Block(Instruction.FUNC_CALL, "b = fibonacci(x - 2)"),

                Block(Instruction.RETURN, "a + b"),
            Block(Instruction.FUNC_END),

            Block(Instruction.FUNC_CALL, "result = fibonacci(value)"),
            Block(Instruction.PRINT, "result")
        ))

        fun getBlocks(template: Template): Array<Block>? {
            return templates[template]
        }
    }
}