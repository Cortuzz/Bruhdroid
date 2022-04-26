package com.example.bruhdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityBlocksBinding
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.blocks.Block

class BlocksActivity : AppCompatActivity() {
    private var layoutId = 0

    private lateinit var binding: ActivityBlocksBinding
    private lateinit var instruction: Instruction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_blocks)

        binding.initButton.setOnClickListener {
            layoutId = R.layout.block_init
            instruction = Instruction.INIT
            returnBlock()
        }
        binding.printButton.setOnClickListener {
            layoutId = R.layout.block_print
            instruction = Instruction.PRINT
            returnBlock()
        }
        binding.inputButton.setOnClickListener {
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