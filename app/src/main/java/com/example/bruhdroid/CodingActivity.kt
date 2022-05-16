package com.example.bruhdroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.View.FOCUS_UP
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.window.layout.WindowMetricsCalculator
import com.example.bruhdroid.databinding.ActivityCodingBinding
import com.example.bruhdroid.databinding.BottomsheetBinBinding
import com.example.bruhdroid.databinding.BottomsheetConsoleBinding
import com.example.bruhdroid.databinding.BottomsheetFragmentBinding
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class CodingActivity : AppCompatActivity(), Observer, CategoryAdapter.OnCategoryListener {
    private enum class Debug {
        NEXT, BREAKPOINT
    }

    private var viewToBlock = mutableMapOf<View, Block>()
    private var codingViewList = LinkedList<View>()
    private var binViewList = LinkedList<View>()
    private var connectorsMap = mutableMapOf<View, View>()
    private var categoryBlocks = LinkedList<LinkedList<View>>()
    private var prevBlock: View? = null
    private var prevBlockInBin: View? = null
    private var debugMode = false

    private lateinit var debugType: Debug
    private lateinit var currentDrag: View

    private lateinit var binding: ActivityCodingBinding
    private lateinit var bindingSheetMenu: BottomsheetFragmentBinding
    private lateinit var bindingSheetBin: BottomsheetBinBinding
    private lateinit var bindingSheetConsole: BottomsheetConsoleBinding
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var bottomSheetMenu: BottomSheetDialog
    private lateinit var bottomSheetBin: BottomSheetDialog
    private lateinit var bottomSheetConsole: BottomSheetDialog

    private val interpreter = Interpreter()
    private val controller = Controller()
    private val connectingInstructions = listOf(Instruction.END, Instruction.END_WHILE)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)

        setBindingSheetBlocks()

        bindingSheetConsole = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_console, null, false)
        bottomSheetConsole = BottomSheetDialog(this@CodingActivity)
        bottomSheetConsole.setContentView(bindingSheetConsole.root)

        bindingSheetBin = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_bin, null, false)
        bottomSheetBin = BottomSheetDialog(this@CodingActivity)
        bottomSheetBin.setContentView(bindingSheetBin.root)

        val blocks = intent.getSerializableExtra("blocks")
        if (blocks is Array<*>) {
            parseBlocks(blocks)
        }

        controller.addObserver(this)
        interpreter.addObserver(this)

        binding.mainPanel.setOnDragListener { v, event ->
            generateDropAreaForMainPanel(v, event)
        }
        binding.buttonsPanel.setOnDragListener { v, event ->
            generateDropAreaForbuttonsPanel(v, event)
        }

        binding.menuButton.setOnClickListener {
            bottomSheetMenu.show()
        }
        binding.binButton.setOnClickListener {
            bottomSheetBin.show()
        }
        binding.consoleButton.setOnClickListener {
            bottomSheetConsole.show()
        }
        binding.launchButton.setOnClickListener {
            debugMode = false
            bindingSheetConsole.console.text = ""
            controller.runProgram(interpreter, viewToBlock, codingViewList)
        }

        binding.debugButton.setOnClickListener {
            debugMode = true
            debugType = Debug.BREAKPOINT
            bindingSheetConsole.console.text = ""
            binding.debugPanel.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.INVISIBLE

            controller.runProgram(interpreter, viewToBlock, codingViewList)
        }

        binding.nextButton.setOnClickListener {
            debugType = Debug.NEXT
            updateDebugger()

            GlobalScope.launch {
                controller.resumeProgram()
            }
        }

        binding.resumeButton.setOnClickListener {
            debugType = Debug.BREAKPOINT
            updateDebugger()

            GlobalScope.launch {
                controller.resumeProgram()
            }
        }

        binding.pauseButton.setOnClickListener {
            debugType = Debug.NEXT
        }

        binding.stopButton.setOnClickListener {
            binding.debugPanel.visibility = View.INVISIBLE
            binding.mainPanel.visibility = View.VISIBLE
            interpreter.clear()
        }

