package com.example.bruhdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.Block

class BlocksActivity : AppCompatActivity() {
    private lateinit var instruction: Instruction
    private var layoutId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocks)

        val initButton: Button = findViewById(R.id.initButton)
        initButton.setOnClickListener {
            layoutId = R.layout.block_init
            instruction = Instruction.INIT
            returnBlock()
        }

        val printButton: Button = findViewById(R.id.printButton)
        printButton.setOnClickListener {
            layoutId = R.layout.block_print
            instruction = Instruction.PRINT
            returnBlock()
        }

        val inputButton: Button = findViewById(R.id.inputButton)
        inputButton.setOnClickListener {
            layoutId = R.layout.block_input
            instruction = Instruction.INPUT
            returnBlock()
        }
    }

    private fun returnBlock() {
        val intent = Intent().apply { putExtra("instruction", instruction) }
        intent.apply { putExtra("blockLayout", layoutId) }
        setResult(RESULT_OK, intent)
        finish()
    }
}