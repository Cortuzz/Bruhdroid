package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

open class Block(val instruction: Instruction, val leftBody: Block?, val rightBody: Block?)

data class Container(val instructions: MutableList<Block>) : Block(Instruction.SCOPE, null, null)

data class Init(val body: Block) : Block(Instruction.INIT, body, null)

data class Assign(val name: String, val body: Block) : Block(Instruction.SET, body, null)

data class RawInput(val input: String) : Block(Instruction.RAW, null, null)