package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction

open class Block(val instruction: Instruction, val leftBody: Block?, val rightBody: Block?, var line: Int = 0)

data class Container(val instructions: MutableList<Block>) : Block(Instruction.SCOPE, null, null)

data class Init(val body: Block) : Block(Instruction.INIT, body, null)

data class Assign(val name: String, val body: Block) : Block(Instruction.SET, body, null)

data class RawInput(val input: String) : Block(Instruction.RAW, null, null)

data class Print(val body: Block) : Block(Instruction.PRINT, body, null)
