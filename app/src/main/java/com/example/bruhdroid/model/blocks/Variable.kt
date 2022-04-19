package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.Instruction

class Variable(val name: String = "") : Block(Instruction.VAR, null, null)