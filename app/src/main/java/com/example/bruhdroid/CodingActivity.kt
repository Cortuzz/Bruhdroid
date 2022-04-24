package com.example.bruhdroid


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.bruhdroid.model.*
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.blocks.*
import java.util.*

open class CodingActivity : AppCompatActivity(), Observer {
    var viewBlocks: MutableList<View> = mutableListOf()
    val interpreter = Interpreter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding)

        interpreter.addObserver(this)

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
        val view = layoutInflater.inflate(R.layout.block_init, null)
        layout.addView(view)
        viewBlocks.add(view)
    }

    private fun Launch() {
        val blocks: MutableList<Block> = mutableListOf()
        for (view in viewBlocks) {
            val expression = view.findViewById<EditText>(R.id.expression).getText().toString()
            blocks.add(Init(RawInput(expression)))
        }
        blocks.add(Print(RawInput("a + 3")))

        try {
            Lexer.checkBlocks(blocks)
            interpreter.initBlocks(blocks)
            interpreter.run()
        } catch(e: RuntimeError) {
            print(e.message)
        } catch(e: LexerError) {
            print(e.message)
        }
    }

    override fun update(p0: Observable?, p1: Any?) {
        val console: TextView = findViewById(R.id.console)
        console.append(interpreter.popOutput() + "\n")
    }
}
