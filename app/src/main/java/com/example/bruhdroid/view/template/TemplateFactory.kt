package com.example.bruhdroid.view.template

import com.example.bruhdroid.databinding.ActivityTemplatesBinding
import com.example.bruhdroid.model.blocks.instruction.*
import com.example.bruhdroid.model.blocks.instruction.cycle.ForInstruction
import com.example.bruhdroid.model.blocks.instruction.function.FuncInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.EndInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.IfInstruction
import com.example.bruhdroid.model.blocks.instruction.cycle.EndForInstruction
import com.example.bruhdroid.model.blocks.instruction.cycle.EndWhileInstruction
import com.example.bruhdroid.model.blocks.instruction.cycle.WhileInstruction
import com.example.bruhdroid.model.blocks.instruction.CallInstruction
import com.example.bruhdroid.model.blocks.instruction.function.FuncEndInstruction
import com.example.bruhdroid.model.blocks.instruction.ReturnInstruction


class TemplateFactory(binding: ActivityTemplatesBinding) {
    private val templates = arrayOf(
    Template(
        binding.bubbleSort,
        arrayOf(
            InitInstruction("n = 10, *arr[n]"),

            ForInstruction("i = 0, i < n, i += 1"),
            SetInstruction("arr[i] = (100 * rand() - 50).toInt()"),
            EndForInstruction(),

            ForInstruction("i = 0, i < n, i += 1"),
                ForInstruction("j = i + 1, j < n, j += 1"),
                    IfInstruction("arr[i] > arr[j]"),
                        InitInstruction("t = arr[i]"),
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
            InitInstruction("count = 0"),
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
            InitInstruction("value = 0"),
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
            InitInstruction("value = 0"),
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