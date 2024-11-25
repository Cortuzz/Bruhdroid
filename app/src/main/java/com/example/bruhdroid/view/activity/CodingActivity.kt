package com.example.bruhdroid.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.controller.Controller
import com.example.bruhdroid.R
import com.example.bruhdroid.databinding.*
import com.example.bruhdroid.model.interpreter.Interpreter
import com.example.bruhdroid.model.blocks.instruction.*
import com.example.bruhdroid.model.blocks.instruction.condition.ConditionInstruction
import com.example.bruhdroid.model.blocks.instruction.condition.IfInstruction
import com.example.bruhdroid.view.category.CategoryHelper
import com.example.bruhdroid.view.instruction.InstructionHelper
import com.example.bruhdroid.view.instruction.InstructionView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates


class CodingActivity : AppCompatActivity(), Observer {
    private enum class Debug {
        NEXT, BREAKPOINT
    }

    private var viewToBlock = mutableMapOf<View, Instruction>()
    private var viewInstructions = mutableListOf<InstructionView>()
    private var binViewList = LinkedList<InstructionView>()
    private var connectorsMap = mutableMapOf<View, View>()
    private var prevBlock: View? = null
    private var prevBlockInBin: View? = null
    private var debugMode = false

    private lateinit var debugType: Debug
    private lateinit var currentDrag: View

    private lateinit var binding: ActivityCodingBinding
    private lateinit var bindingSheetBin: BottomsheetBinBinding
    private lateinit var bindingSheetConsole: BottomsheetConsoleBinding

    private lateinit var bottomSheetBin: BottomSheetDialog
    private lateinit var bottomSheetConsole: BottomSheetDialog
    private lateinit var instructionHelper: InstructionHelper
    private lateinit var categoryHelper: CategoryHelper
    private var dp by Delegates.notNull<Float>()

