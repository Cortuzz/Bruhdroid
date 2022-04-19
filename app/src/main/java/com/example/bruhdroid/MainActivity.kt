package com.example.bruhdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bruhdroid.model.blocks.Assign
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.Init
import com.example.bruhdroid.model.blocks.Valuable
import com.example.bruhdroid.model.Instruction
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Type

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = Valuable(5, Type.INT)
        val b = Valuable(3, Type.INT)
        val c = Valuable(17, Type.INT)
        val d = Valuable(9, Type.INT)

        val initA = Init("a", a)
        val initB = Init("b", b)
        val initC = Init("c", c)
        val assignC = Assign("a", d)

        val sum1 = Block(Instruction.PLUS, a, b)
        val sum2 = Block(Instruction.MINUS, sum1, c)
        val sum3 = Block(Instruction.MUL, sum2, d)

        // -81
        val initRes = Init("res", sum3)
        val blocks = listOf(initA, initB, initC, assignC, initRes)

        val interpreter = Interpreter(blocks)
        interpreter.run()
    }

    fun exit(view: View) {
        finishAffinity()
    }
}
