package com.example.bruhdroid

import android.view.View
import android.widget.EditText
import com.example.bruhdroid.model.Interpreter
import com.example.bruhdroid.model.Lexer
import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.LexerError
import com.example.bruhdroid.model.src.RuntimeError
import com.example.bruhdroid.model.src.blocks.*
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