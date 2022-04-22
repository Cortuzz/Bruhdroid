package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction

class Variable(val name: String = "") : Block(Instruction.VAR, null, null)