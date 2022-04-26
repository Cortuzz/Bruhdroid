package com.example.bruhdroid

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.blocks.*
import java.util.*

class CodingActivity : AppCompatActivity(), Observer {
    private lateinit var currentInstruction: Instruction
    private var currentBlockLayout = 0

    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private var viewBlocks: MutableList<View> = mutableListOf()
    private var instructions: MutableList<Instruction> = mutableListOf()
    private val interpreter = Interpreter()
    private val controller = Controller()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding)

        controller.addObserver(this)
        interpreter.addObserver(this)

        val addBlock: Button = findViewById(R.id.addBlock)
        addBlock.setOnClickListener {
            buildBlock()
        }

        val launch: Button = findViewById(R.id.launchButton)
        launch.setOnClickListener {
            controller.runProgram(interpreter, instructions, viewBlocks)
        }

        val blocks: Button = findViewById(R.id.chooseBlock)
        blocks.setOnClickListener {
            chooseBlock()
        }

        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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

    private fun buildBlock() {
        val layout: LinearLayout = findViewById(R.id.container)
        val view = layoutInflater.inflate(currentBlockLayout, null)
        layout.addView(view)
        viewBlocks.add(view)
        instructions.add(currentInstruction)
    }

    private fun buildAlertDialog(label: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(label)
        builder.setMessage(message)

        builder.setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    override fun update(p0: Observable?, p1: Any?) {
        val console: TextView = findViewById(R.id.console)

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
            console.append(output + "\n")
        }
    }
}
