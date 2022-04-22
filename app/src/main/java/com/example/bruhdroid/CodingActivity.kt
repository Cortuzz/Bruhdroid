package com.example.bruhdroid


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.blocks.*

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
        try {
            Lexer.checkBlocks(blocks)
            interpreter.run()
        } catch(e: RuntimeError) {
            print(e.message)
        } catch(e: LexerError) {
            print(e.message)
        }

        println("a: " + interpreter.memory.pop("a")?.value)
        println("b: " + interpreter.memory.pop("b")?.value)
        println("c: " + interpreter.memory.pop("c")?.value)
    }
}
