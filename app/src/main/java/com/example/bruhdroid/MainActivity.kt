package com.example.bruhdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bruhdroid.blocks.Block
import com.example.bruhdroid.blocks.Init
import com.example.bruhdroid.blocks.Variable
import com.example.bruhdroid.blocks.variables.Integer
import com.example.bruhdroid.blocks.variables.Str

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val interpreter = Interpreter()
        val a = Variable(5, Type.INT)
        val b = Variable(3, Type.INT)
        val c = Variable(17, Type.INT)
        val d = Variable(9, Type.INT)

        val initA = Init("a", a)
        val initB = Init("b", b)
        val initC = Init("c", c)

        interpreter.parseBlock(initA)
        interpreter.parseBlock(initB)
        interpreter.parseBlock(initC)

        val sum1 = Block(Instruction.PLUS, a, b)
        val sum2 = Block(Instruction.MINUS, sum1, c)
        val sum3 = Block(Instruction.MUL, sum2, d)

        // -81
        val initRes = Init("res", sum3)

        interpreter.parseBlock(initRes)
        Log.e("e", interpreter.memory.stack.toString())
    }

    fun exit(view: View) {
        finishAffinity()
    }
}
