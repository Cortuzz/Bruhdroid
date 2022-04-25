package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction
import java.io.Serializable

open class Block(val instruction: Instruction, val expression: String = "", var line: Int = 0) : Serializable