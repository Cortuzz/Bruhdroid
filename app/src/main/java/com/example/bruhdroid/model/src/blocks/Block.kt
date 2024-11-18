package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction
import java.io.Serializable

open class Block(
    val instruction: Instruction,
    var expression: String = "",
    var breakpoint: Boolean = false
) : Serializable