package com.example.bruhdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bruhdroid.variables.Integer
import com.example.bruhdroid.variables.Str
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = Integer("a", 5)
        val b = Integer("b", 2)
    }

    fun exit(view: View) {
        exitProcess(0)
    }
}
