package com.example.bruhdroid.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bruhdroid.controller.Controller
import com.example.bruhdroid.R
import com.example.bruhdroid.databinding.ActivityTemplatesBinding
import com.example.bruhdroid.databinding.ButtonBinding
import com.example.bruhdroid.view.template.TemplateFactory
import com.example.bruhdroid.model.blocks.instruction.Instruction
import com.example.bruhdroid.view.instruction.InstructionHelper


class TemplatesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemplatesBinding
    private lateinit var templateFactory: TemplateFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_templates)
        templateFactory = TemplateFactory(binding)

        for (template in templateFactory.getTemplates()) {
            template.bindingButton.setOnClickListener {
                openCodingActivity(template.blocks)
            }
        }

        loadSavedProgramsFromStorage()
    }

    private fun loadSavedProgramsFromStorage() {
        for (file in this.filesDir.listFiles()) {
            if (!file.name.contains("\\.lapp$".toRegex())) {
                continue
            }

            val buttonBinding: ButtonBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.button, null, false)
            val dp: Float = this.resources.displayMetrics.density
            val button = buttonBinding.text

            button.text = file.nameWithoutExtension
            buttonBinding.deleteButton.setOnClickListener {
                file.delete()
                binding.savedPrograms.removeView(buttonBinding.root)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val margin = 12
            val totalMargin = dp.toInt() * margin
            params.setMargins(0, totalMargin, 0, 0)

            buttonBinding.root.layoutParams = params
            button.setOnClickListener {
                openCodingActivity(
                    Controller.loadProgram(
                        file,
                        InstructionHelper(layoutInflater).getInstructionViews()
                            .map { vi -> vi.instruction }
                    ),
                    file.nameWithoutExtension
                )
            }

            binding.savedPrograms.addView(buttonBinding.root)
        }
    }

    private fun openCodingActivity(blocks: Array<Instruction>?, filename: String? = null) {
        val intent = Intent(this, CodingActivity::class.java)
        intent.putExtra("blocks", blocks)
        intent.putExtra("filename", filename)
        startActivity(intent)
        overridePendingTransition(R.anim.alpha_reversed, R.anim.alpha)
    }
}