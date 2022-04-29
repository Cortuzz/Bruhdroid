package com.example.bruhdroid

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.ViewBindingAdapter.setOnLongClickListener
import com.example.bruhdroid.databinding.ActivityCodingBinding
import com.example.bruhdroid.databinding.BlockInitBinding
import com.example.bruhdroid.databinding.BottomsheetFragmentBinding
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.math.abs

class CodingActivity : AppCompatActivity(), Observer {
    private var currentDragIndex = 0
    private var viewToBlock = mutableMapOf<View, Block>()
    private var viewList = LinkedList<View>()
    private var prevBlock: View? = null

    private lateinit var binding : ActivityCodingBinding
    private lateinit var bindingSheet: BottomsheetFragmentBinding
    private lateinit var bindingBlock: BlockInitBinding
    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var currentDrag: View

    private val interpreter = Interpreter()
    private val controller = Controller()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_coding)
        bindingSheet = DataBindingUtil.inflate(layoutInflater, R.layout.bottomsheet_fragment, null, false)
        bindingBlock = DataBindingUtil.inflate(layoutInflater, R.layout.block_init, null, false)

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
            buildBlock(prevBlock, "Init", Instruction.INIT)
        }
        bindingSheet.blockPrint.setOnClickListener {
            buildBlock(prevBlock, "Print", Instruction.PRINT)
        }
        bindingSheet.blockInput.setOnClickListener {
            buildBlock(prevBlock, "Input", Instruction.INPUT)
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
                val item: ClipData.Item = event.clipData.getItemAt(0)

                if (currentDrag === receiverView) {
                    return false
                }
                var newIndex = viewList.indexOf(receiverView)
                viewList.remove(currentDrag)

                newIndex = when(event.y < receiverView.height / 2) {
                    true -> newIndex
                    else -> newIndex + 1
                }
                viewList.add(newIndex, currentDrag)

                binding.container.removeView(currentDrag)
                binding.container.addView(currentDrag, newIndex)

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

    private fun buildBlock(prevView: View?, layoutId: String, instruction: Instruction) {
        val view = layoutInflater.inflate(R.layout.block_init, null)
        view.findViewById<TextView>(R.id.textView).text = layoutId

        generateDragArea(view)
        view.setOnDragListener { v, event ->
            generateDropArea(v, event)
        }

        binding.container.addView(view)
        if (prevView == null) {
            viewList.add(view)
        } else {
            val prevIndex = viewList.indexOf(prevView)
            viewList.add(prevIndex, view)
        }
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
