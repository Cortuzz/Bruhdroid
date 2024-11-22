package com.example.bruhdroid.view.instruction

import android.view.LayoutInflater
import com.example.bruhdroid.R
import com.example.bruhdroid.model.blocks.instruction.*

class InstructionHelper(private val layoutInflater: LayoutInflater) {
    private val instructionViews = listOf(
        InstructionView("Pragma", R.drawable.ic_block_pragma,
            layoutInflater, PragmaInstruction()
        ),
        InstructionView("Print", R.drawable.ic_block_print,
            layoutInflater, PrintInstruction()
        ),
        InstructionView("Input", R.drawable.ic_block_input,
            layoutInflater, InputInstruction()
        ),
        InstructionView("Init", R.drawable.ic_block_init,
            layoutInflater, InitInstruction()
        ),
        InstructionView("While", R.drawable.ic_block_while,
            layoutInflater, WhileInstruction(),
            getEndWhileView()
        ),
        InstructionView("If", R.drawable.ic_block_if,
            layoutInflater, IfInstruction(),
            // TODO
        ),
        InstructionView("Set", R.drawable.ic_block_set,
            layoutInflater, SetInstruction()
        ),
        InstructionView("Break", R.drawable.ic_block_break,
            layoutInflater, BreakInstruction(), hasText = false
        ),
        InstructionView("Continue", R.drawable.ic_block_continue,
            layoutInflater, ContinueInstruction(), hasText = false
        ),
        InstructionView("Method", R.drawable.ic_block_func,
            layoutInflater, FuncInstruction(),
            getEndFuncView()
        ),
        InstructionView("Return", R.drawable.ic_block_return,
            layoutInflater, ReturnInstruction()
        ),
        InstructionView("Call", R.drawable.ic_block_call,
            layoutInflater, CallInstruction()
        ),
        InstructionView("For", R.drawable.ic_block_for,
            layoutInflater, ForInstruction(),
            getEndForView()
        ),
        SubsequentInstructionView("", R.layout.condition_block_end,
            layoutInflater, EndInstruction()
        ),
        getEndWhileView(), getElifView(), getElseView(), getEndForView(), getEndFuncView()
    )

    fun getEndFuncView(): InstructionView {
        return SubsequentInstructionView("", R.layout.block_func_end,
            layoutInflater, FuncEndInstruction()
        )
    }

    fun getEndForView(): InstructionView {
        return SubsequentInstructionView("", R.layout.block_end_for,
            layoutInflater, EndForInstruction()
        )
    }

    fun getElseView(): InstructionView {
        return SubsequentInstructionView("", R.layout.block_else,
            layoutInflater, ElseInstruction()
        )
    }

    fun getElifView(): InstructionView {
        return SubsequentInstructionView("", R.layout.block_elif,
            layoutInflater, ElifInstruction()
        )
    }

    fun getEndWhileView(): InstructionView {
        return SubsequentInstructionView("", R.layout.empty_block,
            layoutInflater, EndWhileInstruction()
        )
    }

    fun getInstructionViews(): List<InstructionView> {
        return instructionViews
    }

    fun getViewByInstruction(instruction: Instruction): InstructionView? {
        for (view in instructionViews) {
            if (view.instruction.instruction == instruction.instruction)
                return view.clone()
        }
        return null
    }
}