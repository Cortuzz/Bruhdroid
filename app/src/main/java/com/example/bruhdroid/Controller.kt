package com.example.bruhdroid

import android.content.res.Configuration
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Lexer
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.blocks.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class Controller: Observable() {
    private var lexerErrors = ""
    private var runtimeErrors = ""
    private lateinit var interpreter: Interpreter
    private var notifying = false

    fun runProgram(ip: Interpreter, blockMap: MutableMap<View,Block>, viewBlocks: List<View>) {
        interpreter = ip

        val blocks: MutableList<Block> = mutableListOf()
        for (i in viewBlocks) {
            val expression = i.findViewById<EditText>(R.id.expression)?.text ?: ""
            blockMap[i]!!.expression = expression.toString()
            blocks.add(blockMap[i]!!)
        }

        try {
            Lexer.checkBlocks(blocks)
            interpreter.initBlocks(blocks)
        } catch (e: LexerError) {
            lexerErrors = e.message.toString().dropLast(2)
            setChanged()
            notifyObservers()
        }
        GlobalScope.launch {
            resumeProgram()
        }
    }

    fun resumeProgram() {
        try {
            notifying = interpreter.run()
        } catch (e: RuntimeError) {
            runtimeErrors = e.message.toString()
            notifying = true
        }
        if (notifying) {
            setChanged()
            notifyObservers()
        }
    }

    fun changeTheme(currentMode: Int) {
        val mode = when (currentMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> return
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun popLexerErrors(): String {
        val err = lexerErrors
        lexerErrors = ""
        return err
    }

    fun popRuntimeErrors(): String {
        val err = runtimeErrors
        runtimeErrors = ""
        return err
    }
}