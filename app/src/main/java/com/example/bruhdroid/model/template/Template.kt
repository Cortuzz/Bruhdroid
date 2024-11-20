package com.example.bruhdroid.model.template

import android.widget.TextView
import com.example.bruhdroid.model.blocks.Block

open class Template(
    val bindingButton: TextView,
    val blocks: Array<Block>,
)