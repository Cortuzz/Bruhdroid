package com.example.bruhdroid.view.instruction

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.bruhdroid.R
import com.example.bruhdroid.model.blocks.instruction.Instruction

class SubsequentInstructionView(
    label: String,
    blockWidth: Float,
    blockHeight: Float,
    drawable: Int,
    layoutInflater: LayoutInflater,
    instruction: Instruction,
    hasText: Boolean = true,
    endInstructionView: InstructionView? = null,
): InstructionView(
    label,
    blockWidth,
    blockHeight,
    drawable,
    layoutInflater,
    instruction,
    null,
    endInstructionView,
    hasText
) {
    override fun clone(): InstructionView {
        return SubsequentInstructionView(
            label,
            blockWidth,
            blockHeight,
            drawable,
            layoutInflater,
            instruction.clone(),
            hasText,
            endInstructionView?.clone()
        )
    }

    @SuppressLint("InflateParams")
    override fun updateBlockView(ctx: Context): View {
        view = layoutInflater.inflate(drawable, null)
        return view
    }
}