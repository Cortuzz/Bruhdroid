package com.example.bruhdroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityCodingBinding
import com.example.bruhdroid.databinding.BottomsheetFragmentBinding
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class CodingActivity : AppCompatActivity(), Observer {
    private var currentDragIndex = 0
    private var viewToBlock = mutableMapOf<View, Block>()
    private var viewList = mutableListOf<View>()
    private var connectorsMap = mutableMapOf<View, View>()
    private var prevBlock: View? = null

    private lateinit var binding : ActivityCodingBinding
    private lateinit var bindingSheet: BottomsheetFragmentBinding
    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var currentDrag: View

    private val interpreter = Interpreter()
    private val controller = Controller()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)
        bindingSheet = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_fragment, null, false)
        bottomSheet = BottomSheetDialog(this@CodingActivity)
        bottomSheet.setContentView(bindingSheet.root)

        controller.addObserver(this)
        interpreter.addObserver(this)

        binding.menuButton.setOnClickListener {
            bottomSheet.show()
        }
        binding.launchButton.setOnClickListener {
            controller.runProgram(interpreter, viewToBlock, viewList)
        }

        bindingSheet.blockInit.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_init, Instruction.INIT)
        }
        bindingSheet.blockPrint.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_print, Instruction.PRINT)
        }
        bindingSheet.blockInput.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_input, Instruction.INPUT)
        }
        bindingSheet.blockWhile.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_while, Instruction.WHILE, true)
        }
        bindingSheet.blockIf.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_if, Instruction.IF, true)
        }
    }

    override fun onBackPressed() {
        if (!Controller.suppressingWarns) { // todo: Save check
            val builder = buildAlertDialog("DATA WARNING", "There are unsaved changes.\n\n" +
                    "This action will wipe all unsaved changes.")
            builder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                super.onBackPressed()
            }
            builder.setNeutralButton(R.string.suppress_data_warning)  { _: DialogInterface, _: Int ->
                Controller.suppressingWarns = true
                super.onBackPressed()
            }
            builder.setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int -> }
            builder.show()

            return
        }
        super.onBackPressed()
    }

    private fun generateDropArea(v: View, event: DragEvent): Boolean {
        val receiverView: ConstraintLayout = v as ConstraintLayout

        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    v.invalidate()
                    true
                } else {
                    false
                }
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {
                if (currentDrag === receiverView) {
                    return false
                }
                var newIndex = viewList.indexOf(receiverView)

                newIndex = when(newIndex > viewList.indexOf(currentDrag)) {
                    true -> newIndex - 1
                    false -> newIndex
                }
                newIndex = when(event.y < receiverView.height / 2) {
                    true -> newIndex
                    else -> newIndex + 1
                }

                val instr = viewToBlock[currentDrag]!!.instruction
                if (instr == Instruction.WHILE || instr == Instruction.IF) {
                    reBuildBlocks(newIndex, currentDrag, true)
                } else {
                    reBuildBlocks(newIndex, currentDrag)
                }

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                GlobalScope.launch {
                    runOnUiThread {
                        currentDrag.visibility = View.VISIBLE
                        if (viewToBlock[currentDrag]!!.instruction == Instruction.WHILE || viewToBlock[currentDrag]!!.instruction == Instruction.IF) {
                            var index = viewList.indexOf(currentDrag) + 1
                            val instruction = when (viewToBlock[currentDrag]!!.instruction){
                                Instruction.WHILE -> Instruction.END_WHILE
                                Instruction.IF -> Instruction.END
                                else -> false
                            }
                            while (viewToBlock[viewList[index]]!!.instruction !== instruction) {
                                viewList[index].visibility = View.VISIBLE
                                index++
                            }
                            viewList[index].visibility = View.VISIBLE
                            connectorsMap[viewList[index]]!!.visibility = View.VISIBLE
                        }
                    }
                }
                v.invalidate()
                true
            }

            else -> {
                false
            }
        }
    }

    private fun replaceUntilEnd(indexLast: Int, indexNew: Int): List<Int> {
        val endInstructions = listOf(Instruction.END, Instruction.END_WHILE) //todo: elif / else check
        val startInstructions = listOf(Instruction.IF, Instruction.WHILE)
        val tempViews = mutableListOf<View>()
        var height = 0
        var count = 0

        do {
            val view = viewList.removeAt(indexLast)
            when (viewToBlock[view]!!.instruction) {
                in endInstructions -> --count
                in startInstructions -> ++count
                else -> {}
            }

            height += view.height + 10
            tempViews.add(view)
        } while(count > 0)

        val size: Int = tempViews.size
        while (tempViews.size > 0) {
            viewList.add(indexNew, tempViews.removeLast())
        }

        return listOf(height, size)
    }

    private fun clearConstraints(set: ConstraintSet, view: View) {
        val id = view.id

        set.clear(id, ConstraintSet.TOP)
        set.clear(id, ConstraintSet.BOTTOM)
        set.clear(id, ConstraintSet.LEFT)
        set.clear(id, ConstraintSet.RIGHT)

        val connector = connectorsMap[view]?.id
        if (connector != null) {
            set.clear(connector, ConstraintSet.TOP)
            set.clear(connector, ConstraintSet.BOTTOM)
            set.clear(connector, ConstraintSet.LEFT)
            set.clear(connector, ConstraintSet.RIGHT)
        }
    }

    private fun getRatio(ind: Int, y1: Float, y2: Float, h: Int, new: Int, old: Int, c: Int, cycleLength: Int): Float? {
        val prevSize = y1 - y2
        val cycleStart = ind - cycleLength - 1
        val decrease = (new + c > ind || new <= cycleStart) && cycleStart < old + c && old + c <= ind
        val increase = (old + c > ind || old <= cycleStart) && cycleStart < new && new + c <= ind // todo: cycleStart < new ? c ?

        if (decrease && increase) {
            return null
        }

        if (decrease) {
            return prevSize - h
        }
        if (increase) {
            return prevSize + h
        }
        return null
    }

    private fun buildConstraints(set: ConstraintSet, height: Int, newIndex: Int, lastIndex: Int, count: Int) {
        val endInstructions = listOf(Instruction.END, Instruction.END_WHILE) //todo: elif / else check
        val startInstructions = listOf(Instruction.IF, Instruction.WHILE)

        val nestViews = mutableListOf<View>()
        val nestCount = mutableListOf<Int>()

        for (i in 1 until viewList.size) {
            val view = viewList[i]
            val prevView = viewList[i - 1]

            if (viewToBlock[prevView]!!.instruction in startInstructions) {
                nestViews.add(prevView)
                nestCount.add(0)
            }

            if (viewToBlock[view]!!.instruction in endInstructions) {
                val nest = nestViews.removeLast()
                val nestId = nest.id
                val connector = connectorsMap[view]!!
                val connectorId = connector.id

                val ratio = getRatio(i, view.y, nest.y, height, newIndex, lastIndex, count, nestCount.removeLast())?.div(connector.height)
                if (ratio != null) {
                    set.setScaleY(connectorId, ratio)
                }

                set.connect(connectorId, ConstraintSet.TOP, nestId, ConstraintSet.BOTTOM, 0)
                set.connect(connectorId, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connectorId, ConstraintSet.LEFT, nestId, ConstraintSet.LEFT, 10)
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind]++ }
                set.connect(view.id, ConstraintSet.LEFT, nestViews.last().id, ConstraintSet.LEFT, 150)
            }

            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 10)
        }
    }

    private fun reBuildBlocks(index: Int, drag: View, untilEnd: Boolean = false) {
        val set = ConstraintSet()
        val lastIndex = viewList.indexOf(drag)
        val blockHeight: Int
        val count: Int


        set.clone(binding.container)
        for  (i in 0 until viewList.size) {
            clearConstraints(set, viewList[i])
        }

        if (untilEnd) {
            val data = replaceUntilEnd(viewList.indexOf(drag), index)
            blockHeight = data[0]
            count = data[1]
        } else {
            blockHeight = drag.height
            count = 1

            viewList.remove(drag)
            if (index > viewList.lastIndex) {
                viewList.add(drag)
            } else {
                viewList.add(index, drag)
            }

        }

        buildConstraints(set, blockHeight, index, lastIndex, count)
        prevBlock = viewList.last()
        set.applyTo(binding.container)
    }

    private fun generateDragArea(view: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return
        }

        view.apply {
            tag = ""
            setOnLongClickListener { v ->
                currentDrag = v
                currentDragIndex = viewList.indexOf(v)

                val item = ClipData.Item(v.tag as? CharSequence)

                val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                val myShadow = View.DragShadowBuilder(v)

                v.startDragAndDrop(dragData, myShadow, null, 0)
                v.visibility = View.INVISIBLE

                if (viewToBlock[currentDrag]!!.instruction == Instruction.WHILE || viewToBlock[currentDrag]!!.instruction == Instruction.IF) {
                    var index = viewList.indexOf(currentDrag) + 1
                    val instruction = when (viewToBlock[currentDrag]!!.instruction){
                        Instruction.WHILE -> Instruction.END_WHILE
                        Instruction.IF -> Instruction.END
                        else -> false
                    }
                    while (viewToBlock[viewList[index]]!!.instruction !== instruction) {
                        viewList[index].visibility = View.INVISIBLE
                        index++
                    }
                    viewList[index].visibility = View.INVISIBLE
                    connectorsMap[viewList[index]]!!.visibility = View.INVISIBLE
                }

                true
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction, connect: Boolean = false) {
        val view = layoutInflater.inflate(layoutId, null)
        viewList.add(view)

        var endBlock: View? = null
        var connector: View? = null

        if (connect) {
            val endInstruction: Instruction

            if (instruction == Instruction.WHILE) {
                endBlock = layoutInflater.inflate(R.layout.empty_block, null)
                endInstruction = Instruction.END_WHILE

            } else {
                endBlock = layoutInflater.inflate(R.layout.condition_block_end, null)
                endInstruction = Instruction.END
            }

            connector = layoutInflater.inflate(R.layout.block_connector, null)

            binding.container.addView(connector)
            binding.container.addView(endBlock)

            viewList.add(endBlock)
            viewToBlock[endBlock] = Block(endInstruction, "")
            endBlock.id = View.generateViewId()
            connector.id = View.generateViewId()

            endBlock.setOnDragListener { v, event ->
                generateDropArea(v, event)
            }
        }

        binding.container.addView(view)
        view.id = View.generateViewId()

        val set = ConstraintSet()
        set.clone(binding.container)

        generateDragArea(view)
        view.setOnDragListener { v, event ->
            generateDropArea(v, event)
        }

        if (prevView != null) {
            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 10)
        }
        prevBlock = view

        if (endBlock != null && connector != null) {
            connectorsMap[endBlock] = connector
            set.connect(connector.id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, 0)
            set.connect(connector.id, ConstraintSet.BOTTOM, endBlock.id, ConstraintSet.TOP, 0)
            set.connect(connector.id, ConstraintSet.LEFT, view.id, ConstraintSet.LEFT, 10)
            set.connect(endBlock.id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, 10)
            prevBlock = endBlock
        }
        set.applyTo(binding.container)
        viewToBlock[view] = Block(instruction, "")
    }

    private fun buildAlertDialog(label: String, message: String): AlertDialog.Builder {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle(label)
        builder.setMessage(message)

        return builder
    }

    private fun showErrorDialog(label: String, message: String) {
        val builder = buildAlertDialog(label, message)
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> }
        builder.show()
    }

    override fun update(p0: Observable?, p1: Any?) {
        val lexerErrors = controller.popLexerErrors()
        val runtimeErrors = controller.popRuntimeErrors()
        val output = interpreter.output

        runOnUiThread {
            if (runtimeErrors.isNotEmpty()) {
                showErrorDialog("RUNTIME ERROR", runtimeErrors)
            }

            if (lexerErrors.isNotEmpty()) {
                showErrorDialog("LEXER ERROR", lexerErrors)
                return@runOnUiThread
            }
            if (output.isNotEmpty()) {
                binding.console.text = output
            }

                GlobalScope.launch {
                controller.resumeProgram()
            }
        }
    }
}
