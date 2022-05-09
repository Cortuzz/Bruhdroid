package com.example.bruhdroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class CodingActivity : AppCompatActivity(), Observer {
    private var viewToBlock = mutableMapOf<View, Block>()
    private var viewList = mutableListOf<View>()
    private var connectorsMap = mutableMapOf<View, View>()
    private var prevBlock: View? = null

    private lateinit var binding: ActivityCodingBinding
    private lateinit var bindingSheet: BottomsheetFragmentBinding
    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var currentDrag: View

    private val interpreter = Interpreter()
    private val controller = Controller()
    private val connectingInstructions = listOf(Instruction.END, Instruction.END_WHILE)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)
        bindingSheet = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_fragment, null, false)
        bottomSheet = BottomSheetDialog(this@CodingActivity)
        bottomSheet.setContentView(bindingSheet.root)

        val blocks=intent.getSerializableExtra("blocks")
        if(blocks is Array<*>){
            parseBlocks(blocks)
        }

        controller.addObserver(this)
        interpreter.addObserver(this)

        binding.menuButton.setOnClickListener {
            bottomSheet.show()
        }
        binding.launchButton.setOnClickListener {
            controller.runProgram(interpreter, viewToBlock, viewList)
        }

        bindingSheet.blockPrint.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_print, Instruction.PRINT, false, bindingSheet.expression1.text.toString())
        }
        bindingSheet.blockInit.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_init, Instruction.INIT, false, bindingSheet.expression3.text.toString())
        }
        bindingSheet.blockInput.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_input, Instruction.INPUT, false, bindingSheet.expression2.text.toString())
        }
        bindingSheet.blockWhile.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_while, Instruction.WHILE, true, bindingSheet.expression4.text.toString())
        }
        bindingSheet.blockIf.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_if, Instruction.IF, true, bindingSheet.expression5.text.toString())
        }

        binding.binButton.setOnDragListener { v, event ->
            generateDropAreaForBin(v, event)
        }
        bindingSheet.blockSet.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_set, Instruction.SET, false, bindingSheet.expression6.text.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun parseBlocks(blocks: Array<*>) {
        val layoutMap = mapOf(
            Instruction.PRINT to R.layout.block_print,
            Instruction.INIT to R.layout.block_init,
            Instruction.WHILE to R.layout.block_while,
            Instruction.IF to R.layout.block_if,
            Instruction.SET to R.layout.block_set,
            Instruction.END_WHILE to R.layout.empty_block,
            Instruction.END to R.layout.condition_block_end
        )

        GlobalScope.launch {
            for (block in blocks) {
                block as Block
                val instr = block.instruction
                val view = layoutInflater.inflate(layoutMap[instr]!!, null)

                if (instr in connectingInstructions) {
                    val connector = layoutInflater.inflate(R.layout.block_connector, null)
                    connector.id = View.generateViewId()
                    connectorsMap[view] = connector
                    runOnUiThread {
                        binding.container.addView(connector)
                    }
                } else {
                    generateDragArea(view)
                }
                view.setOnDragListener { v, event ->
                    generateDropArea(v, event)
                }

                view.id = View.generateViewId()

                viewList.add(view)
                prevBlock = view
                viewToBlock[view] = Block(instr, "")
            }
            runOnUiThread {
                for (view in viewList) {
                    binding.container.addView(view)
                }
            }
            delay(100)

            runOnUiThread {
                buildConstraints()
            }
        }
    }

    override fun onBackPressed() {
        if (!Controller.suppressingWarns) { // todo: Save check
            val builder = buildAlertDialog(
                "DATA WARNING", "There are unsaved changes.\n\n" +
                        "This action will wipe all unsaved changes."
            )
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

    private fun generateDropAreaForBin(v: View, event:DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                    true
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
                val index = -1
                val instr = viewToBlock[currentDrag]!!.instruction
                if (instr == Instruction.WHILE || instr == Instruction.IF) {
                    reBuildBlocks(index , currentDrag, true)
                } else {
                    reBuildBlocks(index , currentDrag)
                }

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                GlobalScope.launch {
                    runOnUiThread {
                        if (viewToBlock[currentDrag] != null) {
                            makeBlocksVisible(currentDrag)
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

    private fun generateDropArea(v: View, event: DragEvent): Boolean {
        val receiverView: ConstraintLayout = v as ConstraintLayout

        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
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

                newIndex = when (newIndex > viewList.indexOf(currentDrag)) {
                    true -> newIndex - 1
                    false -> newIndex
                }
                newIndex = when (event.y < receiverView.height / 2) {
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
            if (indexNew == -1) {
                viewToBlock.remove(view)
            }

            height += view.height + 10
            tempViews.add(view)
        } while (count > 0)

        val size: Int = tempViews.size

        if (indexNew != -1) {
            while (tempViews.size > 0) {
                viewList.add(indexNew, tempViews.removeLast())
            }
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

    private fun buildConstraints() {
        val set = ConstraintSet()
        set.clone(binding.container)
        val endInstructions = listOf(Instruction.END, Instruction.END_WHILE) //todo: elif / else check
        val startInstructions = listOf(Instruction.IF, Instruction.WHILE)

        val nestViews = mutableListOf<View>()
        val nestCount = mutableListOf<Int>()

        for (i in 1 until viewList.size) {
            val view = viewList[i]
            val prevView = viewList[i - 1]

            if (viewToBlock[prevView]!!.instruction in startInstructions) {
                nestViews.add(prevView)
                nestCount.add(prevView.height)
            }

            if (viewToBlock[view]!!.instruction in endInstructions) {
                val nest = nestViews.removeLast()
                val nestId = nest.id
                val connector = connectorsMap[view]!!
                val connectorId = connector.id
                val ratio = (nestCount.removeLast()) / (connector.height).toFloat()
                set.setScaleY(connectorId, ratio)

                set.connect(connectorId, ConstraintSet.TOP, nestId, ConstraintSet.BOTTOM, 0)
                set.connect(connectorId, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connectorId, ConstraintSet.LEFT, nestId, ConstraintSet.LEFT, 10)
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind] += view.height + 10 }
                set.connect(view.id, ConstraintSet.LEFT, nestViews.last().id, ConstraintSet.LEFT, 200)
            }

            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 10)
        }
        set.applyTo(binding.container)
    }

    private fun reBuildBlocks(index: Int, drag: View, untilEnd: Boolean = false) {
        GlobalScope.launch {
            val set = ConstraintSet()

            set.clone(binding.container)
            for (i in 0 until viewList.size) {
                clearConstraints(set, viewList[i])
            }

            if (untilEnd) {
                replaceUntilEnd(viewList.indexOf(drag), index)
            } else {
                viewList.remove(drag)
                when {
                    index == -1 -> {
                        viewToBlock.remove(drag)
                    }
                    index > viewList.lastIndex -> {
                        viewList.add(drag)
                    }
                    else -> {
                        viewList.add(index, drag)
                    }
                }
            }

            runOnUiThread {
                set.applyTo(binding.container)
                buildConstraints()
            }

            prevBlock = if (viewList.isEmpty()) {
                null
            } else {
                viewList.last()
            }
        }
    }

    private fun makeBlocksInvisible(v: View) {
        v.visibility = View.INVISIBLE

        if (viewToBlock[v]!!.instruction == Instruction.WHILE || viewToBlock[v]!!.instruction == Instruction.IF) {
            var index = viewList.indexOf(v) + 1
            var count = 1

            while (count != 0) {
                viewList[index].visibility = View.INVISIBLE
                val block = viewToBlock[viewList[index]]
                if (block!!.instruction == Instruction.END_WHILE || block.instruction == Instruction.END) {
                    connectorsMap[viewList[index]]!!.visibility = View.INVISIBLE
                    count--
                } else if (block.instruction == Instruction.WHILE || block.instruction == Instruction.IF) {
                    count++
                }
                index++
            }
        }
    }

    private fun makeBlocksVisible(v: View) {
        v.visibility = View.VISIBLE

        if (viewToBlock[v]!!.instruction == Instruction.WHILE || viewToBlock[v]!!.instruction == Instruction.IF) {
            var index = viewList.indexOf(v) + 1
            var count = 1

            while (count != 0) {
                viewList[index].visibility = View.VISIBLE
                val block = viewToBlock[viewList[index]]
                if (block!!.instruction == Instruction.END_WHILE || block.instruction == Instruction.END) {
                    connectorsMap[viewList[index]]!!.visibility = View.VISIBLE
                    count--
                } else if (block.instruction == Instruction.WHILE || block.instruction == Instruction.IF) {
                    count++
                }
                index++
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun generateDragArea(view: View) {
        view.setOnLongClickListener {
            currentDrag = it
            makeBlocksInvisible(it)
            it.startDragAndDrop(null, View.DragShadowBuilder(it), it, 0)
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("InflateParams")
    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction, connect: Boolean = false, text: String) {
        val view = layoutInflater.inflate(layoutId, null)
        view.findViewById<EditText>(R.id.expression)?.setText(text)
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
        viewToBlock[view] = Block(instruction, text)
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
