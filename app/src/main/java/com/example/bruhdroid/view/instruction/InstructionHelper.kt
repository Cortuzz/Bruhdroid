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
    private val baseBlockWidth = 400f
    private val baseBlockHeight = 110f
    private val baseSmallBlockWidth = 200f
    private val baseSmallBlockHeight = 80f

    private val instructionViews = listOf(
        InstructionView("Pragma", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_pragma,
            layoutInflater, PragmaInstruction(), CategoryHelper.getIoCategory().id
        ),
        InstructionView("Print", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_print,
            layoutInflater, PrintInstruction(), CategoryHelper.getIoCategory().id
        ),
        InstructionView("Input", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_input,
            layoutInflater, InputInstruction(), CategoryHelper.getIoCategory().id
        ),
        InstructionView("Init", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_init,
            layoutInflater, InitInstruction(), CategoryHelper.getVarCategory().id
        ),
        InstructionView("While", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_while,
            layoutInflater, WhileInstruction(), CategoryHelper.getCycleCategory().id,
            getEndWhileView()
        ),
        InstructionView("If", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_if,
            layoutInflater, IfInstruction(), CategoryHelper.getConditionCategory().id,
            getEndConditionView()
        ),
        InstructionView("Set", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_set,
            layoutInflater, SetInstruction(), CategoryHelper.getVarCategory().id
        ),
        InstructionView("Break", baseSmallBlockWidth, baseSmallBlockHeight, R.drawable.ic_block_break,
            layoutInflater, BreakInstruction(),
            CategoryHelper.getCycleCategory().id, hasText = false
        ),
        InstructionView("Continue", baseSmallBlockWidth, baseSmallBlockHeight, R.drawable.ic_block_continue,
            layoutInflater, ContinueInstruction(),
            CategoryHelper.getCycleCategory().id, hasText = false
        ),
        InstructionView("Method", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_func,
            layoutInflater, FuncInstruction(), CategoryHelper.getFuncCategory().id,
            getEndFuncView()
        ),
        InstructionView("Return", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_return,
            layoutInflater, ReturnInstruction(), CategoryHelper.getFuncCategory().id
        ),
        InstructionView("Call", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_call,
            layoutInflater, CallInstruction(), CategoryHelper.getFuncCategory().id
        ),
        InstructionView("For", baseBlockWidth, baseBlockHeight, R.drawable.ic_block_for,
            layoutInflater, ForInstruction(), CategoryHelper.getCycleCategory().id,
            getEndForView()
        ),
        getEndConditionView(), getElifView(), getElseView(),
        getEndWhileView(), getEndForView(), getEndFuncView()
    )

    fun getEndConditionView(): InstructionView {
        return SubsequentInstructionView("", baseSmallBlockWidth, baseSmallBlockHeight,
            R.layout.condition_block_end, layoutInflater, EndInstruction()
        )
    }

    fun getEndFuncView(): InstructionView {
        return SubsequentInstructionView("",baseSmallBlockWidth, baseSmallBlockHeight,
            R.layout.block_func_end, layoutInflater, FuncEndInstruction()
        )
    }

    fun getEndForView(): InstructionView {
        return SubsequentInstructionView("", baseSmallBlockWidth, baseSmallBlockHeight,
            R.layout.block_end_for, layoutInflater, EndForInstruction()
        )
    }

    fun getElseView(): InstructionView {
        return SubsequentInstructionView("", baseSmallBlockWidth, baseSmallBlockHeight,
            R.layout.block_else, layoutInflater, ElseInstruction()
        )
    }

    fun getElifView(): InstructionView {
        return SubsequentInstructionView("", baseBlockWidth, baseBlockHeight,
            R.layout.block_elif, layoutInflater, ElifInstruction()
        )
    }

    fun getEndWhileView(): InstructionView {
        return SubsequentInstructionView("", baseSmallBlockWidth, baseSmallBlockHeight,
            R.layout.empty_block, layoutInflater, EndWhileInstruction()
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