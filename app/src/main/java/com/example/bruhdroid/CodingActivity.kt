package com.example.bruhdroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bruhdroid.databinding.*
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
    private lateinit var bindingSheetMemory: BottomsheetMemoryBinding
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var bottomSheetMenu: BottomSheetDialog
    private lateinit var bottomSheetBin: BottomSheetDialog
    private lateinit var bottomSheetConsole: BottomSheetDialog
    private lateinit var bottomSheetMemory: BottomSheetDialog

    private val interpreter = Interpreter()
    private val controller = Controller()
    private val startConnectingInstructions = listOf(Instruction.WHILE, Instruction.IF, Instruction.FUNC)
    private val connectingInstructions = listOf(Instruction.END, Instruction.END_WHILE, Instruction.FUNC_END)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)

        setBindingSheetBlocks()

        bindingSheetConsole = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_console, null, false)
        bottomSheetConsole = BottomSheetDialog(this@CodingActivity)
        bottomSheetConsole.setContentView(bindingSheetConsole.root)

        bindingSheetMemory = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_memory, null, false)
        bottomSheetMemory = BottomSheetDialog(this@CodingActivity)
        bottomSheetMemory.setContentView(bindingSheetMemory.root)

        bindingSheetBin = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_bin, null, false)
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
            generateDropAreaForScroll(v, event,30)
        }
        binding.buttonsPanel.setOnDragListener { v, event ->
            generateDropAreaForScroll(v, event,-30)
        }

        binding.menuButton.setOnClickListener {
            bottomSheetMenu.show()
        }
        binding.memoryButton.setOnClickListener {
            bottomSheetMemory.show()
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

            controller.runProgram(interpreter, viewToBlock, codingViewList, debugMode)
        }
        binding.saveButton.setOnClickListener {
            if (filename is String) {
                if (Controller.saveProgram(filename, this.filesDir, viewToBlock, codingViewList)) {
                    Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
                }
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

            controller.runProgram(interpreter, viewToBlock, codingViewList, debugMode)
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
        categoryList.add(Category(3, "Conditions | "))
        categoryList.add(Category(4, "Functions"))

        categoryRecycler(categoryList)

        val blockInit = layoutInflater.inflate(R.layout.block_init, null)
        val blockSet = layoutInflater.inflate(R.layout.block_set, null)
        val blockPragma = layoutInflater.inflate(R.layout.pragma_block, null)
        val blockInput = layoutInflater.inflate(R.layout.block_input, null)
        val blockPrint = layoutInflater.inflate(R.layout.block_print, null)
        val blockWhile = layoutInflater.inflate(R.layout.block_while, null)
        val blockBreak = layoutInflater.inflate(R.layout.block_break, null)
        val blockContinue = layoutInflater.inflate(R.layout.block_continue, null)
        val blockIf = layoutInflater.inflate(R.layout.block_if, null)

        val blockReturn = layoutInflater.inflate(R.layout.block_return, null)
        val blockFunc = layoutInflater.inflate(R.layout.block_func, null)
        val blockFuncCall = layoutInflater.inflate(R.layout.block_func_call, null)

        blockInit.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_init, Instruction.INIT, false)
        }
        blockSet.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_set, Instruction.SET, false)
        }
        blockPragma.setOnClickListener {
            buildBlock(prevBlock, R.layout.pragma_block, Instruction.PRAGMA, false)
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
        blockBreak.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_break, Instruction.BREAK, false)
        }
        blockContinue.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_continue, Instruction.CONTINUE, false)
        }
        blockIf.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_if, Instruction.IF, true)
        }
        blockReturn.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_return, Instruction.RETURN, false)
        }
        blockFunc.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_func, Instruction.FUNC, true)
        }
        blockFuncCall.setOnClickListener {
            buildBlock(prevBlock, R.layout.block_func_call, Instruction.FUNC_CALL, false)
        }

        val firstCategory = LinkedList<View>()
        val secondCategory = LinkedList<View>()
        val thirdCategory = LinkedList<View>()
        val fourthCategory = LinkedList<View>()
        val fifthCategory = LinkedList<View>()

        firstCategory.add(blockInit)
        firstCategory.add(blockSet)

        secondCategory.add(blockPragma)
        secondCategory.add(blockInput)
        secondCategory.add(blockPrint)

        thirdCategory.add(blockWhile)
        thirdCategory.add(blockBreak)
        thirdCategory.add(blockContinue)

        fourthCategory.add(blockIf)

        fifthCategory.add(blockReturn)
        fifthCategory.add(blockFunc)
        fifthCategory.add(blockFuncCall)

        categoryBlocks.add(firstCategory)
        categoryBlocks.add(secondCategory)
        categoryBlocks.add(thirdCategory)
        categoryBlocks.add(fourthCategory)
        categoryBlocks.add(fifthCategory)

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
            Instruction.PRAGMA to R.layout.pragma_block,
            Instruction.PRINT to R.layout.block_print,
            Instruction.INPUT to R.layout.block_input,
            Instruction.INIT to R.layout.block_init,
            Instruction.WHILE to R.layout.block_while,
            Instruction.IF to R.layout.block_if,
            Instruction.SET to R.layout.block_set,
            Instruction.END_WHILE to R.layout.empty_block,
            Instruction.END to R.layout.condition_block_end,
            Instruction.ELSE to R.layout.block_else,
            Instruction.BREAK to R.layout.block_break,
            Instruction.CONTINUE to R.layout.block_continue,
            Instruction.ELIF to R.layout.block_elif
        )

        GlobalScope.launch {
            for (block in blocks) {
                block as Block
                val instr = block.instruction
                val view = layoutInflater.inflate(layoutMap[instr]!!, null)
                view.id = View.generateViewId()
                generateBreakpoint(view)
                view.findViewById<EditText>(R.id.expression)?.setText(block.expression)

                codingViewList.add(view)
                prevBlock = view
                viewToBlock[view] = Block(instr, "")

                if (codingViewList.size != 1) {
                    val connector = layoutInflater.inflate(R.layout.block_connector, null)
                    connector.id = View.generateViewId()
                    connectorsMap[view] = connector
                    runOnUiThread {
                        binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
                    }
                }

                if (viewToBlock[view]!!.instruction !in connectingInstructions){
                    generateDragArea(view)
                    runOnUiThread {
                        binding.container.addView(view, ConstraintLayout.LayoutParams(900, 300))
                    }
                } else {
                    runOnUiThread {
                        binding.container.addView(view)
                    }
                }

                view.setOnDragListener { v, event ->
                    generateDropArea(v, event)
                }
            }

            delay(500)

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

    private fun generateDropAreaForScroll(v: View, event: DragEvent,speed: Int): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> {
                println("FUCK")
                binding.mainCode.panBy(0.0F,speed.toFloat(),false)
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
                val index = -1
                val instr = viewToBlock[currentDrag]!!.instruction

                if (instr in startConnectingInstructions) {
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
        val set = ConstraintSet()
        set.clone(binding.container)

        GlobalScope.launch {
            for (block in addedBlocks) {
                if (connectorsMap[block] != null) {
                    runOnUiThread {
                        bindingSheetBin.deletedList.addView(connectorsMap[block])
                    }
                }

                runOnUiThread {
                    bindingSheetBin.deletedList.addView(block)
                }
                binViewList.add(block)
                prevBlockInBin = block
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
        view.setOnLongClickListener(null)
        tempList.add(view)
        binding.container.removeView(view)
        if (connectorsMap[view] != null) {
            binding.container.removeView(connectorsMap[view])
            connectorsMap.remove(view)
        }

        var index = codingViewList.indexOf(view) + 1

        if (isConnected) {
            var count = 1

            do {
                val currView = codingViewList[index]
                currView.setOnLongClickListener(null)
                tempList.add(currView)
                binding.container.removeView(currView)
                binding.container.removeView(connectorsMap[currView])

                val block = viewToBlock[currView]
                if (block!!.instruction in connectingInstructions) {
                    count--
                } else if (block.instruction in startConnectingInstructions) {
                    count++
                }
                index++
            } while (count > 0)
        }

        if (codingViewList.indexOf(view) == 0 && index <= codingViewList.lastIndex) {
            binding.container.removeView(connectorsMap[codingViewList[index]])
            connectorsMap.remove(codingViewList[index])
        }

        view.setOnClickListener {
            for (view in tempList) {
                bindingSheetBin.deletedList.removeView(view)
                if (connectorsMap[view] != null) {
                    bindingSheetBin.deletedList.removeView(connectorsMap[view])
                }
                binViewList.remove(view)

                binding.container.addView(view)
                if (connectorsMap[view] != null) {
                    binding.container.addView(connectorsMap[view])
                } else if (!codingViewList.isEmpty()) {
                    val connector = layoutInflater.inflate(R.layout.block_connector, null)
                    connector.id = View.generateViewId()
                    binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
                    connectorsMap[view] = connector
                }

                view.bringToFront()
                codingViewList.add(view)
                generateDragArea(view)
            }
            buildConstraints(bindingSheetBin.deletedList, binViewList)
            buildConstraints(binding.container, codingViewList)

            prevBlock = codingViewList.last
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
                if (instr in  startConnectingInstructions) {
                    reBuildBlocks(newIndex, currentDrag, true)
                } else {
                    reBuildBlocks(newIndex, currentDrag)
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
        val tempViews = mutableListOf<View>()
        var count = 0

        do {
            when (viewToBlock[codingViewList[indexFrom]]!!.instruction) {
                in connectingInstructions -> --count
                in startConnectingInstructions -> ++count
                else -> {}
            }
            val view = codingViewList.removeAt(indexFrom)
            tempViews.add(view)
        } while (count > 0)

        if (indexTo != -1) {
            if (indexFrom == 0 || indexTo == 0) {
                when {
                    indexFrom == 0 -> {
                        val connector  = connectorsMap[codingViewList[0]]
                        connectorsMap[tempViews[0]] = connector as View
                    }
                    indexTo == 0 -> {
                        val connector = connectorsMap[tempViews[0]]
                        connectorsMap[codingViewList[0]] = connector as View
                    }
                    else -> {}
                }
            }
            if (indexTo <= indexFrom) {
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
        for (view in viewList) {
            if (container == binding.container) {
                view.setOnClickListener(null)
            }
            clearConstraints(set, view)
        }

        val endInstructions = listOf(Instruction.END, Instruction.END_WHILE, Instruction.ELSE, Instruction.ELIF, Instruction.FUNC_END)
        val startInstructions = listOf(Instruction.IF, Instruction.WHILE, Instruction.ELSE, Instruction.ELIF, Instruction.FUNC)
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
                Log.d("hh", viewToBlock[view]!!.instruction.toString() + " " + i.toString())
                val nest = nestViews.removeLast()
                val nestId = nest.id
                val connector = connectorsMap[view]!!
                val connectorId = connector.id
                val ratio = (nestCount.removeLast()) / (connector.height).toFloat()
                set.setScaleY(connectorId, ratio)

                nest.bringToFront()
                set.connect(connectorId, ConstraintSet.TOP, nestId, ConstraintSet.BOTTOM, -15)
                set.connect(connectorId, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connectorId, ConstraintSet.LEFT, nestId, ConstraintSet.LEFT, 80)
            } else if (connectorsMap[view] != null) {
                prevView.bringToFront()
                set.connect(connectorsMap[view]!!.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, -15)
                set.connect(connectorsMap[view]!!.id, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connectorsMap[view]!!.id, ConstraintSet.LEFT, view.id, ConstraintSet.LEFT, 80)
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind] += view.height -15}
                set.connect(view.id, ConstraintSet.LEFT, nestViews.last().id, ConstraintSet.LEFT, 80)
            }
            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, -15)
        }
        set.applyTo(container)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun reBuildBlocks(index: Int, drag: View, untilEnd: Boolean = false) {
        GlobalScope.launch {
            if (untilEnd) {
                replaceUntilEnd(codingViewList.indexOf(drag), index)
            } else {
                if (index != -1 && (codingViewList.indexOf(drag) == 0 || index == 0)) {
                    val connector = when {
                        codingViewList.indexOf(drag) == 0 -> connectorsMap[codingViewList[1]]
                        index == 0 -> connectorsMap[drag]
                        else -> {}
                    }
                    connectorsMap[codingViewList[0]] = connector as View
                }

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

            if (!codingViewList.isEmpty()) {
                connectorsMap.remove(codingViewList[0])
            }

            runOnUiThread {
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

        if (viewToBlock[v]!!.instruction in startConnectingInstructions) {

            var index = codingViewList.indexOf(v) + 1
            var count = 1
            var ifCount = -1
            if (viewToBlock[v]!!.instruction == Instruction.IF) {
                ifCount = 1
            }
            var elseConnector: View? = null

            while (count != 0) {
                val currentView = codingViewList[index]
                currentView.visibility = View.INVISIBLE
                val block = viewToBlock[currentView]

                if (ifCount != -1) {
                    when (block!!.instruction) {
                        Instruction.IF -> {
                            ifCount++
                        }
                        Instruction.ELSE -> {
                            ifCount--
                        }
                    }
                }

                if (ifCount == 0) {
                    ifCount = -1
                    elseConnector = connectorsMap[currentView]
                }

                if (connectorsMap[currentView] != null) {
                    connectorsMap[currentView]!!.visibility = View.INVISIBLE
                }

                if (block!!.instruction in connectingInstructions) {
                    count--
                } else if (block.instruction in startConnectingInstructions) {
                    count++
                }
                index++
            }

            if ((codingViewList.indexOf(v) != 0) && (index <= codingViewList.lastIndex)) {
                connectorsMap[codingViewList[index - 1]]!!.visibility = View.VISIBLE
                if (elseConnector != null) {
                    elseConnector.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun makeBlocksVisible(v: View) {
        v.visibility = View.VISIBLE

        if (viewToBlock[v]!!.instruction in startConnectingInstructions) {
            var index = codingViewList.indexOf(v) + 1
            var count = 1

            while (count != 0) {
                codingViewList[index].visibility = View.VISIBLE
                val block = viewToBlock[codingViewList[index]]
                if (connectorsMap[codingViewList[index]] != null) {
                    connectorsMap[codingViewList[index]]!!.visibility = View.VISIBLE
                }

                if (block!!.instruction in connectingInstructions) {
                    count--
                } else if (block.instruction in startConnectingInstructions) {
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

    private fun addStatementBlock(endBlock: View, instr: Instruction, blockId: Int, full: Boolean) {
        val elseView = layoutInflater.inflate(blockId, null)
        generateBreakpoint(elseView)
        val index = codingViewList.indexOf(endBlock)
        codingViewList[index] = elseView
        try {codingViewList.add(index + 1, endBlock)}
        catch (e: Exception) {codingViewList.add(endBlock)}

        viewToBlock[elseView] = Block(instr, "")
        elseView.id = View.generateViewId()

        val elseConnector = layoutInflater.inflate(R.layout.block_connector, null)
        connectorsMap[elseView] = elseConnector
        elseConnector.id = View.generateViewId()
        binding.container.addView(elseConnector, ConstraintLayout.LayoutParams(5, 300))
        if (full) {
            binding.container.addView(elseView, ConstraintLayout.LayoutParams(900, 300))
        } else {
            binding.container.addView(elseView)
        }

        elseView.setOnDragListener { v, event ->
            generateDropArea(v, event)
        }

        GlobalScope.launch {
            delay(100)
            runOnUiThread {
                buildConstraints(binding.container, codingViewList)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("InflateParams")
    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction, connect: Boolean = false) {
        val view = layoutInflater.inflate(layoutId, null)
        var endBlock: View? = null
        var nestedConnector: View? = null
        val connector = layoutInflater.inflate(R.layout.block_connector, null)
        view.id = View.generateViewId()
        connector.id = View.generateViewId()

        generateBreakpoint(view)
        codingViewList.add(view)

        if (connect) {
            val endInstruction: Instruction

            if (instruction == Instruction.WHILE) {
                endBlock = layoutInflater.inflate(R.layout.empty_block, null)
                endInstruction = Instruction.END_WHILE
            } else if (instruction == Instruction.FUNC) {
                endBlock = layoutInflater.inflate(R.layout.block_func_end, null)
                endInstruction = Instruction.FUNC_END
            } else {
                endBlock = layoutInflater.inflate(R.layout.condition_block_end, null)

                val addElse = endBlock.findViewById<Button>(R.id.addElseButton)
                val addElif = endBlock.findViewById<Button>(R.id.addElifButton)
                addElse.setOnClickListener {
                    addElif.visibility = View.INVISIBLE
                    addElse.visibility = View.INVISIBLE

                    addStatementBlock(endBlock, Instruction.ELSE, R.layout.block_else, false)
                }
                addElif.setOnClickListener {
                    addStatementBlock(endBlock, Instruction.ELIF, R.layout.block_elif, true)
                }
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
            binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
            prevView.bringToFront()
        }

        if (instruction == Instruction.BREAK || instruction == Instruction.CONTINUE) {
            binding.container.addView(view)
        } else {
            binding.container.addView(view, ConstraintLayout.LayoutParams(900, 300))
        }

        if (nestedConnector != null) {
            binding.container.addView(nestedConnector, ConstraintLayout.LayoutParams(5, 300))
            view.bringToFront()
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

            set.connect(connector.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 0)
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
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.input_dialog)

        val inputVal: EditText = dialog.findViewById(R.id.input)
        val submitButton: Button = dialog.findViewById(R.id.button)

        submitButton.setOnClickListener {
            interpreter.input = inputVal.text.toString()
            interpreter.waitingForInput = false
            dialog.dismiss()
            if (!debugMode) {
                controller.resumeFull()
            } else {
                GlobalScope.launch {
                    controller.resumeProgram()
                }
            }
        }
        dialog.show()
    }

    private fun showSaveDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.input_dialog)
        dialog.findViewById<TextView>(R.id.textView).text = "Program name:"

        val inputVal: EditText = dialog.findViewById(R.id.input)
        val submitButton: Button = dialog.findViewById(R.id.button)

        submitButton.setOnClickListener {
            if (Controller.saveProgram(inputVal.text.toString(), this.filesDir, viewToBlock, codingViewList)) {
                Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()

        }
        dialog.show()
    }

    private fun showMemoryInfo(info: String) {
        bindingSheetMemory.memory.text = info
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun update(p0: Observable?, p1: Any?) {
        val internalErrors = controller.popInternalErrors()
        val runtimeErrors = controller.popRuntimeErrors()
        val output = interpreter.output

        if (runtimeErrors.isNotEmpty()) {
            runOnUiThread {showErrorDialog("RUNTIME ERROR", runtimeErrors)}
            return
        }
        if (internalErrors.isNotEmpty()) {
            runOnUiThread {showErrorDialog("INTERNAL ERROR", internalErrors)}
            return
        }

        if (interpreter.waitingForInput) {
            runOnUiThread {showCustomDialog()}
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

        if (interpreter.currentLine + 1 != interpreter.blocks?.size) {
            val block = interpreter.blocks?.get(interpreter.currentLine + 1)
            val breakpoint = block?.breakpoint

            if ((debugType == Debug.NEXT ||
                        debugType == Debug.BREAKPOINT && breakpoint == true)) {
                val button = getDebuggerView()?.findViewById<ImageButton>(R.id.breakpoint)
                showMemoryInfo(interpreter.getMemoryData())

                runOnUiThread {
                    button?.setBackgroundResource(when (debugType) {
                        Debug.NEXT -> android.R.drawable.presence_away
                        Debug.BREAKPOINT -> android.R.drawable.presence_busy
                    })
                }
                return
            }

            GlobalScope.launch {
                runOnUiThread {
                    controller.resumeProgram()
                }
            }
        }
    }
}
