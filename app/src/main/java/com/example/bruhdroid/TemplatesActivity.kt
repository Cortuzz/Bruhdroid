package com.example.bruhdroid

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.databinding.ActivityTemplatesBinding
import com.example.bruhdroid.databinding.ButtonBinding
import com.example.bruhdroid.model.src.TemplateStorage
import com.example.bruhdroid.model.src.TemplateStorage.Companion.Template
import com.example.bruhdroid.model.src.blocks.Block


class TemplatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemplatesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_templates)

        binding.bubbleSort.setOnClickListener {
            openCodingActivity(TemplateStorage.getBlocks(Template.BUBBLE_SORT))
        }
        binding.factorial.setOnClickListener {
            openCodingActivity(TemplateStorage.getBlocks(Template.FACTORIAL))
        }
        binding.fibonacci.setOnClickListener {
            openCodingActivity(TemplateStorage.getBlocks(Template.FIBONACCI))
        }
        binding.infinityLoop.setOnClickListener {
            openCodingActivity(TemplateStorage.getBlocks(Template.INFINITY_LOOP))
        }
        binding.ahegao.setOnClickListener {
            openCodingActivity(TemplateStorage.getBlocks(Template.AHEGAO))
        }

        loadPrograms()
    }

    private fun loadPrograms() {
        for (file in this.filesDir.listFiles()) {
            if (!file.name.contains("\\.lapp$".toRegex())) {
                continue
            }

            val buttonBinding: ButtonBinding = DataBindingUtil.inflate(layoutInflater, R.layout.button, null, false)
            val dp: Float = this.resources.displayMetrics.density
            var button = buttonBinding.text

            button.text = file.nameWithoutExtension
            buttonBinding.deleteButton.setOnClickListener{
                file.delete()
                binding.savedPrograms.removeView(buttonBinding.root)
            }

            button.width = 320 * dp.toInt()

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val margin = 12
            val totalMargin = dp.toInt() * margin
            params.setMargins(0, totalMargin, 0, 0)

            buttonBinding.root.layoutParams = params
            button.setOnClickListener {
                openCodingActivity(Controller.loadProgram(file), file.nameWithoutExtension)
            }

            binding.savedPrograms.addView(buttonBinding.root)
        }
    }

    private fun openCodingActivity(blocks: Array<Block>?, filename: String? = null) {
        val intent = Intent(this, CodingActivity::class.java)
        intent.putExtra("blocks", blocks)
        intent.putExtra("filename", filename)
        startActivity(intent)
        overridePendingTransition(R.anim.alpha_reversed, R.anim.alpha)
    }
}