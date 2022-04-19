package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

data class Assign(val name: String, val body: Block) : Block(Instruction.SET, body, null)