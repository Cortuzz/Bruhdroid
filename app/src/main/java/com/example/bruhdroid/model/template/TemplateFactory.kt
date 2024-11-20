package com.example.bruhdroid.model.template

import com.example.bruhdroid.databinding.ActivityTemplatesBinding
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Block


class TemplateFactory(binding: ActivityTemplatesBinding) {
    private val templates = arrayOf(
    Template(
        binding.bubbleSort,
        arrayOf(
            Block(BlockInstruction.INIT, "n = 10, *arr[n]"),

            Block(BlockInstruction.FOR, "i = 0, i < n, i += 1"),
                Block(BlockInstruction.SET, "arr[i] = (100 * rand() - 50).toInt()"),
            Block(BlockInstruction.END_FOR),

            Block(BlockInstruction.FOR, "i = 0, i < n, i += 1"),
                Block(BlockInstruction.FOR, "j = i + 1, j < n, j += 1"),
                    Block(BlockInstruction.IF, "arr[i] > arr[j]"),
                        Block(BlockInstruction.INIT, "t = arr[i]"),
                        Block(BlockInstruction.SET, "arr[i] = arr[j], arr[j] = t"),
                    Block(BlockInstruction.END),
                Block(BlockInstruction.END_FOR),

            Block(BlockInstruction.END_FOR),
            Block(BlockInstruction.PRINT, "arr"),
        )
    ),
    Template(
        binding.infinityLoop,
        arrayOf(
            Block(BlockInstruction.INIT, "count = 0"),
            Block(BlockInstruction.WHILE, "1"),
                Block(BlockInstruction.PRINT, "count"),
                Block(BlockInstruction.SET, "count += 1"),
            Block(BlockInstruction.END_WHILE)
        )
    ),
    Template(
        binding.ahegao,
        arrayOf(
            Block(BlockInstruction.PRAGMA, "IO_LINES = 20, INIT_MESSAGE = false, IO_MESSAGE = false"),
            Block(BlockInstruction.PRINT, "\"⠄⠄⠄⢰⣧⣼⣯⠄⣸⣠⣶⣶⣦⣾⠄⠄⠄⠄⡀⠄⢀⣿⣿⠄⠄⠄⢸⡇⠄⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠄⠄⣾⣿⠿⠿⠶⠿⢿⣿⣿⣿⣿⣦⣤⣄⢀⡅⢠⣾⣛⡉⠄⠄⠄⠸⢀⣿⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠄⢀⡋⣡⣴⣶⣶⡀⠄⠄⠙⢿⣿⣿⣿⣿⣿⣴⣿⣿⣿⢃⣤⣄⣀⣥⣿⣿⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠄⢸⣇⠻⣿⣿⣿⣧⣀⢀⣠⡌⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠿⣿⣿⣿⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⢀⢸⣿⣷⣤⣤⣤⣬⣙⣛⢿⣿⣿⣿⣿⣿⣿⡿⣿⣿⡍⠄⠄⢀⣤⣄⠉⠋⣰\""),
            Block(BlockInstruction.PRINT, "\"⠄⣼⣖⣿⣿⣿⣿⣿⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⢇⣿⣿⡷⠶⠶⢿⣿⣿⠇⢀⣤\""),
            Block(BlockInstruction.PRINT, "\"⠘⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣽⣿⣿⣿⡇⣿⣿⣿⣿⣿⣿⣷⣶⣥⣴⣿⡗\""),
            Block(BlockInstruction.PRINT, "\"⢀⠈⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⠄\""),
            Block(BlockInstruction.PRINT, "\"⢸⣿⣦⣌⣛⣻⣿⣿⣧⠙⠛⠛⡭⠅⠒⠦⠭⣭⡻⣿⣿⣿⣿⣿⣿⣿⣿⡿⠃⠄\""),
            Block(BlockInstruction.PRINT, "\"⠘⣿⣿⣿⣿⣿⣿⣿⣿⡆⠄⠄⠄⠄⠄⠄⠄⠄⠹⠈⢋⣽⣿⣿⣿⣿⣵⣾⠃⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠘⣿⣿⣿⣿⣿⣿⣿⣿⠄⣴⣿⣶⣄⠄⣴⣶⠄⢀⣾⣿⣿⣿⣿⣿⣿⠃⠄⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠄⠈⠻⣿⣿⣿⣿⣿⣿⡄⢻⣿⣿⣿⠄⣿⣿⡀⣾⣿⣿⣿⣿⣛⠛⠁⠄⠄⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠄⠄⠄⠈⠛⢿⣿⣿⣿⠁⠞⢿⣿⣿⡄⢿⣿⡇⣸⣿⣿⠿⠛⠁⠄⠄⠄⠄⠄\""),
            Block(BlockInstruction.PRINT, "\"⠄⠄⠄⠄⠄⠄⠄⠉⠻⣿⣿⣾⣦⡙⠻⣷⣾⣿⠃⠿⠋⠁⠄⠄⠄⠄⠄⢀⣠⣴\""),
            Block(BlockInstruction.PRINT, "\"⣿⣿⣿⣶⣶⣮⣥⣒⠲⢮⣝⡿⣿⣿⡆⣿⡿⠃⠄⠄⠄⠄⠄⠄⠄⣠⣴⣿⣿⣿\""),
        )
    ),
    Template(
        binding.factorial,
        arrayOf(
            Block(BlockInstruction.INIT, "value = 0"),
            Block(BlockInstruction.INPUT, "value"),
            Block(BlockInstruction.SET, "value = value.toInt()"),

            Block(BlockInstruction.FUNC, "fact(x)"),
                Block(BlockInstruction.IF, "x <= 1"),
                    Block(BlockInstruction.RETURN, "1"),
                Block(BlockInstruction.END),

                Block(BlockInstruction.FUNC_CALL, "t = fact(x - 1)"),
                Block(BlockInstruction.RETURN, "x * t"),
            Block(BlockInstruction.FUNC_END),

            Block(BlockInstruction.FUNC_CALL, "result = fact(value)"),
            Block(BlockInstruction.PRINT, "result")
        )
    ),
    Template(
        binding.fibonacci,
        arrayOf(
            Block(BlockInstruction.INIT, "value = 0"),
            Block(BlockInstruction.INPUT, "value"),
            Block(BlockInstruction.SET, "value = value.toInt()"),

            Block(BlockInstruction.FUNC, "fibonacci(x)"),
                Block(BlockInstruction.IF, "x <= 2"),
                    Block(BlockInstruction.RETURN, "1"),
                Block(BlockInstruction.END),

                Block(BlockInstruction.FUNC_CALL, "a = fibonacci(x - 1)"),
                Block(BlockInstruction.FUNC_CALL, "b = fibonacci(x - 2)"),

                Block(BlockInstruction.RETURN, "a + b"),
            Block(BlockInstruction.FUNC_END),

            Block(BlockInstruction.FUNC_CALL, "result = fibonacci(value)"),
            Block(BlockInstruction.PRINT, "result")
        )
    )
    )

    fun getTemplates(): Array<Template> {
        return templates
    }
}