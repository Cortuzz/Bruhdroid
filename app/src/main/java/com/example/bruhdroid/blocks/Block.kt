package com.example.bruhdroid.blocks

import com.example.bruhdroid.Instruction

open class Block(val instruction: Instruction, val leftBody: Block?, val rightBody: Block?) {
}
