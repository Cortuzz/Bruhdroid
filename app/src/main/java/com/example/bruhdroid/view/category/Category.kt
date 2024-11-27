package com.example.bruhdroid.view.category

import com.example.bruhdroid.view.instruction.InstructionView

class Category(
    var id: Int,
    var title: String,
    val instructionViews: MutableList<InstructionView> = mutableListOf()
)