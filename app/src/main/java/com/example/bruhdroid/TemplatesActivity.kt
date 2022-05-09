package com.example.bruhdroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityTemplatesBinding
import com.example.bruhdroid.model.src.TemplateStorage
import com.example.bruhdroid.model.src.TemplateStorage.Companion.Template

class TemplatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemplatesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_templates)

        binding.bubbleSort.setOnClickListener {
            val blocks = TemplateStorage.getBlocks(Template.BUBBLE_SORT)
            val intent = Intent(this, CodingActivity::class.java)
            intent.putExtra("blocks", blocks)
            startActivity(intent)
            overridePendingTransition(R.anim.alpha_reversed, R.anim.alpha)
        }
    }
}