package com.example.bruhdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.bruhdroid.variables.Integer
import com.example.bruhdroid.variables.Str
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton()
        exitButton()
    }

    private fun startButton() {
        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            val intent = Intent(this, CodingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun exitButton() {
        val exitButton: Button = findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            exitProcess(0)
        }
    }

}
