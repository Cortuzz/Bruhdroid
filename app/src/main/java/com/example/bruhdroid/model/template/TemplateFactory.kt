package com.example.bruhdroid.model.template

import com.example.bruhdroid.databinding.ActivityTemplatesBinding
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.instruction.*


class TemplateFactory(binding: ActivityTemplatesBinding) {
    private val templates = arrayOf(
    Template(
        binding.bubbleSort,
        arrayOf(
            SetInstruction("n = 10, *arr[n]", true),

            ForInstruction("i = 0, i < n, i += 1"),
            SetInstruction("arr[i] = (100 * rand() - 50).toInt()"),
            EndForInstruction(),

            ForInstruction("i = 0, i < n, i += 1"),
                ForInstruction("j = i + 1, j < n, j += 1"),
                    IfInstruction("arr[i] > arr[j]"),
                        SetInstruction("t = arr[i]", true),
                        SetInstruction("arr[i] = arr[j], arr[j] = t"),
                    EndInstruction(),
                EndForInstruction(),

            EndForInstruction(),
            PrintInstruction("arr"),
        )
    ),
    Template(
        binding.infinityLoop,
        arrayOf(
            SetInstruction("count = 0", true),
            WhileInstruction("1"),
                PrintInstruction("count"),
            SetInstruction("count += 1"),
            EndWhileInstruction()
        )
    ),
    Template(
        binding.ahegao,
        arrayOf(
            PragmaInstruction("IO_LINES = 20, INIT_MESSAGE = false, IO_MESSAGE = false"),
            PrintInstruction("\"⠄⠄⢰⣧⣼⣯⠄⣸⣠⣶⣶⣦⣾⠄⠄⠄⠄⡀⠄⢀⣿⣿⠄⠄⠄⢸⡇⠄⠄\""),
            PrintInstruction("\"⠄⠄⣾⣿⠿⠿⠶⠿⢿⣿⣿⣿⣿⣦⣤⣄⢀⡅⢠⣾⣛⡉⠄⠄⠄⠸⢀⣿⠄\""),
            PrintInstruction("\"⠄⢀⡋⣡⣴⣶⣶⡀⠄⠄⠙⢿⣿⣿⣿⣿⣿⣴⣿⣿⣿⢃⣤⣄⣀⣥⣿⣿⠄\""),
            PrintInstruction("\"⠄⢸⣇⠻⣿⣿⣿⣧⣀⢀⣠⡌⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠿⣿⣿⣿⠄\""),
            PrintInstruction("\"⢀⢸⣿⣷⣤⣤⣤⣬⣙⣛⢿⣿⣿⣿⣿⣿⣿⡿⣿⣿⡍⠄⠄⢀⣤⣄⠉⠋⣰\""),
            PrintInstruction("\"⣼⣖⣿⣿⣿⣿⣿⣿⣿⣿⣿⢿⣿⣿⣿⣿⣿⢇⣿⣿⡷⠶⠶⢿⣿⣿⠇⢀⣤\""),
            PrintInstruction( "\"⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣽⣿⣿⣿⡇⣿⣿⣿⣿⣿⣿⣷⣶⣥⣴⣿⡗\""),
            PrintInstruction("\"⠈⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⠄\""),
            PrintInstruction("\"⣿⣦⣌⣛⣻⣿⣿⣧⠙⠛⠛⡭⠅⠒⠦⠭⣭⡻⣿⣿⣿⣿⣿⣿⣿⣿⡿⠃⠄\""),
            PrintInstruction("\"⣿⣿⣿⣿⣿⣿⣿⣿⡆⠄⠄⠄⠄⠄⠄⠄⠄⠹⠈⢋⣽⣿⣿⣿⣿⣵⣾⠃⠄\""),
            PrintInstruction("\"⠘⣿⣿⣿⣿⣿⣿⣿⣿⠄⣴⣿⣶⣄⠄⣴⣶⠄⢀⣾⣿⣿⣿⣿⣿⣿⠃⠄⠄\""),
            PrintInstruction("\"⠄⠈⠻⣿⣿⣿⣿⣿⣿⡄⢻⣿⣿⣿⠄⣿⣿⡀⣾⣿⣿⣿⣿⣛⠛⠁⠄⠄⠄\""),
            PrintInstruction("\"⠄⠄⠄⠈⠛⢿⣿⣿⣿⠁⠞⢿⣿⣿⡄⢿⣿⡇⣸⣿⣿⠿⠛⠁⠄⠄⠄⠄⠄\""),
            PrintInstruction("\"⠄⠄⠄⠄⠄⠄⠉⠻⣿⣿⣾⣦⡙⠻⣷⣾⣿⠃⠿⠋⠁⠄⠄⠄⠄⠄⢀⣠⣴\""),
            PrintInstruction("\"⣿⣿⣶⣶⣮⣥⣒⠲⢮⣝⡿⣿⣿⡆⣿⡿⠃⠄⠄⠄⠄⠄⠄⠄⣠⣴⣿⣿⣿\""),
        )
    ),
    Template(
        binding.factorial,
        arrayOf(
            SetInstruction("value = 0", true),
            InputInstruction("value"),
            SetInstruction("value = value.toInt()"),

            FuncInstruction("fact(x)"),
                IfInstruction("x <= 1"),
                    ReturnInstruction("1"),
                EndInstruction(),

                CallInstruction("t = fact(x - 1)"),
                ReturnInstruction("x * t"),
            FuncEndInstruction(),

            CallInstruction("result = fact(value)"),
            PrintInstruction("result")
        )
    ),
    Template(
        binding.fibonacci,
        arrayOf(
            SetInstruction("value = 0", true),
            InputInstruction("value"),
            SetInstruction("value = value.toInt()"),

            FuncInstruction("fibonacci(x)"),
                IfInstruction("x <= 2"),
                    ReturnInstruction("1"),
                EndInstruction(),

                CallInstruction("a = fibonacci(x - 1)"),
            CallInstruction( "b = fibonacci(x - 2)"),

                ReturnInstruction("a + b"),
            FuncEndInstruction(),

            CallInstruction("result = fibonacci(value)"),
            PrintInstruction("result")
        )
    )
    )

    fun getTemplates(): Array<Template> {
        return templates
    }
}