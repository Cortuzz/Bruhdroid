package com.example.bruhdroid.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bruhdroid.controller.Controller
import com.example.bruhdroid.R
import com.example.bruhdroid.databinding.*
import com.example.bruhdroid.view.category.Category
import com.example.bruhdroid.view.category.CategoryAdapter
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.view.ViewBlock
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.instruction.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates


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
    private var dp by Delegates.notNull<Float>()

    private var layoutMap = mapOf<Block, ViewBlock>()
    private val interpreter = Interpreter()
    private val controller = Controller()
    private val startConnectingInstructions = listOf(
        BlockInstruction.WHILE, BlockInstruction.IF,
        BlockInstruction.FUNC, BlockInstruction.FOR
    )
    private val connectingInstructions = listOf(
        BlockInstruction.END, BlockInstruction.END_WHILE,
        BlockInstruction.FUNC_END, BlockInstruction.END_FOR
    )


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dp = this.resources.displayMetrics.density
        layoutMap = mapOf(
            PragmaInstruction() to ViewBlock("Pragma", R.drawable.ic_block_pragma),
            PrintInstruction() to ViewBlock("Print", R.drawable.ic_block_print),
            InputInstruction() to ViewBlock("Input", R.drawable.ic_block_input),
            SetInstruction(initialization = true) to ViewBlock("Init", R.drawable.ic_block_init),
            WhileInstruction() to ViewBlock("While", R.drawable.ic_block_while),
            IfInstruction() to ViewBlock("If", R.drawable.ic_block_if),
            SetInstruction() to ViewBlock("Set", R.drawable.ic_block_set),
            BreakInstruction() to ViewBlock("Break", R.drawable.ic_block_break, false),
            ContinueInstruction() to ViewBlock("Continue", R.drawable.ic_block_continue, false),
            FuncInstruction() to ViewBlock("Method", R.drawable.ic_block_func),
            ReturnInstruction() to ViewBlock("Return", R.drawable.ic_block_return),
            CallInstruction() to ViewBlock("Call", R.drawable.ic_block_call),
            ForInstruction() to ViewBlock("For", R.drawable.ic_block_for)
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)

        setBindingSheetBlocks()

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
            bottomSheetMenu.show()
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

    @SuppressLint("InflateParams")
    private fun makeBlockView(viewBlock: ViewBlock): View {
        val block = when (viewBlock.hasText) {
            true -> layoutInflater.inflate(R.layout.block, null)
            false -> layoutInflater.inflate(R.layout.block_non_text, null)
        }

        block.findViewById<TextView>(R.id.textView).text = viewBlock.label
        block.background = ContextCompat.getDrawable(this, viewBlock.drawable)
        return block
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

        val firstBlockCategory = LinkedList<View>()
        val secondBlockCategory = LinkedList<View>()
        val thirdBlockCategory = LinkedList<View>()
        val fourthBlockCategory = LinkedList<View>()
        val fifthBlockCategory = LinkedList<View>()

        for (instr in layoutMap.keys) {
            val view = makeBlockView(copyBlock(instr)!!)

            view.setOnClickListener {
                buildBlock(
                    prevBlock, makeBlockView(copyBlock(instr)!!), instr,
                    instr.instruction in startConnectingInstructions
                )
            }
            when (instr.instruction) {
                in listOf(BlockInstruction.INIT, BlockInstruction.SET) -> firstBlockCategory.add(view)
                in listOf(
                    BlockInstruction.PRAGMA,
                    BlockInstruction.INPUT,
                    BlockInstruction.PRINT
                ) -> secondBlockCategory.add(view)
                in listOf(
                    BlockInstruction.FOR,
                    BlockInstruction.WHILE,
                    BlockInstruction.BREAK,
                    BlockInstruction.CONTINUE
                ) -> thirdBlockCategory.add(view)
                in listOf(BlockInstruction.IF) -> fourthBlockCategory.add(view)
                in listOf(
                    BlockInstruction.FUNC,
                    BlockInstruction.RETURN,
                    BlockInstruction.FUNC_CALL
                ) -> fifthBlockCategory.add(view)
                else -> {}
            }
        }

        categoryBlocks.add(firstBlockCategory)
        categoryBlocks.add(secondBlockCategory)
        categoryBlocks.add(thirdBlockCategory)
        categoryBlocks.add(fourthBlockCategory)
        categoryBlocks.add(fifthBlockCategory)

        onCategoryClick(0)
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
        val blockMenuParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (110 * dp).toInt()
        )

        for (view in categoryBlocks[position]) {
            bindingSheetMenu.blocks.addView(view, blockMenuParams)
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
            getKey(viewToBlock, interpreter.getBlockAtCurrentLine())
        } catch (e: Exception) {
            null
        }
    }

    private fun copyBlock(block: Block): ViewBlock? {
        for (bl in layoutMap.keys) {
            if (bl.instruction == block.instruction)
                return layoutMap[bl]!!
        }
        return null
    }

    @SuppressLint("InflateParams")
    @OptIn(DelicateCoroutinesApi::class)
    private fun parseBlocks(blocks: Array<*>) {
        val subsequentInstructionsViews = mapOf(
            BlockInstruction.END_WHILE to R.layout.empty_block,
            BlockInstruction.END to R.layout.condition_block_end,
            BlockInstruction.ELSE to R.layout.block_else,
            BlockInstruction.ELIF to R.layout.block_elif,
            BlockInstruction.FUNC_END to R.layout.block_func_end,
            BlockInstruction.END_FOR to R.layout.block_end_for
        )

        GlobalScope.launch {
            for (block in blocks) {
                block as Block

                val instructions = layoutMap.keys.map { b -> b.instruction }

                val view = if (block.instruction in instructions) {
                    makeBlockView(copyBlock(block)!!)
                } else {
                    layoutInflater.inflate(subsequentInstructionsViews[block.instruction]!!, null)
                }

                view.id = View.generateViewId()
                generateBreakpoint(view)
                view.findViewById<EditText>(R.id.expression)?.setText(block.expression)

                codingViewList.add(view)
                prevBlock = view
                viewToBlock[view] = block

                if (codingViewList.size != 1) {
                    val connector = layoutInflater.inflate(R.layout.block_connector, null)
                    connector.id = View.generateViewId()
                    connectorsMap[view] = connector
                    runOnUiThread {
                        binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
                    }
                }

                if (viewToBlock[view]!!.instruction !in connectingInstructions) {
                    generateDragArea(view)
                    runOnUiThread {
                        binding.container.addView(
                            view,
                            ConstraintLayout.LayoutParams((400 * dp).toInt(), (110 * dp).toInt())
                        )
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

    @SuppressLint("InflateParams")
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
            for (tempView in tempList) {
                bindingSheetBin.deletedList.removeView(tempView)
                if (connectorsMap[tempView] != null) {
                    bindingSheetBin.deletedList.removeView(connectorsMap[tempView])
                }
                binViewList.remove(tempView)

                binding.container.addView(tempView)
                if (connectorsMap[tempView] != null) {
                    binding.container.addView(connectorsMap[tempView])
                } else if (!codingViewList.isEmpty()) {
                    val connector = layoutInflater.inflate(R.layout.block_connector, null)
                    connector.id = View.generateViewId()
                    binding.container.addView(connector, ConstraintLayout.LayoutParams(5, 300))
                    connectorsMap[tempView] = connector
                }

                tempView.bringToFront()
                codingViewList.add(tempView)
                if (viewToBlock[tempView]!!.instruction !in connectingInstructions &&
                    viewToBlock[tempView]!!.instruction !in listOf(
                        BlockInstruction.ELSE,
                        BlockInstruction.ELIF
                    )
                ) {
                    generateDragArea(tempView)
                }
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
                if (instr in startConnectingInstructions) {
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
                        val connector = connectorsMap[codingViewList[0]]
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

        val endInstructions = listOf(
            BlockInstruction.END, BlockInstruction.END_WHILE, BlockInstruction.ELSE,
            BlockInstruction.ELIF, BlockInstruction.FUNC_END, BlockInstruction.END_FOR
        )
        val startInstructions = listOf(
            BlockInstruction.IF, BlockInstruction.WHILE, BlockInstruction.ELSE,
            BlockInstruction.ELIF, BlockInstruction.FUNC, BlockInstruction.FOR
        )
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

                nest.bringToFront()
                set.connect(connectorId, ConstraintSet.TOP, nestId, ConstraintSet.BOTTOM, -15)
                set.connect(connectorId, ConstraintSet.BOTTOM, view.id, ConstraintSet.TOP, 0)
                set.connect(connectorId, ConstraintSet.LEFT, nestId, ConstraintSet.LEFT, 80)
            } else if (connectorsMap[view] != null) {
                prevView.bringToFront()
                set.connect(
                    connectorsMap[view]!!.id,
                    ConstraintSet.TOP,
                    prevView.id,
                    ConstraintSet.BOTTOM,
                    -15
                )
                set.connect(
                    connectorsMap[view]!!.id,
                    ConstraintSet.BOTTOM,
                    view.id,
                    ConstraintSet.TOP,
                    0
                )
                set.connect(
                    connectorsMap[view]!!.id,
                    ConstraintSet.LEFT,
                    view.id,
                    ConstraintSet.LEFT,
                    80
                )
            }

            if (nestViews.isNotEmpty()) {
                nestCount.forEachIndexed { ind, _ -> nestCount[ind] += view.height - 15 }
                set.connect(
                    view.id,
                    ConstraintSet.LEFT,
                    nestViews.last().id,
                    ConstraintSet.LEFT,
                    80
                )
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
                        else -> {
                            throw Exception()
                        }
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
            val ifList = mutableListOf<View>()
            if (viewToBlock[v]!!.instruction == BlockInstruction.IF) {
                ifList.add(v)
            }
            var connector: View? = null

            while (count != 0) {
                val currentView = codingViewList[index]
                currentView.visibility = View.INVISIBLE
                val block = viewToBlock[currentView]

                if (ifList.isNotEmpty()) {
                    when (block!!.instruction) {
                        BlockInstruction.IF -> ifList.add(currentView)
                        in listOf(BlockInstruction.ELSE, BlockInstruction.ELIF) -> {
                            val checkIf = ifList.removeLast()
                            if (checkIf == v) {
                                connector = connectorsMap[currentView]
                            }
                        }
                        else -> {}
                    }
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
                if (connector != null) {
                    connector.visibility = View.VISIBLE
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

    @SuppressLint("InflateParams")
    @OptIn(DelicateCoroutinesApi::class)
    private fun addStatementBlock(endBlock: View, block: Block, blockId: Int, full: Boolean) {
        val elseView = layoutInflater.inflate(blockId, null)
        generateBreakpoint(elseView)
        val index = codingViewList.indexOf(endBlock)
        codingViewList[index] = elseView
        try {
            codingViewList.add(index + 1, endBlock)
        } catch (e: Exception) {
            codingViewList.add(endBlock)
        }

        viewToBlock[elseView] = block
        elseView.id = View.generateViewId()

        val elseConnector = layoutInflater.inflate(R.layout.block_connector, null)
        connectorsMap[elseView] = elseConnector
        elseConnector.id = View.generateViewId()
        binding.container.addView(elseConnector, ConstraintLayout.LayoutParams(5, 300))
        if (full) {
            binding.container.addView(
                elseView,
                ConstraintLayout.LayoutParams((400 * dp).toInt(), (110 * dp).toInt())
            )
        } else {
            binding.container.addView(
                elseView,
                ConstraintLayout.LayoutParams((200 * dp).toInt(), (70 * dp).toInt())
            )
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

    @SuppressLint("InflateParams")
    private fun buildBlock(
        prevView: View?,
        view: View,
        block: Block,
        connect: Boolean = false
    ) {
        var endBlock: View? = null
        var nestedConnector: View? = null
        val connector = layoutInflater.inflate(R.layout.block_connector, null)
        view.id = View.generateViewId()
        connector.id = View.generateViewId()

        generateBreakpoint(view)
        codingViewList.add(view)

        if (connect) {
            val endInstruction: Block

            when (block) {
                is WhileInstruction -> {
                    endBlock = layoutInflater.inflate(R.layout.empty_block, null)
                    endInstruction = EndWhileInstruction()
                }
                is ForInstruction -> {
                    endBlock = layoutInflater.inflate(R.layout.block_end_for, null)
                    endInstruction = EndForInstruction()
                }
                is FuncInstruction -> {
                    endBlock = layoutInflater.inflate(R.layout.block_func_end, null)
                    endInstruction = FuncEndInstruction()
                }
                else -> {
                    endBlock = layoutInflater.inflate(R.layout.condition_block_end, null)

                    val addElse = endBlock.findViewById<Button>(R.id.addElseButton)
                    val addElif = endBlock.findViewById<Button>(R.id.addElifButton)
                    addElse.setOnClickListener {
                        addElif.visibility = View.INVISIBLE
                        addElse.visibility = View.INVISIBLE

                        addStatementBlock(endBlock, ElseInstruction(), R.layout.block_else, false)
                    }
                    addElif.setOnClickListener {
                        addStatementBlock(endBlock, ElifInstruction(""), R.layout.block_elif, true)
                    }
                    endInstruction = EndInstruction()
                }
            }

            generateBreakpoint(endBlock)
            nestedConnector = layoutInflater.inflate(R.layout.block_connector, null)

            codingViewList.add(endBlock)
            viewToBlock[endBlock] = endInstruction
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

        if (block is BreakInstruction || block is ContinueInstruction) {
            binding.container.addView(
                view,
                ConstraintLayout.LayoutParams((200 * dp).toInt(), (80 * dp).toInt())
            )
        } else {
            binding.container.addView(
                view,
                ConstraintLayout.LayoutParams((400 * dp).toInt(), (110 * dp).toInt())
            )
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
        viewToBlock[view] = block
    }

    private fun buildAlertDialog(label: String?, message: String?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        if (message != null) {
            builder.setMessage(message)
        }
        if (label != null) {
            builder.setTitle(label)
        }
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
            if (Controller.saveProgram(
                    inputVal.text.toString(),
                    this.filesDir,
                    viewToBlock,
                    codingViewList
                )
            ) {
                Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
            }
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
}
