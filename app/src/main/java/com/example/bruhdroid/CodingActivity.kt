package com.example.bruhdroid

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityCodingBinding
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.blocks.*
import java.util.*

class CodingActivity : AppCompatActivity(), Observer {
    private lateinit var currentInstruction: Instruction
    private var currentBlockLayout = 0
    private var viewToBlock: MutableMap<View, Block> = mutableMapOf()
    private var viewList = LinkedList<View>()
    private var prevBlock: View? = null
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private val interpreter = Interpreter()
    private val controller = Controller()
    private val binding: ActivityCodingBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_coding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding)


        controller.addObserver(this)
        interpreter.addObserver(this)

        binding.addBlockButton.setOnClickListener {
            buildBlock(prevBlock, currentBlockLayout, currentInstruction)
        }
        binding.launchButton.setOnClickListener {
            controller.runProgram(interpreter, viewToBlock, viewList)
        }
        binding.chooseBlockButton.setOnClickListener {
            chooseBlock()
        }

        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    currentInstruction = it.data?.getSerializableExtra("instruction") as Instruction
                    currentBlockLayout = it.data?.getSerializableExtra("blockLayout") as Int
                }
            }
    }

    private fun chooseBlock() {
        val blockPickerIntent = Intent(this@CodingActivity, BlocksActivity::class.java)
        activityLauncher.launch(blockPickerIntent)
    }

    private fun buildBlock(prevView: View?, layoutId: Int, instruction: Instruction) {

        val view = layoutInflater.inflate(layoutId, null)
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
        val builder = AlertDialog.Builder(this)
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
