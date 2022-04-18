package com.example.bruhdroid.blocks

import com.example.bruhdroid.Instruction

data class Init(val name: String, val body: Block) : Block(Instruction.INIT, body, null)