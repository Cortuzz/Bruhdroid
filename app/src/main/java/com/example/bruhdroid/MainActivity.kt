package com.example.bruhdroid

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityMainBinding

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
        binding.loadButton.setOnClickListener {
            val intent = Intent(this, TemplatesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.alpha_reversed, R.anim.alpha)
        }
        binding.changeThemeButton.setOnClickListener {
            Controller().changeTheme(resources.configuration.uiMode)
        }
        binding.exitButton.setOnClickListener {
            buildAlertDialog()
        }
    }

    private fun buildAlertDialog() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle("Are you sure you want to quit?")
        builder.setMessage("Maybe it’s better to write some useful code.")

        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
            finishAffinity()
        }
        builder.setNegativeButton(android.R.string.cancel) { _: DialogInterface, _: Int -> }
        builder.show()
    }
}
