package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

data class Container(val instructions: MutableList<Block>) :
    Block(Instruction.SCOPE, null, null)