//        bindingSheetMenu.blockPrint.setOnClickListener {
//            buildBlock(prevBlock, R.layout.block_print, Instruction.PRINT, false, bindingSheetMenu.expression1.text.toString())
//        }
//        bindingSheetMenu.blockInit.setOnClickListener {
//            buildBlock(prevBlock, R.layout.block_init, Instruction.INIT, false, bindingSheetMenu.expression3.text.toString())
//        }
//        bindingSheetMenu.blockInput.setOnClickListener {
//            buildBlock(prevBlock, R.layout.block_input, Instruction.INPUT, false, bindingSheetMenu.expression2.text.toString())
//        }
//        bindingSheetMenu.blockWhile.setOnClickListener {
//            buildBlock(prevBlock, R.layout.block_while, Instruction.WHILE, true, bindingSheetMenu.expression4.text.toString())
//        }
//        bindingSheetMenu.blockIf.setOnClickListener {
//            buildBlock(prevBlock, R.layout.block_if, Instruction.IF, true, bindingSheetMenu.expression5.text.toString())
//        }

        binding.binButton.setOnDragListener { v, event ->
            generateDropAreaForBin(v, event)
        }
//        bindingSheetMenu.blockSet.setOnClickListener {
//            buildBlock(prevBlock, R.layout.block_set, Instruction.SET, false, bindingSheetMenu.expression6.text.toString())
//        }
    }

    private fun setBindingSheetBlocks() {
        bindingSheetMenu =
            DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_fragment, null, false)
        bottomSheetMenu = BottomSheetDialog(this@CodingActivity)
        bottomSheetMenu.setContentView(bindingSheetMenu.root)

        val categoryList = LinkedList<Category>()

        categoryList.add(Category(0, "Variables | "))
        categoryList.add(Category(1, "Standard io | "))
        categoryList.add(Category(2, "Cycles | "))
        categoryList.add(Category(3, "Conditions"))

        categoryRecycler(categoryList)


        val blockInit = layoutInflater.inflate(R.layout.block_init, null)
        val blockSet = layoutInflater.inflate(R.layout.block_set, null)
        val blockInput = layoutInflater.inflate(R.layout.block_input, null)
        val blockPrint = layoutInflater.inflate(R.layout.block_print, null)
        val blockWhile = layoutInflater.inflate(R.layout.block_while, null)
        val blockIf = layoutInflater.inflate(R.layout.block_if, null)

        blockInit.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_init, Instruction.INIT, false)
        }
        blockSet.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_set, Instruction.SET, false)
        }
        blockInput.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_input, Instruction.INPUT, false)
        }
        blockPrint.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_print, Instruction.PRINT, false)
        }
        blockWhile.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_while, Instruction.WHILE, true)
        }
        blockIf.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_if, Instruction.IF, true)
        }

        val firstCategory = LinkedList<View>()
        val secondCategory = LinkedList<View>()
        val thirdCategory = LinkedList<View>()
        val fourthCategory = LinkedList<View>()

        firstCategory.add(blockInit)
        firstCategory.add(blockSet)
        secondCategory.add(blockInput)
        secondCategory.add(blockPrint)
        thirdCategory.add(blockWhile)
        fourthCategory.add(blockIf)

        categoryBlocks.add(firstCategory)
        categoryBlocks.add(secondCategory)
        categoryBlocks.add(thirdCategory)
        categoryBlocks.add(fourthCategory)

        for (view in categoryBlocks[0]) {
            val params = ConstraintLayout.LayoutParams(1000, 300)
            bindingSheetMenu.blocks.addView(view, params)
        }
    }

    private fun categoryRecycler(categoryList: LinkedList<Category>) {
        val layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        categoryRecycler = bindingSheetMenu.categoryRecycler
        categoryRecycler.layoutManager = layoutManager
        categoryAdapter = CategoryAdapter(this, categoryList, this)
        categoryRecycler.adapter = categoryAdapter
    }

    override fun onCategoryClick(position: Int) {
        bindingSheetMenu.blocks.removeAllViews()

        for (view in categoryBlocks[position]) {
            val params = ConstraintLayout.LayoutParams(1000, 300)
            bindingSheetMenu.blocks.addView(view, params)
        }
    }

    private fun updateDebugger() {
        val view = getViewByLine()
        val button = view?.findViewById<ImageButton>(R.id.breakpoint)

        if (button != null) {
            val block = viewToBlock[view] ?: return
            drawBreakpoint(button, block)
        }
    }

    private fun getDebuggerView(): View? {
        val view = getViewByLine()

        if (view == null) {
            runOnUiThread {
                binding.debugPanel.visibility = View.INVISIBLE
                binding.mainPanel.visibility = View.VISIBLE
            }
            return null
        }
        return view
    }

    private fun getViewByLine(): View? {
        fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
            return hashMap.filter { target == it.value }.keys.first()
        }

        return try {
            getKey(viewToBlock, interpreter.blocks?.get(interpreter.currentLine + 1))
        } catch (e: Exception) {
            null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun parseBlocks(blocks: Array<*>) {
        val layoutMap = mapOf(
            Instruction.PRINT to R.layout.block_print,
            Instruction.INPUT to R.layout.block_input,
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
                generateBreakpoint(view)
                view.findViewById<EditText>(R.id.expression)?.setText(block.expression)

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

                codingViewList.add(view)
                prevBlock = view
                viewToBlock[view] = Block(instr, "")
            }
            runOnUiThread {
                for (view in codingViewList) {
                    binding.container.addView(view)
                }
            }
            delay(100)

            runOnUiThread {
                buildConstraints(binding.container, codingViewList)
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
            builder.setNeutralButton(R.string.suppress_data_warning) { _: DialogInterface, _: Int ->
                Controller.suppressingWarns = true
                super.onBackPressed()
            }
            builder.setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int -> }
            builder.show()

            return
        }
        super.onBackPressed()
    }

    private fun generateDropAreaForMainPanel(v: View, event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                binding.mainCode.pageScroll(FOCUS_UP)
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {
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

    private fun generateDropAreaForbuttonsPanel(v: View, event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                binding.mainCode.pageScroll(FOCUS_DOWN)
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun generateDropAreaForBin(v: View, event: DragEvent): Boolean {
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
                    addBlocksToBin(currentDrag, true)
                    reBuildBlocks(index, currentDrag, true)
                } else {
                    addBlocksToBin(currentDrag)
                    reBuildBlocks(index, currentDrag)
                }

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                GlobalScope.launch {
                    runOnUiThread {
                        if (codingViewList.contains(currentDrag)) {
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun addBlocksToBin(view: View, isConnected: Boolean = false) {
        val addedBlocks = removeBlocksFromParent(view, isConnected)

        GlobalScope.launch {
            for (block in addedBlocks) {
                val instr = viewToBlock[block]!!.instruction

                if (instr in connectingInstructions) {
                    runOnUiThread {
                        bindingSheetBin.deletedList.addView(connectorsMap[block])
                    }
                }

                binViewList.add(block)
                prevBlockInBin = block
            }
            runOnUiThread {
                for (block in addedBlocks) {
                    bindingSheetBin.deletedList.addView(block)
                }
            }
            delay(100)

            runOnUiThread {
                buildConstraints(bindingSheetBin.deletedList, binViewList)
            }
        }

        makeBlocksVisible(view)
    }

    private fun removeBlocksFromParent(view: View, isConnected: Boolean = false): List<View> {
        val tempList = mutableListOf<View>()
        tempList.add(view)
        (view.parent as ViewGroup).removeView(view)

        if (isConnected) {
            var index = codingViewList.indexOf(view) + 1
            var count = 1

            while (count != 0) {
                tempList.add(codingViewList[index])
                (codingViewList[index].parent as ViewGroup).removeView(codingViewList[index])
                val block = viewToBlock[codingViewList[index]]
                if (block!!.instruction == Instruction.END_WHILE || block.instruction == Instruction.END) {
                    (connectorsMap[codingViewList[index]]!!.parent as ViewGroup).removeView(connectorsMap[codingViewList[index]])
                    count--
                } else if (block.instruction == Instruction.WHILE || block.instruction == Instruction.IF) {
                    count++
                }
                index++
            }
        }
        return tempList
    }

    private fun generateDropArea(v: View, event: DragEvent): Boolean {
        val receiverView: ConstraintLayout = v as ConstraintLayout

        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                v.invalidate()
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
                var newIndex = codingViewList.indexOf(receiverView)

                newIndex = when (newIndex > codingViewList.indexOf(currentDrag)) {
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

    private fun replaceUntilEnd(indexFrom: Int, indexTo: Int) {
        val endInstructions = listOf(Instruction.END, Instruction.END_WHILE) //todo: elif / else check
        val startInstructions = listOf(Instruction.IF, Instruction.WHILE)
        val tempViews = mutableListOf<View>()
        var count = 0

        do {
            when (viewToBlock[codingViewList[indexFrom]]!!.instruction) {
                in endInstructions -> --count
                in startInstructions -> ++count
                else -> {}
            }
            val view = codingViewList.removeAt(indexFrom)
            tempViews.add(view)
        } while (count > 0)

        if (indexTo != -1) {
            if (indexTo < indexFrom) {
                codingViewList.addAll(indexTo, tempViews)
            } else {
                codingViewList.addAll(indexTo + 1 - tempViews.size, tempViews)
            }
        }
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

    private fun buildConstraints(container: ConstraintLayout, viewList: List<View>) {
        val set = ConstraintSet()
        set.clone(container)
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

                set.connect(connectorId, ConstraintSet.TOP, nestId, ConstraintSet.BOTTOM, -15)
                set.connect(connectorId, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connectorId, ConstraintSet.LEFT, nestId, ConstraintSet.LEFT, 50)
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind] += view.height }
                set.connect(view.id, ConstraintSet.LEFT, nestViews.last().id, ConstraintSet.LEFT, 50)
            }

            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, -15)
        }
        set.applyTo(container)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun reBuildBlocks(index: Int, drag: View, untilEnd: Boolean = false) {
        GlobalScope.launch {
            val set = ConstraintSet()

            set.clone(binding.container)
            for (i in 0 until codingViewList.size) {
                clearConstraints(set, codingViewList[i])
            }
            if (untilEnd) {
                replaceUntilEnd(codingViewList.indexOf(drag), index)
            } else {
                codingViewList.remove(drag)
                when {
                    index == -1 -> {}
                    index > codingViewList.lastIndex -> {
                        codingViewList.add(drag)
                    }
                    else -> {
                        codingViewList.add(index, drag)
                    }
                }
            }

            runOnUiThread {
                set.applyTo(binding.container)
                buildConstraints(binding.container, codingViewList)
            }

            prevBlock = if (codingViewList.isEmpty()) {
                null
            } else {
                codingViewList.last()
            }
        }
    }

    private fun makeBlocksInvisible(v: View) {
        v.visibility = View.INVISIBLE

        if (viewToBlock[v]!!.instruction == Instruction.WHILE || viewToBlock[v]!!.instruction == Instruction.IF) {
            var index = codingViewList.indexOf(v) + 1
            var count = 1

            while (count != 0) {
                codingViewList[index].visibility = View.INVISIBLE
                val block = viewToBlock[codingViewList[index]]
                if (block!!.instruction == Instruction.END_WHILE || block.instruction == Instruction.END) {
                    if (count != 1) {
                        connectorsMap[codingViewList[index]]!!.visibility = View.INVISIBLE
                    }
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
            var index = codingViewList.indexOf(v) + 1
            var count = 1

            while (count != 0) {
                codingViewList[index].visibility = View.VISIBLE
                val block = viewToBlock[codingViewList[index]]
                if (block!!.instruction == Instruction.END_WHILE || block.instruction == Instruction.END) {
                    connectorsMap[codingViewList[index]]!!.visibility = View.VISIBLE
                    count--
                } else if (block.instruction == Instruction.WHILE || block.instruction == Instruction.IF) {
                    count++
                }
                index++
            }
        }
    }

    private fun generateDragArea(view: View) {
        view.setOnLongClickListener {
            currentDrag = it
            makeBlocksInvisible(it)
            it.startDragAndDrop(null, View.DragShadowBuilder(it), it, 0)
            true
        }
    }

    private fun drawBreakpoint(button: ImageButton, block: Block) {
        button.setBackgroundResource(
            when (block.breakpoint) {
                true -> android.R.drawable.presence_online
                false -> android.R.drawable.presence_invisible
            }
        )
    }

    private fun generateBreakpoint(view: View) {
        val button = view.findViewById<ImageButton>(R.id.breakpoint)
        button?.setOnClickListener {
           val block = viewToBlock[view] ?: return@setOnClickListener
           block.breakpoint = !block.breakpoint
           drawBreakpoint(button, block)
        }
    }

    @SuppressLint("InflateParams")
    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction, connect: Boolean = false) {
        val view = layoutInflater.inflate(layoutId, null)
        generateBreakpoint(view)
        codingViewList.add(view)

        var endBlock: View? = null
        var nestedConnector: View? = null
        var connector = layoutInflater.inflate(R.layout.block_connector, null)
        connector.id = View.generateViewId()

        if (connect) {
            val endInstruction: Instruction

            if (instruction == Instruction.WHILE) {
                endBlock = layoutInflater.inflate(R.layout.empty_block, null)
                endInstruction = Instruction.END_WHILE

            } else {
                endBlock = layoutInflater.inflate(R.layout.condition_block_end, null)
                endInstruction = Instruction.END
            }

            generateBreakpoint(endBlock)
            nestedConnector = layoutInflater.inflate(R.layout.block_connector, null)

            codingViewList.add(endBlock)
            viewToBlock[endBlock] = Block(endInstruction, "")
            endBlock.id = View.generateViewId()
            nestedConnector.id = View.generateViewId()

            endBlock.setOnDragListener { v, event ->
                generateDropArea(v, event)
            }
        }

        if (prevView != null) {
            if (viewToBlock[prevView]!!.instruction in connectingInstructions) {
                binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 250))
            } else {
                binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
            }
        }

        binding.container.addView(view, ConstraintLayout.LayoutParams(900, 300))
        view.id = View.generateViewId()

        if (nestedConnector != null) {
            binding.container.addView(nestedConnector, ConstraintLayout.LayoutParams(5, 300))
            binding.container.addView(endBlock)
        }

        val set = ConstraintSet()
        set.clone(binding.container)

        generateDragArea(view)
        view.setOnDragListener { v, event ->
            generateDropArea(v, event)
        }

        if (prevView != null) {
            connectorsMap[view] = connector
            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, -15)

            if (viewToBlock[prevView]!!.instruction in connectingInstructions) {
                set.connect(connector.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 30)
            } else {
                set.connect(connector.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 0)
            }

            set.connect(connector.id, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
            set.connect(connector.id, ConstraintSet.LEFT, view.id, ConstraintSet.LEFT, 80)
        }

        prevBlock = view

        if (nestedConnector != null) {
            connectorsMap[endBlock!!] = nestedConnector
            set.connect(nestedConnector.id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, 0)
            set.connect(nestedConnector.id, ConstraintSet.BOTTOM, endBlock.id, ConstraintSet.TOP, 0)
            set.connect(nestedConnector.id, ConstraintSet.LEFT, view.id, ConstraintSet.LEFT, 80)

            set.connect(endBlock.id, ConstraintSet.TOP, view.id, ConstraintSet.BOTTOM, 10)
            prevBlock = endBlock
        }

        set.applyTo(binding.container)
        viewToBlock[view] = Block(instruction)
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun showCustomDialog() {
        val dialog = Dialog(this)
        //We have added a title in the custom layout. So let's disable the default title.
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true)
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.input_dialog)

        //Initializing the views of the dialog.
        val inputVal: EditText = dialog.findViewById(R.id.input)
        val submitButton: Button = dialog.findViewById(R.id.button)

        submitButton.setOnClickListener {
            interpreter.input = inputVal.text.toString()
            interpreter.waitingForInput = false
            dialog.dismiss()
            GlobalScope.launch {
                controller.resumeProgram()
            }
        }
        dialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun update(p0: Observable?, p1: Any?) {
        val lexerErrors = controller.popLexerErrors()
        val runtimeErrors = controller.popRuntimeErrors()
        val output = interpreter.output

        runOnUiThread {
            if (runtimeErrors.isNotEmpty()) {
                showErrorDialog("RUNTIME ERROR", runtimeErrors)
                return@runOnUiThread
            }
            if (lexerErrors.isNotEmpty()) {
                showErrorDialog("LEXER ERROR", lexerErrors)
                return@runOnUiThread
            }

            if (interpreter.waitingForInput) {
                showCustomDialog()
                return@runOnUiThread
            }
            if (output.isNotEmpty()) {
                bottomSheetConsole.show()
                bindingSheetConsole.console.text = output
            }

            if (interpreter.currentLine + 1 != interpreter.blocks?.size) {
                val block = interpreter.blocks?.get(interpreter.currentLine + 1)
                val breakpoint = block?.breakpoint

                if (debugMode && (debugType == Debug.NEXT ||
                            debugType == Debug.BREAKPOINT && breakpoint == true)) {
                    val button = getDebuggerView()?.findViewById<ImageButton>(R.id.breakpoint)

                    button?.setBackgroundResource(when (debugType) {
                        Debug.NEXT -> android.R.drawable.presence_away
                        Debug.BREAKPOINT -> android.R.drawable.presence_busy
                    })
                    return@runOnUiThread
                }

                GlobalScope.launch {
                        controller.resumeProgram()
                }
            }
        }
    }
}
