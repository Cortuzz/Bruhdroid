package com.example.bruhdroid.view.category

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bruhdroid.R
import com.example.bruhdroid.databinding.BottomsheetFragmentBinding
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.view.instruction.InstructionView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class CategoryHelper(
    private val ctx: Context,
    layoutInflater: LayoutInflater,
    private val dp: Float,
): CategoryAdapter.OnCategoryListener {
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private var bindingSheetMenu: BottomsheetFragmentBinding =
        DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_fragment, null, false)
    private var bottomSheetMenu = BottomSheetDialog(ctx)
    private val categoryList = LinkedList<Category>()

    companion object {
        fun getVarCategory(): Category {
            return Category(0, "Variables | ")
        }

        fun getIoCategory(): Category {
            return Category(1, "Standard io | ")
        }

        fun getCycleCategory(): Category {
            return Category(2, "Cycles | ")
        }

        fun getConditionCategory(): Category {
            return Category(3, "Conditions | ")
        }

        fun getFuncCategory(): Category {
            return Category(4, "Functions")
        }
    }

    init {
        categoryList.add(getVarCategory())
        categoryList.add(getIoCategory())
        categoryList.add(getCycleCategory())
        categoryList.add(getConditionCategory())
        categoryList.add(getFuncCategory())
    }

    fun show() {
        bottomSheetMenu.show()
    }

    fun updateCategories(allInstructions: List<InstructionView>) {
        bottomSheetMenu.setContentView(bindingSheetMenu.root)

        categoryRecycler(categoryList)

        for (templateInstructionView in allInstructions) {
            val instructionView = templateInstructionView.clone()

            if (instructionView.categoryType == null)
                continue

            instructionView.updateBlockView(ctx)
            addToCategory(instructionView)
        }

        onCategoryClick(0)
    }

    private fun addToCategory(instructionView: InstructionView) {
        for (category in categoryList) {
            if (category.id == instructionView.categoryType) {
                category.instructionViews.add(instructionView)
                return
            }
        }
        throw NotFoundException("No such category")
    }

    fun getInstructionViews(): List<InstructionView> {
        val views = mutableListOf<InstructionView>()

        for (category in categoryList) {
            views.addAll(category.instructionViews)
        }
        return views
    }

    private fun categoryRecycler(categoryList: LinkedList<Category>) {
        val layoutManager = LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false)
        categoryRecycler = bindingSheetMenu.categoryRecycler
        categoryRecycler.layoutManager = layoutManager
        categoryAdapter = CategoryAdapter(ctx, categoryList, this)
        categoryRecycler.adapter = categoryAdapter
    }

    override fun onCategoryClick(position: Int) {
        bindingSheetMenu.blocks.removeAllViews()
        val blockMenuParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (110 * dp).toInt()
        )

        for (view in categoryList[position].instructionViews) {
            bindingSheetMenu.blocks.addView(view.view, blockMenuParams)
        }
    }
}