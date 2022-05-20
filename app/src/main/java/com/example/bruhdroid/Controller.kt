package com.example.bruhdroid

import android.content.res.Configuration
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.UnhandledError
import com.example.bruhdroid.model.src.blocks.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


class Controller: Observable() {
    companion object {
        var suppressingWarns = false

        fun saveProgram(name: String, dir: File, blockMap: MutableMap<View, Block>, viewBlocks: List<View>): Boolean {
            return try {
                val file = File(dir, "$name.lapp")
                file.setWritable(true)
                file.writeText(parseBlocks(blockMap, viewBlocks).toString())
                true
            } catch (e: Exception) {false}
        }

        fun loadProgram(file: File): Array<Block> {
            val blocksMap = mapOf(
                "SET" to Instruction.SET,
                "INIT" to Instruction.INIT,
                "PRINT" to Instruction.PRINT,
                "PRAGMA" to Instruction.PRAGMA,
                "INPUT" to Instruction.INPUT,
                "IF" to Instruction.IF,
                "ELIF" to Instruction.ELIF,
                "ELSE" to Instruction.ELSE,
                "WHILE" to Instruction.WHILE,
                "END" to Instruction.END,
                "END_WHILE" to Instruction.END_WHILE,
                "BREAK" to Instruction.BREAK,
                "CONTINUE" to Instruction.CONTINUE,
                "FUNC" to Instruction.FUNC,
                "FUNC_END" to Instruction.FUNC_END,
                "FUNC_CALL" to Instruction.FUNC_CALL,
                "RETURN" to Instruction.RETURN,
            )

            val blocks = mutableListOf<Block>()
            val text = file.readText()

            try {
                val jsonArray = JSONArray(text)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray[i] as JSONObject

                    val instruction = blocksMap[jsonObject["instruction"]]
                    val expression = jsonObject["expression"] as String

                    blocks.add(Block(instruction!!, expression))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return blocks.toTypedArray()
        }

        private fun parseBlocks(blockMap: MutableMap<View, Block>, viewBlocks: List<View>): JSONArray {
            val jsonArray = JSONArray()
            for (i in viewBlocks) {
                val jsonObject = JSONObject()
                val expression = i.findViewById<EditText>(R.id.expression)?.text ?: ""

                jsonObject.put("instruction", blockMap[i]!!.instruction)
                jsonObject.put("expression", expression)
                jsonArray.put(jsonObject)
            }
            return jsonArray
        }
    }

    private var internalErrors = ""
    private var runtimeErrors = ""
    private lateinit var interpreter: Interpreter
    private var notifying = false

    fun runProgram(ip: Interpreter, blockMap: MutableMap<View,Block>, viewBlocks: List<View>, debug: Boolean = false) {
        interpreter = ip

        val blocks: MutableList<Block> = mutableListOf()
        for (i in viewBlocks) {
            val expression = i.findViewById<EditText>(R.id.expression)?.text ?: ""
            blockMap[i]!!.expression = expression.toString()
            blocks.add(blockMap[i]!!)
        }

        try {
            interpreter.initBlocks(blocks)
            interpreter.debug = debug

            if (!debug) {
                GlobalScope.launch {
                    coroutineScope {
                        resumeFull()
                    }
                }
                return
            }
            GlobalScope.launch {
                resumeProgram()
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

    fun resumeProgram() {
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

    fun resumeFull() {
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
