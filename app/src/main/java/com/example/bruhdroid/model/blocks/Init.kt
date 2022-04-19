package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

data class Init(val name: String, val body: Block) : Block(Instruction.INIT, body, null)