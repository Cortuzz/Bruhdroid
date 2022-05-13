package com.example.bruhdroid.model.src

import com.example.bruhdroid.model.src.blocks.Block


class TemplateStorage {
    companion object {
        enum class Template {
            BUBBLE_SORT, INFINITY_LOOP, AHEGAO
        }

        private val templates = mutableMapOf(
            Template.BUBBLE_SORT to arrayOf(
                Block(Instruction.INIT, "n = 5, *arr[n]"),
                Block(Instruction.INIT, "i = 0, j = 0"),

                Block(Instruction.WHILE, "i < n"),
                    Block(Instruction.INPUT, "arr[i]"),
                    Block(Instruction.SET, "i += 1"),
                Block(Instruction.END_WHILE),

                Block(Instruction.SET, "i = 0"),
                Block(Instruction.WHILE, "i < n"),
                    Block(Instruction.SET, "j = i + 1"),
                    Block(Instruction.WHILE, "j < n"),
                        Block(Instruction.IF, "arr[i] > arr[j]"),
                            Block(Instruction.INIT, "t = arr[i]"),
                            Block(Instruction.SET, "arr[i] = arr[j], arr[j] = t"),
                        Block(Instruction.END),
                    Block(Instruction.SET, "j += 1"),
                    Block(Instruction.END_WHILE),
                Block(Instruction.SET, "i += 1"),

                Block(Instruction.END_WHILE),
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
        ))

        fun getBlocks(template: Template): Array<Block>? {
            return templates[template]
        }
    }
}