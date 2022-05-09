package com.example.bruhdroid.model.src

import com.example.bruhdroid.model.src.blocks.Block

class TemplateStorage {
    companion object {
        enum class Template {
            BUBBLE_SORT
        }

        private val templates = mutableMapOf(
            Template.BUBBLE_SORT to arrayOf(
                Block(Instruction.INIT, "n = 5, *arr[n]"),
                Block(Instruction.INIT, "i = 0, j = 0"),
                Block(Instruction.SET, "arr[0]=4,arr[1]=3,arr[2]=5,arr[3]=1,arr[4]=2"),

                Block(Instruction.WHILE, "i < n"),
                    Block(Instruction.SET, "j = i + 1"),
                    Block(Instruction.WHILE, "j < n"),
                        Block(Instruction.IF, "arr[i] < arr[j]"),
                            Block(Instruction.INIT, "t = arr[i]"),
                            Block(Instruction.SET, "arr[i] = arr[j], arr[j] = t"),
                        Block(Instruction.END),
                    Block(Instruction.SET, "j = j + 1"),
                    Block(Instruction.END_WHILE),
                Block(Instruction.SET, "i = i + 1"),

                Block(Instruction.END_WHILE),
                Block(Instruction.PRINT, "arr"),
            ))

        fun getBlocks(template: Template): Array<Block>? {
            return templates[template]
        }
    }
}