package com.example.bruhdroid


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.example.bruhdroid.model.Instruction
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.Interpreter

class CodingActivity : AppCompatActivity() {

    var blocks: MutableList<Block> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding)

        val addBlock: Button = findViewById(R.id.addBlock)
        addBlock.setOnClickListener {
            buildBlock()
        }

    }

    private fun buildBlock() {
        val layout: LinearLayout = findViewById(R.id.container)
        val view = layoutInflater.inflate(R.layout.block, null)
        println(view.findViewById<EditText>(R.id.variable).text)
        layout.addView(view)
        //blocks.add(Block.(Instruction.SET,))
    }

    private fun Launch() {
        //val interpreter: Interpreter = Interpreter()
    }

}