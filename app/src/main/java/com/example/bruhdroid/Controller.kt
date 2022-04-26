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
import java.security.AccessController.getContext
import java.util.*

class Controller: Observable() {
    private var lexerErrors = ""
    private var runtimeErrors = ""

    fun runProgram(interpreter: Interpreter, blockMap: MutableMap<View,Block>, viewBlocks: LinkedList<View>) {
        val blocks: MutableList<Block> = mutableListOf()
        for (i in viewBlocks) {

            blockMap[i]!!.expression = i.findViewById<EditText>(R.id.expression).getText().toString()
            blocks.add(blockMap[i]!!)
        }

        try {
            Lexer.checkBlocks(blocks)
            interpreter.initBlocks(blocks)
            interpreter.run()
        } catch (e: RuntimeError) {
            runtimeErrors = e.message.toString()
            setChanged()
            notifyObservers()
        } catch (e: LexerError) {
            lexerErrors = e.message.toString().dropLast(2)
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