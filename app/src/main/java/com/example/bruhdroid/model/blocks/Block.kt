package com.example.bruhdroid.model.blocks

import java.io.Serializable

open class Block(
    val instruction: BlockInstruction,
    var expression: String = "",
    var breakpoint: Boolean = false
) : Serializable