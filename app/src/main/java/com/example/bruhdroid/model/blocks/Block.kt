package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

open class Block(val instruction: Instruction, val leftBody: Block?, val rightBody: Block?) {
}
