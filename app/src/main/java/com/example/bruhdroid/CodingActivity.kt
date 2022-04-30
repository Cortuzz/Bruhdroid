package com.example.bruhdroid

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import androidx.annotation.UiThread
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class CodingActivity : AppCompatActivity(), Observer {
    private var currentDragIndex = 0
    private var viewToBlock = mutableMapOf<View, Block>()
    private var viewList = mutableListOf<View>()
    private var connectorsMap = mutableMapOf<View, Int>()
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

            DragEvent.ACTION_DRAG_LOCATION ->
                true

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

                if (viewToBlock[currentDrag]!!.instruction == Instruction.WHILE) {
                    reBuildBlocks(newIndex, currentDrag, true)
                } else {
                    reBuildBlocks(newIndex, currentDrag)
                }

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                v.invalidate()
                true
            }

            else -> {
                false
            }
        }
    }

    private fun replaceUntilEnd(indexLast: Int, indexNew: Int) {
        val endInstructions = listOf(Instruction.END, Instruction.END_WHILE)
        val startInstructions = listOf(Instruction.IF, Instruction.WHILE)
        val tempViews = mutableListOf<View>()
        var count = 0

        do {
            val view = viewList.removeAt(indexLast)
            when (viewToBlock[view]!!.instruction) {
                in endInstructions -> --count
                in startInstructions -> ++count
            }

            tempViews.add(view)
        } while(count > 0)

        while (tempViews.size > 0) {
            viewList.add(indexNew, tempViews.removeLast())
        }
    }

    private fun clearConstraints(set: ConstraintSet, view: View) {
        val id = view.id

        set.clear(id, ConstraintSet.TOP)
        set.clear(id, ConstraintSet.BOTTOM)
        set.clear(id, ConstraintSet.LEFT)
        set.clear(id, ConstraintSet.RIGHT)

        val connector = connectorsMap[view]
        if (connector != null) {
            set.clear(connector, ConstraintSet.TOP)
            set.clear(connector, ConstraintSet.BOTTOM)
            set.clear(connector, ConstraintSet.LEFT)
            set.clear(connector, ConstraintSet.RIGHT)
        }
    }

    private fun reBuildBlocks(index: Int, drag: View, untilEnd: Boolean = false) {
        val nestViews = mutableListOf<View>()
        val nestCount = mutableListOf<Int>()
        val set = ConstraintSet()
        set.clone(binding.container)
        for  (i in 0 until viewList.size) {
            clearConstraints(set, viewList[i])
        }

        if (untilEnd) {
            replaceUntilEnd(viewList.indexOf(drag), index)
        } else {
            viewList.remove(drag)
            if (index > viewList.lastIndex) {
                viewList.add(drag)
            } else {
                viewList.add(index, drag)
            }

        }

        for (i in 1 until viewList.size) {
            val view = viewList[i]
            val prevView = viewList[i - 1]
            if (viewToBlock[prevView]!!.instruction == Instruction.WHILE &&
                    viewToBlock[view]!!.instruction == Instruction.END_WHILE) {

                val nest = prevView.id
                val connector = connectorsMap[view]!!
                set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 50)
                set.connect(view.id, ConstraintSet.LEFT, prevView.id, ConstraintSet.LEFT, 0)

                set.connect(connector, ConstraintSet.TOP, nest, ConstraintSet.BOTTOM, 0)
                set.connect(connector, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connector, ConstraintSet.LEFT, nest, ConstraintSet.LEFT, 0)
                continue
            }

            if (viewToBlock[prevView]!!.instruction == Instruction.WHILE) {
                nestViews.add(prevView)
                nestCount.add(0)
            }

            if (viewToBlock[view]!!.instruction == Instruction.END_WHILE) {
                val count = nestCount.removeLast()
                val nest = nestViews.removeLast().id
                val connector = connectorsMap[view]!!
                set.setScaleY(connector, count * 1f)

                set.connect(connector, ConstraintSet.TOP, nest, ConstraintSet.BOTTOM, 0)
                set.connect(connector, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                //set.connect(view.id, ConstraintSet.LEFT, nest, ConstraintSet.LEFT, 150)

                set.connect(connector, ConstraintSet.LEFT, nest, ConstraintSet.LEFT, 0)
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind]++ }
                set.connect(view.id, ConstraintSet.LEFT, nestViews.last().id, ConstraintSet.LEFT, 150)
            }

            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 10)
        }
        prevBlock = viewList.last()
        set.applyTo(binding.container)
    }

    private fun generateDragArea(view: View) {
        if (applicationInfo.targetSdkVersion < 24) {
            return
        }

        view.apply {
            tag = ""
            setOnLongClickListener { v ->
                currentDrag = v
                currentDragIndex = viewList.indexOf(v)
                if (applicationInfo.targetSdkVersion >= 24) {
                    val item = ClipData.Item(v.tag as? CharSequence)

                    val dragData = ClipData(v.tag as? CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
                    val myShadow = MyDragShadowBuilder(view)

                    v.startDragAndDrop(dragData, myShadow, null, 0)
                }
                true
            }
        }
    }

    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction, connect: Boolean = false) {
        val view = layoutInflater.inflate(layoutId, null)
        viewList.add(view)

        var endBlock: View? = null
        var connector: View? = null

        if (connect) {
            endBlock = layoutInflater.inflate(R.layout.empty_block, null)
            connector = layoutInflater.inflate(R.layout.block_connector, null)

            binding.container.addView(endBlock)
            binding.container.addView(connector)

            viewList.add(endBlock)
            viewToBlock[endBlock] = Block(Instruction.END_WHILE, "")
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
            connectorsMap[endBlock] = connector.id
            set.connect(endBlock.id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, 50)

            set.connect(connector.id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, -100)
            set.connect(connector.id, ConstraintSet.BOTTOM, endBlock.id, ConstraintSet.TOP, -100)

            prevBlock = endBlock
        }
        set.applyTo(binding.container)
        viewToBlock[view] = Block(instruction, "")
    }

    private fun removeBlock(view: View) {
        val block: Block = viewToBlock[view] ?: return
        if (!(block.instruction == Instruction.IF || block.instruction == Instruction.ELIF || block.instruction == Instruction.ELSE)) {
            viewToBlock.remove(view)
            viewList.remove(view)
            return
        }
        var counter = 1
        val index = viewList.indexOf(view)
        while (counter > 0) {

            if (viewToBlock[viewList[index]]!!.instruction == Instruction.END) {
                counter--
            }
            if (viewToBlock[viewList[index]]!!.instruction == Instruction.IF) {
                counter++
            }
            viewToBlock.remove(viewList[index])
            viewList.removeAt(index)
        }

    }

    private fun buildAlertDialog(label: String, message: String) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle(label)
        builder.setMessage(message)

        builder.setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    override fun update(p0: Observable?, p1: Any?) {
        val lexerErrors = controller.popLexerErrors()
        val runtimeErrors = controller.popRuntimeErrors()
        val output = interpreter.output

        runOnUiThread {
            if (runtimeErrors.isNotEmpty()) {
                buildAlertDialog("RUNTIME ERROR", runtimeErrors)
            }

            if (lexerErrors.isNotEmpty()) {
                buildAlertDialog("LEXER ERROR", lexerErrors)
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
