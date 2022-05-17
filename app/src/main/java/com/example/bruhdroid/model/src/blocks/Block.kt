package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction
import java.io.Serializable

open class Block(val instruction: Instruction, var expression: String = "", var line: Int = 0,
                 var breakpoint: Boolean = false, var parsed: List<String> = listOf()) : Serializable