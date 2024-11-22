package com.example.bruhdroid.view.instruction

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bruhdroid.R
import com.example.bruhdroid.model.blocks.instruction.Instruction

open class InstructionView(
    val label: String,
    val drawable: Int,
    val layoutInflater: LayoutInflater,
    val instruction: Instruction,
    val endInstructionView: InstructionView? = null,
    val hasText: Boolean = true,
) {
    val nestedViews = mutableListOf<InstructionView>()
    lateinit var view: View

    open fun clone(): InstructionView {
        return InstructionView(
            label,
            drawable,
            layoutInflater,
            instruction.clone(),
            endInstructionView?.clone(),
            hasText
        )
    }

    @SuppressLint("InflateParams")
    open fun updateBlockView(ctx: Context): View {
        view = when (hasText) {
            true -> layoutInflater.inflate(R.layout.block, null)
            false -> layoutInflater.inflate(R.layout.block_non_text, null)
        }

        view.findViewById<TextView>(R.id.textView).text = label
        view.background = ContextCompat.getDrawable(ctx, drawable)

        return view
    }

    fun generateBreakpoint() {
        val button = view.findViewById<ImageButton>(R.id.breakpoint)
        button?.setOnClickListener {
            instruction.breakpoint = !instruction.breakpoint
            drawBreakpoint()
        }
    }

    fun drawBreakpoint() {
        val button = view.findViewById<ImageButton>(R.id.breakpoint)

        button.setBackgroundResource(
            when (instruction.breakpoint) {
                true -> android.R.drawable.presence_online
                false -> android.R.drawable.presence_invisible
            }
        )
    }
}