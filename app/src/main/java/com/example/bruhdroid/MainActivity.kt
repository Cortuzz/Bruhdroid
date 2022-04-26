package com.example.bruhdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.startButton.setOnClickListener {
            val intent = Intent(this, CodingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.alpha_reversed, R.anim.alpha)
        }
        binding.changeThemeButton.setOnClickListener {
            Controller().changeTheme(resources.configuration.uiMode)
        }
        binding.exitButton.setOnClickListener {
            exitProcess(0)
        }
    }
}