    private val interpreter = Interpreter()
    private val controller = Controller()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dp = this.resources.displayMetrics.density

        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)
        instructionHelper = InstructionHelper(layoutInflater)
        categoryHelper = CategoryHelper(this, layoutInflater, dp)

        categoryHelper.updateCategories(instructionHelper.getInstructionViews())

        for (instructionView in categoryHelper.getInstructionViews()) {
            instructionView.view.setOnClickListener {
                val newView = instructionView.clone()
                newView.updateBlockView(this)
                buildBlock(
                    prevBlock, newView,
                    newView.instruction.isStartInstruction()
                )
            }
        }

        bindingSheetConsole =
            DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_console, null, false)
        bottomSheetConsole = BottomSheetDialog(this@CodingActivity)
        bottomSheetConsole.setContentView(bindingSheetConsole.root)

        bindingSheetBin =
            DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_bin, null, false)
        bottomSheetBin = BottomSheetDialog(this@CodingActivity)
        bottomSheetBin.setContentView(bindingSheetBin.root)

        val blocks = intent.getSerializableExtra("blocks")
        val filename = intent.getSerializableExtra("filename")
        if (blocks is Array<*>) {
            parseBlocks(blocks)
        }

        controller.addObserver(this)
        interpreter.addObserver(this)

        binding.mainPanel.setOnDragListener { v, event ->
            generateDropAreaForScroll(v, event, 30)
        }
        binding.buttonsPanel.setOnDragListener { v, event ->
            generateDropAreaForScroll(v, event, -30)
        }

        binding.changeThemeButton.setOnClickListener {
            Controller().changeTheme(resources.configuration.uiMode)
        }
        binding.menuButton.setOnClickListener {
            categoryHelper.show()
        }
        binding.binButton.setOnClickListener {
            bottomSheetBin.show()
        }
        binding.binButton.setOnDragListener { v, event ->
            generateDropAreaForBin(v, event)
        }
        binding.consoleButton.setOnClickListener {
            bottomSheetConsole.show()
        }
        binding.launchButton.setOnClickListener {
            debugMode = false
            bindingSheetConsole.console.text = ""

            controller.runProgram(interpreter, viewInstructions, debugMode)
        }
        binding.saveButton.setOnClickListener {
            if (filename is String) {
                saveProgram(filename)
                return@setOnClickListener
            }
            showSaveDialog()
        }

        binding.debugButton.setOnClickListener {
            debugMode = true
            debugType = Debug.BREAKPOINT
            bindingSheetConsole.console.text = ""
            binding.debugPanel.visibility = View.VISIBLE
            binding.mainPanel.visibility = View.INVISIBLE

            controller.runProgram(interpreter, viewInstructions, debugMode)
        }

        binding.nextButton.setOnClickListener {
            debugType = Debug.NEXT
            updateDebugger()

            GlobalScope.launch {
                controller.resumeOneIteration()
            }
        }

        binding.resumeButton.setOnClickListener {
            debugType = Debug.BREAKPOINT
            updateDebugger()

            GlobalScope.launch {
                controller.resumeOneIteration()
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
    }

    private fun updateDebugger() {
        val view = getViewByLine()

        getViewInstructionByView(view!!).drawBreakpoint()
    }

    private fun getDebuggerView(): View? {
        val view = getViewByLine()

        if (view != null)
            return view

        runOnUiThread {
            binding.debugPanel.visibility = View.INVISIBLE
            binding.mainPanel.visibility = View.VISIBLE
        }

        return null
    }

    private fun getViewByLine(): View? {
        return try {
            viewInstructions.first { interpreter.getBlockAtCurrentLine() == it.instruction }.view
        } catch (e: Exception) {
            null
        }
    }

    private fun getDpMetric(value: Float): Int {
        return (value * dp).toInt()
    }

    private fun setupConnector(view: View) {
        val connector = layoutInflater.inflate(R.layout.block_connector, null)
        connector.id = View.generateViewId()
        connectorsMap[view] = connector
        runOnUiThread {
            binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
        }
    }

    private fun setBlockOnDragListener(view: View) {
        view.setOnDragListener { v, event ->
            generateDropArea(v, event)
        }
    }

    private fun drawBlock(view: View) {
        if (viewToBlock[view]!!.isEndInstruction()) {
            binding.container.addView(view)
            return
        }

        generateDragArea(view)
        binding.container.addView(
            view,
            ConstraintLayout.LayoutParams(
                getDpMetric(getViewInstructionByView(view).blockWidth),
                getDpMetric(getViewInstructionByView(view).blockHeight),
            )
        )
    }

    private fun parseBlock(instruction: Instruction) {
        val viewBlock = instructionHelper.getViewByInstruction(instruction)!!
        val view = viewBlock.updateBlockView(this)

        view.id = View.generateViewId()
        view.findViewById<EditText>(R.id.expression)?.setText(instruction.expression)
        viewBlock.generateBreakpoint()

        viewInstructions.add(viewBlock)
        prevBlock = view
        viewToBlock[view] = instruction

        if (viewInstructions.size != 1)
            setupConnector(view)

        setBlockOnDragListener(view)

        runOnUiThread {
            drawBlock(view)
        }
    }

    @SuppressLint("InflateParams")
    @OptIn(DelicateCoroutinesApi::class)
    private fun parseBlocks(instructions: Array<*>) {
        GlobalScope.launch {
            for (instruction in instructions) {
                parseBlock(instruction as Instruction)
            }

            delay(500)

            runOnUiThread {
                buildConstraints(binding.container, viewInstructions)
            }
        }
    }

    override fun onBackPressed() {
        if (!Controller.suppressingWarns) {
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

    private fun generateDropAreaForScroll(v: View, event: DragEvent, speed: Int): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                binding.mainCode.panBy(0.0F, speed.toFloat(), false)
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

            DragEvent.ACTION_DROP -> {
                val instructionView = getViewInstructionByView(currentDrag)
                val index = -1
                val instr = viewToBlock[currentDrag]!!

                if (instr.isStartInstruction()) {
                    addBlocksToBin(currentDrag, true)
                    reBuildBlocks(index, instructionView, true)
                } else {
                    addBlocksToBin(currentDrag)
                    reBuildBlocks(index, instructionView)
                }

                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                GlobalScope.launch {
                    runOnUiThread {
                        if (viewInstructions.firstOrNull { vi -> vi.view == currentDrag } != null) {
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
        val set = ConstraintSet()
        set.clone(binding.container)

        GlobalScope.launch {
            for (block in addedBlocks) {
                if (connectorsMap[block.view] != null) {
                    runOnUiThread {
                        bindingSheetBin.deletedList.addView(connectorsMap[block.view])
                    }
                }

                runOnUiThread {
                    bindingSheetBin.deletedList.addView(block.view)
                }
                binViewList.add(getViewInstructionByView(block.view))
                prevBlockInBin = block.view
            }
            delay(100)

            runOnUiThread {
                buildConstraints(bindingSheetBin.deletedList, binViewList)
            }
        }

        makeBlocksVisible(view)
    }

    private fun onClickBinBlock(instructionViews: List<InstructionView>) {
        for (instructionView in instructionViews) {
            val view = instructionView.view

            bindingSheetBin.deletedList.removeView(view)
            if (connectorsMap[view] != null) {
                bindingSheetBin.deletedList.removeView(connectorsMap[view])
            }
            binViewList.remove(getViewInstructionByViewInBin(view))

            binding.container.addView(view)
            if (connectorsMap[view] != null) {
                binding.container.addView(connectorsMap[view])
            } else if (viewInstructions.isNotEmpty()) {
                setupConnector(view)
            }

            view.bringToFront()
            viewInstructions.add(instructionView)
            if (!viewToBlock[view]!!.isEndInstruction()) {
                generateDragArea(view)
            }
        }

        buildConstraints(bindingSheetBin.deletedList, binViewList)
        buildConstraints(binding.container, viewInstructions)

        prevBlock = viewInstructions.last().view
    }

    private fun removeLastConnectedView(index: Int, view: View) {
        if (viewInstructions.indexOf(getViewInstructionByView(view)) != 0)
            return

        if (index > viewInstructions.lastIndex)
            return

        binding.container.removeView(connectorsMap[viewInstructions[index].view])
        connectorsMap.remove(viewInstructions[index].view)
    }

    @SuppressLint("InflateParams")
    private fun removeBlocksFromParent(view: View, isConnected: Boolean = false): List<InstructionView> {
        val tempList = mutableListOf<InstructionView>()
        view.setOnLongClickListener(null)
        tempList.add(getViewInstructionByView(view))
        binding.container.removeView(view)

        if (connectorsMap[view] != null) {
            binding.container.removeView(connectorsMap[view])
            connectorsMap.remove(view)
        }

        val viewInstruction = getViewInstructionByView(view)
        var index = viewInstructions.indexOf(viewInstruction) + 1

        if (isConnected) {
            var count = 1

            do {
                val currViewInstr = viewInstructions[index]
                val currView = currViewInstr.view

                currView.setOnLongClickListener(null)
                tempList.add(currViewInstr)
                binding.container.removeView(currView)
                binding.container.removeView(connectorsMap[currView])

                val block = viewToBlock[currView]
                if (block!!.isEndInstruction()) {
                    count--
                } else if (block.isStartInstruction()) {
                    count++
                }
                index++
            } while (count > 0)
        }

        removeLastConnectedView(index, view)

        view.setOnClickListener {
            onClickBinBlock(tempList)
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

            DragEvent.ACTION_DROP -> {
                if (currentDrag === receiverView) {
                    return false
                }
                var newIndex = viewInstructions.indexOf(getViewInstructionByView(receiverView))

                newIndex = when (newIndex > viewInstructions.indexOf(getViewInstructionByView(currentDrag))) {
                    true -> newIndex - 1
                    false -> newIndex
                }
                newIndex = when (event.y < receiverView.height / 2) {
                    true -> newIndex
                    else -> newIndex + 1
                }

                val instructionView = getViewInstructionByView(currentDrag)
                val instr = viewToBlock[currentDrag]!!
                if (instr.isStartInstruction()) {
                    reBuildBlocks(newIndex, instructionView, true)
                } else {
                    reBuildBlocks(newIndex, instructionView)
                }

                v.invalidate()
                true
            }

            else -> {
                false
            }
        }
    }

    private fun replaceUntilEnd(indexFrom: Int, indexTo: Int) {
        val tempViews = mutableListOf<InstructionView>()
        var count = 0

        do {
            val instr = viewInstructions[indexFrom].instruction
            if (instr.isStartInstruction())
                ++count
            if (instr.isEndInstruction())
                --count

            val view = viewInstructions.removeAt(indexFrom)
            tempViews.add(view)
        } while (count > 0)

        if (indexTo != -1) {
            if (indexFrom == 0 || indexTo == 0) {
                when (indexFrom) {
                    0 -> {
                        val connector = connectorsMap[viewInstructions[0].view]
                        connectorsMap[tempViews[0].view] = connector as View
                    }
                    else -> {
                        val connector = connectorsMap[tempViews[0].view]
                        connectorsMap[viewInstructions[0].view] = connector as View
                    }
                }
            }
            if (indexTo <= indexFrom) {
                viewInstructions.addAll(indexTo, tempViews)
            } else {
                viewInstructions.addAll(indexTo + 1 - tempViews.size, tempViews)
            }
        }
    }

    private fun clearConstraints(set: ConstraintSet, view: InstructionView) {
        val id = view.view.id

        set.clear(id, ConstraintSet.TOP)
        set.clear(id, ConstraintSet.BOTTOM)
        set.clear(id, ConstraintSet.LEFT)
        set.clear(id, ConstraintSet.RIGHT)

        val connector = connectorsMap[view.view]?.id
        if (connector != null) {
            set.clear(connector, ConstraintSet.TOP)
            set.clear(connector, ConstraintSet.BOTTOM)
            set.clear(connector, ConstraintSet.LEFT)
            set.clear(connector, ConstraintSet.RIGHT)
        }
    }

    private fun buildConstraints(container: ConstraintLayout, viewList: List<InstructionView>) {
        val set = ConstraintSet()
        set.clone(container)
        for (view in viewList) {
            if (container == binding.container) {
                view.view.setOnClickListener(null)
            }
            clearConstraints(set, view)
        }

        val nestViews = mutableListOf<View>()
        val nestCount = mutableListOf<Int>()

        for (i in 1 until viewList.size) {
            val view = viewList[i]
            val prevView = viewList[i - 1]

            val prevInstr = viewToBlock[prevView.view]!!
            val currInstr = viewToBlock[view.view]!!

            if (prevInstr.isStartInstruction() || prevInstr.isMiddleInstruction()) {
                nestViews.add(prevView.view)
                nestCount.add(prevView.view.height)
            }

            if (currInstr.isMiddleInstruction() || currInstr.isEndInstruction()) {
                val nest = nestViews.removeLast()
                val nestId = nest.id
                val connector = connectorsMap[view.view]!!
                val connectorId = connector.id
                val ratio = (nestCount.removeLast()) / (connector.height).toFloat()
                set.setScaleY(connectorId, ratio)

                nest.bringToFront()
                set.connect(connectorId, ConstraintSet.TOP, nestId, ConstraintSet.BOTTOM, -15)
                set.connect(connectorId, ConstraintSet.BOTTOM, view.view.id, ConstraintSet.TOP, 0)
                set.connect(connectorId, ConstraintSet.LEFT, nestId, ConstraintSet.LEFT, 80)
            } else if (connectorsMap[view.view] != null) {
                prevView.view.bringToFront()
                set.connect(
                    connectorsMap[view.view]!!.id,
                    ConstraintSet.TOP,
                    prevView.view.id,
                    ConstraintSet.BOTTOM,
                    -15
                )
                set.connect(
                    connectorsMap[view.view]!!.id,
                    ConstraintSet.BOTTOM,
                    view.view.id,
                    ConstraintSet.TOP,
                    0
                )
                set.connect(
                    connectorsMap[view.view]!!.id,
                    ConstraintSet.LEFT,
                    view.view.id,
                    ConstraintSet.LEFT,
                    80
                )
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind] += view.view.height - 15 }
                set.connect(
                    view.view.id,
                    ConstraintSet.LEFT,
                    nestViews.last().id,
                    ConstraintSet.LEFT,
                    80
                )
            }
            set.connect(view.view.id, ConstraintSet.TOP, prevView.view.id, ConstraintSet.BOTTOM, -15)
        }
        set.applyTo(container)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun reBuildBlocks(index: Int, drag: InstructionView, untilEnd: Boolean = false) {
        GlobalScope.launch {
            if (untilEnd) {
                replaceUntilEnd(viewInstructions.indexOf(drag), index)
            } else {
                if (index != -1 && ((viewInstructions.indexOf(drag)) == 0 || index == 0)) {
                    val connector = when {
                        (viewInstructions.indexOf(drag)) == 0 -> connectorsMap[viewInstructions[1].view]
                        index == 0 -> connectorsMap[drag.view]
                        else -> {
                            throw Exception()
                        }
                    }
                    connectorsMap[viewInstructions[0].view] = connector as View
                }

                viewInstructions.remove(getViewInstructionByView(drag.view))
                when {
                    index == -1 -> {}
                    index > viewInstructions.lastIndex -> {
                        viewInstructions.add(drag)
                    }
                    else -> {
                        viewInstructions.add(index, drag)
                    }
                }
            }

            if (viewInstructions.isNotEmpty()) {
                connectorsMap.remove(viewInstructions[0].view)
            }

            runOnUiThread {
                buildConstraints(binding.container, viewInstructions)
            }

            prevBlock = if (viewInstructions.isEmpty()) {
                null
            } else {
                viewInstructions.last().view
            }
        }
    }

    private fun makeBlocksInvisible(v: View) {
        v.visibility = View.INVISIBLE

        if (!viewToBlock[v]!!.isStartInstruction())
            return


        var index = viewInstructions.indexOf(getViewInstructionByView(v)) + 1
        var count = 1
        val ifList = mutableListOf<View>()
        if (viewToBlock[v]!! is IfInstruction) {
            ifList.add(v)
        }
        var connector: View? = null

        while (count != 0) {
            val currentView = viewInstructions[index].view
            currentView.visibility = View.INVISIBLE
            val block = viewToBlock[currentView]!!

            if (ifList.isNotEmpty()) {
                if (block is IfInstruction)
                    ifList.add(currentView)

                if (block is ConditionInstruction && block.isMiddleInstruction()) {
                    val checkIf = ifList.removeLast()
                    if (checkIf == v) {
                        connector = connectorsMap[currentView]
                    }
                }
            }

            if (connectorsMap[currentView] != null) {
                connectorsMap[currentView]!!.visibility = View.INVISIBLE
            }

            if (block.isEndInstruction()) {
                count--
            } else if (block.isStartInstruction()) {
                count++
            }
            index++
        }

        if ((viewInstructions.indexOf(getViewInstructionByView(v)) != 0) && (index <= viewInstructions.lastIndex)) {
            connectorsMap[viewInstructions[index - 1].view]!!.visibility = View.VISIBLE
            if (connector != null) {
                connector.visibility = View.VISIBLE
            }
        }
    }

    private fun makeBlocksVisible(v: View) {
        v.visibility = View.VISIBLE

        if (viewToBlock[v]!!.isStartInstruction()) {
            var index = viewInstructions.indexOf(getViewInstructionByView(v)) + 1
            var count = 1

            while (count != 0) {
                viewInstructions[index].view.visibility = View.VISIBLE
                val block = viewToBlock[viewInstructions[index].view]
                if (connectorsMap[viewInstructions[index].view] != null) {
                    connectorsMap[viewInstructions[index].view]!!.visibility = View.VISIBLE
                }

                if (block!!.isEndInstruction()) {
                    count--
                } else if (block.isStartInstruction()) {
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
            val dummyData = ClipData.newPlainText("dummyData", null)
            val shadowBuilder = DragShadowBuilder(it)
            it.startDragAndDrop(dummyData, shadowBuilder, it, 0)
            true
        }
    }

    @SuppressLint("InflateParams")
    @OptIn(DelicateCoroutinesApi::class)
    private fun addStatementBlock(endBlock: InstructionView, newBlock: InstructionView, full: Boolean) {
        newBlock.updateBlockView(this)
        newBlock.generateBreakpoint()

        val index = viewInstructions.indexOf(getViewInstructionByView(endBlock.view))
        val endBlockInstruction = viewInstructions[index]

        viewInstructions[index] = newBlock

        try {
            viewInstructions.add(index + 1, endBlockInstruction)
        } catch (e: Exception) {
            viewInstructions.add(endBlockInstruction)
        }

        viewToBlock[newBlock.view] = newBlock.instruction
        newBlock.view.id = View.generateViewId()

        setupConnector(newBlock.view)
        binding.container.addView(
            newBlock.view,
            getDpMetric(newBlock.blockWidth),
            getDpMetric(newBlock.blockHeight),
        )

        setBlockOnDragListener(newBlock.view)

        GlobalScope.launch {
            delay(100)
            runOnUiThread {
                buildConstraints(binding.container, viewInstructions)
            }
        }
    }

    private fun setupConnectorConstraints(
        set: ConstraintSet,
        currInstructionView: View,
        prevInstructionView: View,
        connector: View,
        connectorMargin: Int
    ): ConstraintSet {
        connectorsMap[currInstructionView] = connector

        set.connect(currInstructionView.id, ConstraintSet.TOP, prevInstructionView.id, ConstraintSet.BOTTOM, connectorMargin)
        set.connect(connector.id, ConstraintSet.TOP, prevInstructionView.id, ConstraintSet.BOTTOM, 0)
        set.connect(connector.id, ConstraintSet.BOTTOM, currInstructionView.id, ConstraintSet.TOP, 0)
        set.connect(connector.id, ConstraintSet.LEFT, currInstructionView.id, ConstraintSet.LEFT, 80)

        return set
    }

    private fun buildBlockConnectors(
        instructionView: InstructionView,
        endInstructionView: InstructionView?,
        prevView: View?,
        connector: View,
        nestedConnector: View?
    ) {
        if (prevView != null) {
            binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
            prevView.bringToFront()
        }

        binding.container.addView(
            instructionView.view,
            ConstraintLayout.LayoutParams(
                getDpMetric(instructionView.blockWidth),
                getDpMetric(instructionView.blockHeight),
            )
        )

        if (nestedConnector != null) {
            binding.container.addView(nestedConnector, ConstraintLayout.LayoutParams(5, 300))
            instructionView.view.bringToFront()
            binding.container.addView(endInstructionView!!.view)
        }

        var set = ConstraintSet()
        set.clone(binding.container)

        generateDragArea(instructionView.view)
        setBlockOnDragListener(instructionView.view)

        if (prevView != null)
            set = setupConnectorConstraints(set, instructionView.view, prevView, connector, -15)

        prevBlock = instructionView.view

        if (nestedConnector != null) {
            set = setupConnectorConstraints(set, endInstructionView!!.view, instructionView.view, nestedConnector, 10)
            prevBlock = endInstructionView.view
        }

        set.applyTo(binding.container)
        viewToBlock[instructionView.view] = instructionView.instruction
    }

    @SuppressLint("InflateParams")
    private fun buildBlock(
        prevView: View?,
        instructionView: InstructionView,
        connect: Boolean = false
    ) {
        var nestedConnector: View? = null
        val connector = layoutInflater.inflate(R.layout.block_connector, null)
        var endInstructionView: InstructionView? = null

        instructionView.view.id = View.generateViewId()
        connector.id = View.generateViewId()

        instructionView.generateBreakpoint()
        viewInstructions.add(instructionView)

        if (connect) {
            endInstructionView = instructionView.endInstructionView!!.clone()
            endInstructionView.updateBlockView(this)
            endInstructionView.generateBreakpoint()

            viewInstructions.add(endInstructionView)

            endInstructionView.tryGenerateEndStatementInstruction(instructionHelper)
            { endVi, newVi, full -> addStatementBlock(endVi, newVi, full) }

            nestedConnector = layoutInflater.inflate(R.layout.block_connector, null)

            viewToBlock[endInstructionView.view] = endInstructionView.instruction
            endInstructionView.view.id = View.generateViewId()
            nestedConnector.id = View.generateViewId()

            setBlockOnDragListener(endInstructionView.view)
        }

        buildBlockConnectors(instructionView, endInstructionView, prevView, connector, nestedConnector)
    }

    private fun buildAlertDialog(label: String?, message: String?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setMessage(message)
        builder.setTitle(label)
        return builder
    }

    private fun showErrorDialog(label: String, message: String) {
        val builder = buildAlertDialog(label, message)
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int -> }
        builder.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showCustomDialog() {

        val dialog = buildAlertDialog(null, null)
        val layoutView: View = layoutInflater.inflate(R.layout.input_dialog, null)
        dialog.setView(layoutView)

        val inputVal: EditText = layoutView.findViewById(R.id.input)
        val submitButton: Button = layoutView.findViewById(R.id.button)

        val alertDialog = dialog.create()
        alertDialog.show()

        submitButton.setOnClickListener {
            interpreter.handleUserInput(inputVal.text.toString())
            alertDialog.dismiss()
            if (!debugMode) {
                controller.resumeAllIterations()
            } else {
                GlobalScope.launch {
                    controller.resumeOneIteration()
                }
            }
        }
    }

    private fun showSaveDialog() {

        val dialog = buildAlertDialog(null, null)
        val layoutView: View = layoutInflater.inflate(R.layout.input_dialog, null)
        dialog.setView(layoutView)

        val title: TextView = layoutView.findViewById(R.id.textView)
        val inputVal: EditText = layoutView.findViewById(R.id.input)
        val submitButton: Button = layoutView.findViewById(R.id.button)

        title.text = getString(R.string.save_dialog_title)
        val alertDialog = dialog.create()
        alertDialog.show()

        submitButton.setOnClickListener {
            saveProgram(inputVal.text.toString())
            alertDialog.dismiss()

        }
    }

    private fun showMemoryInfo(info: String) {
        val text = bindingSheetConsole.console.text.toString() +
                "⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀\nDEBUGGER:\n" + info + "" +
                "\n⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀"
        bindingSheetConsole.console.text = text
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun update(p0: Observable?, p1: Any?) {
        val internalErrors = controller.popInternalErrors()
        val runtimeErrors = controller.popRuntimeErrors()
        val output = interpreter.output

        if (runtimeErrors.isNotEmpty()) {
            runOnUiThread { showErrorDialog("RUNTIME ERROR", runtimeErrors) }
            return
        }
        if (internalErrors.isNotEmpty()) {
            runOnUiThread { showErrorDialog("INTERNAL ERROR", internalErrors) }
            return
        }

        if (interpreter.waitingForInput) {
            runOnUiThread { showCustomDialog() }
            return
        }
        if (output.isNotEmpty()) {
            runOnUiThread {
                bindingSheetConsole.console.text = output
            }
        }

        if (!debugMode) {
            return
        }

        if (interpreter.getLine() != interpreter.getBlocksSize()) {
            val block = interpreter.getBlockAtCurrentLine()
            val breakpoint = block?.breakpoint

            if ((debugType == Debug.NEXT ||
                        debugType == Debug.BREAKPOINT && breakpoint == true)
            ) {
                val button = getDebuggerView()?.findViewById<ImageButton>(R.id.breakpoint)

                runOnUiThread {
                    showMemoryInfo(interpreter.getMemoryData())
                    button?.setBackgroundResource(
                        when (debugType) {
                            Debug.NEXT -> android.R.drawable.presence_away
                            Debug.BREAKPOINT -> android.R.drawable.presence_busy
                        }
                    )
                }
                return
            }

            GlobalScope.launch {
                runOnUiThread {
                    controller.resumeOneIteration()
                }
            }
        }
    }

    private fun getViewInstructionByView(view: View): InstructionView {
        for (vi in viewInstructions) {
            if (vi.view == view)
                return vi
        }
        throw NotFoundException("Not found")
    }

    private fun getViewInstructionByViewInBin(view: View): InstructionView {
        for (vi in binViewList) {
            if (vi.view == view)
                return vi
        }
        throw NotFoundException("Not found")
    }

    private fun saveProgram(text: String) {
        val saveResult = Controller.saveProgram(text, this.filesDir, viewInstructions)
        Toast.makeText(this,
            if (saveResult) "Save successful" else "Save failed",
            Toast.LENGTH_SHORT).show()
    }
}
