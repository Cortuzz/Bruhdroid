package com.example.bruhdroid.view.instruction

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bruhdroid.R
import com.example.bruhdroid.model.blocks.instruction.Instruction
import com.example.bruhdroid.model.blocks.instruction.condition.EndInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.IfInstruction

open class InstructionView(
    val label: String,
    val blockWidth: Float,
    val blockHeight: Float,
    val drawable: Int,
    val layoutInflater: LayoutInflater,
    val instruction: Instruction,
    val categoryType: Int?,
    val endInstructionView: InstructionView? = null,
    val hasText: Boolean = true,
) {
    lateinit var view: View

    open fun clone(): InstructionView {
        return InstructionView(
            label,
            blockWidth,
            blockHeight,
            drawable,
            layoutInflater,
            instruction.clone(),
            categoryType,
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

    fun tryGenerateEndStatementInstruction(
        instructionHelper: InstructionHelper,
        addStatementCallback: (InstructionView, InstructionView, Boolean) -> Unit
    ) {
        if (instruction !is EndInstruction)
            return

        val addElse = view.findViewById<Button>(R.id.addElseButton)
        val addElif = view.findViewById<Button>(R.id.addElifButton)

        addElse.setOnClickListener {
            addElif.visibility = View.INVISIBLE
            addElse.visibility = View.INVISIBLE

            addStatementCallback(this, instructionHelper.getElseView(), false)
        }
        addElif.setOnClickListener {
            addStatementCallback(this, instructionHelper.getElifView(), true)
        }
    }
}