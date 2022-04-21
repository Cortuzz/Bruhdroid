package com.example.bruhdroid


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.Init
import com.example.bruhdroid.model.blocks.RawInput
import com.example.bruhdroid.model.blocks.Valuable
import kotlin.math.exp

class CodingActivity : AppCompatActivity() {
    var viewBlocks: MutableList<View> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding)

        val addBlock: Button = findViewById(R.id.addBlock)
        addBlock.setOnClickListener {
            buildBlock()
        }

        val launch: Button = findViewById(R.id.launchButton)
        launch.setOnClickListener {
            Launch()
        }
    }

    private fun buildBlock() {
        val layout: LinearLayout = findViewById(R.id.container)
        val view = layoutInflater.inflate(R.layout.block, null)
        layout.addView(view)
        viewBlocks.add(view)
    }

    private fun Launch() {
        var blocks: MutableList<Block> = mutableListOf()
        for (view in viewBlocks) {
            val expression = view.findViewById<EditText>(R.id.expression).getText().toString()
            blocks.add(Init(RawInput(expression)))
        }
        val interpreter = Interpreter(blocks)
        interpreter.run()
        println(interpreter.memory.pop("a")?.value)
        println(interpreter.memory.pop("b")?.value)
    }
}