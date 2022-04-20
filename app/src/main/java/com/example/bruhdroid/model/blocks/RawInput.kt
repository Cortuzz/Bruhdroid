package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

data class RawInput(val input: String) : Block(Instruction.RAW, null, null)