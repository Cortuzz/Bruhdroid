package com.example.bruhdroid.controller

import android.content.res.Configuration
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import com.example.bruhdroid.R
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.exception.RuntimeError
import com.example.bruhdroid.exception.UnhandledError
import com.example.bruhdroid.model.blocks.instruction.*
import com.example.bruhdroid.view.instruction.InstructionHelper
import com.example.bruhdroid.view.instruction.InstructionView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*


class Controller : Observable() {
    companion object {
        var suppressingWarns = false

        fun saveProgram(
            name: String,
            dir: File,
            viewInstructions: MutableList<InstructionView>,
        ): Boolean {
            return try {
                val file = File(dir, "$name.lapp")
                file.setWritable(true)

                viewInstructions.forEach { vi -> vi.instruction.expression =
                    vi.view.findViewById<EditText>(R.id.expression)?.text.toString()
                }

                file.writeText(parseBlocks(viewInstructions).toString())
                true
            } catch (e: Exception) {
                false
            }
        }

        fun loadProgram(file: File, allInstructions: List<Instruction>): Array<Instruction> {
            val instructions = mutableListOf<Instruction>()
            val text = file.readText()

            try {
                val jsonArray = JSONArray(text)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray[i] as JSONObject

                    val instruction = getInstructionByName(
                        jsonObject["instruction"].toString(),
                        allInstructions
                    )!!
                    val expression = jsonObject["expression"] as String

                    instruction.expression = expression
                    instructions.add(instruction)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return instructions.toTypedArray()
        }

        private fun getInstructionByName(
            instructionName: String,
            allInstructions: List<Instruction>
        ): Instruction? {
            for (instruction in allInstructions) {
                if (instruction.compareJsonEncoding(instructionName))
                    return instruction
            }
            return null
        }

        private fun parseBlocks(
            viewInstructions: MutableList<InstructionView>
        ): JSONArray {
            val jsonArray = JSONArray()
            for (vi in viewInstructions) {
                val jsonObject = JSONObject()
                // TODO

                jsonObject.put("instruction", vi.instruction.getJsonEncoding())
                jsonObject.put("expression", vi.instruction.expression)
                jsonArray.put(jsonObject)
            }
            return jsonArray
        }
    }

    private var internalErrors = ""
    private var runtimeErrors = ""
    private lateinit var interpreter: Interpreter
    private var notifying = false

    @OptIn(DelicateCoroutinesApi::class)
    fun runProgram(
        ip: Interpreter,
        viewInstructions: MutableList<InstructionView>,
        debug: Boolean = false
    ) {
        interpreter = ip

        viewInstructions.forEach { vi -> vi.instruction.expression =
            vi.view.findViewById<EditText>(R.id.expression)?.text.toString()
        }
        val blocks = viewInstructions.map { vi -> vi.instruction }

        try {
            interpreter.initBlocks(blocks)

            if (!debug) {
                GlobalScope.launch {
                    coroutineScope {
                        resumeAllIterations()
                    }
                }
                return
            }
            GlobalScope.launch {
                resumeOneIteration()
            }
        } catch (e: UnhandledError) {
            internalErrors = e.message.toString()
            setChanged()
            notifyObservers()
        } catch (e: RuntimeError) {
            runtimeErrors = e.message.toString()
            setChanged()
            notifyObservers()
        }
    }

    fun resumeOneIteration() {
        try {
            notifying = interpreter.runOnce()
        } catch (e: RuntimeError) {
            runtimeErrors = e.message.toString()
            notifying = true
        } catch (e: UnhandledError) {
            internalErrors = e.message.toString()
            notifying = true
        }
        if (notifying) {
            setChanged()
            notifyObservers()
        }
    }

    fun resumeAllIterations() {
        try {
            interpreter.run()
        } catch (e: RuntimeError) {
            runtimeErrors = e.message.toString()
        } catch (e: UnhandledError) {
            internalErrors = e.message.toString()
        }
        setChanged()
        notifyObservers()
    }

    fun changeTheme(currentMode: Int) {
        val mode = when (currentMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun popInternalErrors(): String {
        val err = internalErrors
        internalErrors = ""
        return err
    }

    fun popRuntimeErrors(): String {
        val err = runtimeErrors
        runtimeErrors = ""
        return err
    }
}
