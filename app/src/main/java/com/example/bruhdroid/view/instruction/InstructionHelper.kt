package com.example.bruhdroid.view.instruction

import android.view.LayoutInflater
import com.example.bruhdroid.R
import com.example.bruhdroid.model.blocks.instruction.*
import com.example.bruhdroid.model.blocks.instruction.condition.ElifInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.ElseInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.EndInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.IfInstruction
import com.example.bruhdroid.model.blocks.instruction.cycle.*
import com.example.bruhdroid.model.blocks.instruction.CallInstruction
import com.example.bruhdroid.model.blocks.instruction.function.FuncEndInstruction
import com.example.bruhdroid.model.blocks.instruction.function.FuncInstruction
import com.example.bruhdroid.model.blocks.instruction.ReturnInstruction
import com.example.bruhdroid.view.category.CategoryHelper

class InstructionHelper(private val layoutInflater: LayoutInflater) {
    private val instructionViews = listOf(
        InstructionView("Pragma", R.drawable.ic_block_pragma,
            layoutInflater, PragmaInstruction(), CategoryHelper.getIoCategory().id
        ),
        InstructionView("Print", R.drawable.ic_block_print,
            layoutInflater, PrintInstruction(), CategoryHelper.getIoCategory().id
        ),
        InstructionView("Input", R.drawable.ic_block_input,
            layoutInflater, InputInstruction(), CategoryHelper.getIoCategory().id
        ),
        InstructionView("Init", R.drawable.ic_block_init,
            layoutInflater, InitInstruction(), CategoryHelper.getVarCategory().id
        ),
        InstructionView("While", R.drawable.ic_block_while,
            layoutInflater, WhileInstruction(), CategoryHelper.getCycleCategory().id,
            getEndWhileView()
        ),
        InstructionView("If", R.drawable.ic_block_if,
            layoutInflater, IfInstruction(), CategoryHelper.getConditionCategory().id
            // TODO
        ),
        InstructionView("Set", R.drawable.ic_block_set,
            layoutInflater, SetInstruction(), CategoryHelper.getVarCategory().id
        ),
        InstructionView("Break", R.drawable.ic_block_break,
            layoutInflater, BreakInstruction(),
            CategoryHelper.getCycleCategory().id, hasText = false
        ),
        InstructionView("Continue", R.drawable.ic_block_continue,
            layoutInflater, ContinueInstruction(),
            CategoryHelper.getCycleCategory().id, hasText = false
        ),
        InstructionView("Method", R.drawable.ic_block_func,
            layoutInflater, FuncInstruction(), CategoryHelper.getFuncCategory().id,
            getEndFuncView()
        ),
        InstructionView("Return", R.drawable.ic_block_return,
            layoutInflater, ReturnInstruction(), CategoryHelper.getFuncCategory().id
        ),
        InstructionView("Call", R.drawable.ic_block_call,
            layoutInflater, CallInstruction(), CategoryHelper.getFuncCategory().id
        ),
        InstructionView("For", R.drawable.ic_block_for,
            layoutInflater, ForInstruction(), CategoryHelper.getCycleCategory().id,
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