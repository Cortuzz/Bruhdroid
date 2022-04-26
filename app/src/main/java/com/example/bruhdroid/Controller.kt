package com.example.bruhdroid

import android.os.Bundle
import android.view.View
import android.widget.Button
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

    fun runProgram(interpreter: Interpreter, instructions: List<Instruction>, viewBlocks: List<View>) {
        val blocks: MutableList<Block> = mutableListOf()
        for (i in viewBlocks.indices) {
            val view = viewBlocks[i]
            val instr = instructions[i]

            val expression = view.findViewById<EditText>(R.id.expression).getText().toString()
            blocks.add(Block(instr, expression))
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

    /*private fun getBlockClass(instruction: Instruction, data: RawInput, additionalBlocks: List<Block>? = null): Block {
        return when (instruction) {
            Instruction.INIT -> Init(data)
            Instruction.PRINT -> Print(data)
            Instruction.INPUT -> Input(data)
            else -> throw Exception()
        }
    }*/
}