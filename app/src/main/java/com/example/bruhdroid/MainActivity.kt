package com.example.bruhdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bruhdroid.blocks.Block
import com.example.bruhdroid.blocks.Init
import com.example.bruhdroid.blocks.variables.Integer
import com.example.bruhdroid.blocks.variables.Str

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val interpreter = Interpreter()
        val a = Integer(value=5)
        val b = Integer(value=3)
        val c = Integer(value=17)

        val initA = Init("a", a)
        val initB = Init("b", b)
        val initC = Init("c", c)

        interpreter.parseBlock(initA)
        interpreter.parseBlock(initB)
        interpreter.parseBlock(initC)

        val sum1 = Block(Instruction.ADD, a, b)
        val sum2 = Block(Instruction.ADD, sum1, c)

        val initRes = Init("res", sum2)

        interpreter.parseBlock(initRes)
        Log.e("e", interpreter.memory.stack.toString())
    }

    fun exit(view: View) {
        finishAffinity()
    }
}
