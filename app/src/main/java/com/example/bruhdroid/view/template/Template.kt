package com.example.bruhdroid.view.template

import android.widget.TextView
import com.example.bruhdroid.model.blocks.instruction.Instruction

open class Template(
    val bindingButton: TextView,
    val blocks: Array<Instruction>,
)