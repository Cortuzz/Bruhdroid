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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityCodingBinding
import com.example.bruhdroid.databinding.BottomsheetFragmentBinding
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class CodingActivity : AppCompatActivity(), Observer {
    private var currentDragIndex = 0
    private var viewToBlock = mutableMapOf<View, Block>()
    private var viewList = mutableListOf<View>()
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

               reBuildBlocks(newIndex, currentDrag)
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

    private fun reBuildBlocks(index: Int, drag: View) {
        val set = ConstraintSet()
        set.clone(binding.container)
        for  (i in 0 until viewList.size) {
            set.clear(viewList[i].id, ConstraintSet.TOP)
            set.clear(viewList[i].id, ConstraintSet.BOTTOM)
        }

        viewList.remove(drag)
        if (index > viewList.lastIndex) {
            viewList.add(drag)
        } else {
            viewList.add(index, drag)
        }

        for  (i in 1 until viewList.size) {
            set.connect(viewList[i].id, ConstraintSet.TOP, viewList[i - 1].id, ConstraintSet.BOTTOM, 5)
        }
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

    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction) {
        val view = layoutInflater.inflate(layoutId, null)
        binding.container.addView(view)
        view.id = View.generateViewId()

        val set = ConstraintSet()
        set.clone(binding.container)

        generateDragArea(view)
        view.setOnDragListener { v, event ->
            generateDropArea(v, event)
        }

        viewList.add(view)
        if (prevView != null) {
            set.connect(view.id, ConstraintSet.TOP, prevView.id, ConstraintSet.BOTTOM, 5)
            set.applyTo(binding.container)
        }

        prevBlock = view
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
        val output = interpreter.popOutput()

        if (runtimeErrors.isNotEmpty()) {
            buildAlertDialog("RUNTIME ERROR", runtimeErrors)
        }

        if (lexerErrors.isNotEmpty()) {
            buildAlertDialog("LEXER ERROR", lexerErrors)
        }
        if (output.isNotEmpty()) {
            binding.console.append(output + "\n")
        }
    }
}
