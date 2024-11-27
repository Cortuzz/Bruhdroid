package com.example.bruhdroid.model.interpreter

import android.annotation.SuppressLint
import com.example.bruhdroid.exception.*
import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.blocks.instruction.Instruction
import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.memory.MemoryPresenter
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.operation.operator.AssignOperator
import com.example.bruhdroid.model.operation.operator.Operator
import com.example.bruhdroid.model.blocks.valuable.BooleanValuable
import com.example.bruhdroid.model.blocks.valuable.Valuable
import java.util.*

class Interpreter(instructions_: List<Instruction>? = null) : Observable() {

    var output = ""
    var waitingForInput = false
    var memory = Memory(null, "GLOBAL SCOPE")

    var instructions = instructions_?.toMutableList()
    val memoryPresenter = MemoryPresenter()
    private var input = ""
    var currentLine = -1

    var pragma: MutableMap<String, String> = getPragmaDefaults()

    private var parseMap: MutableMap<String, List<Operation>> = mutableMapOf()
    var ioLines = 0
        private set
    var appliedConditions: MutableList<Boolean> = mutableListOf()
    var cycleLines: MutableList<Int> = mutableListOf()
    var functionLines = mutableMapOf<String, MutableList<Int>>()
    var currentFunction = mutableListOf("GLOBAL")
    var functionsVarsMap = mutableMapOf<String, MutableList<String>>()
    var funcVarsLines = mutableListOf<Int>()
    var args = mutableListOf<List<String>>()
    var forLines = mutableListOf<Int>()

    fun initBlocks(instructions: List<Instruction>) {
        clear()
        this.instructions = instructions.toMutableList()
    }

    fun getBlockAtCurrentLine(): Instruction? {
        return instructions?.get(getLine())
    }

    fun getBlocksSize(): Int? {
        return instructions?.size
    }

    fun getLine(): Int {
        return currentLine + 1
    }

    fun handleUserInput(inp: String) {
       input = inp
       waitingForInput = false
    }

    fun getMemoryData(): String {
        return memoryPresenter.getMemoryData(memory)
    }

    private fun getPragmaDefaults(): MutableMap<String, String> {
        return mutableMapOf(
            "INIT_MESSAGE" to "true", // Выводить ли приветственное сообщение при запуске
            "IO_MESSAGE" to "true", // Выводить ли "I/O:" при каждом консольном выводе
            "IO_LINES" to "10" // Сколько линий в консоли будет отображаться одновременно
        )
    }

    @SuppressLint("SuspiciousIndentation")
    fun pragmaUpdate() {
        output = if (pragma["INIT_MESSAGE"] == "true") {
            ioLines = 5
                    "⢸⣿⡟⠛⢿⣷⠀⢸⣿⡟⠛⢿⣷⡄⢸⣿⡇⠀⢸⣿⡇⢸⣿⡇⠀⢸⣿⡇\n" +
                    "⢸⣿⣧⣤⣾⠿⠀⢸⣿⣇⣀⣸⡿⠃⢸⣿⡇⠀⢸⣿⡇⢸⣿⣇⣀⣸⣿⡇\n" +
                    "⢸⣿⡏⠉⢹⣿⡆⢸⣿⡟⠛⢻⣷⡄⢸⣿⡇⠀⢸⣿⡇⢸⣿⡏⠉⢹⣿⡇\n" +
                    "⢸⣿⣧⣤⣼⡿⠃⢸⣿⡇⠀⢸⣿⡇⠸⣿⣧⣤⣼⡿⠁⢸⣿⡇⠀⢸⣿⡇\n" +
                    "⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀\n"
        } else {
            ""
        }
    }

    fun clear() {
        input = ""
        waitingForInput = false
        appliedConditions.clear()
        cycleLines.clear()
        instructions?.clear()

        functionLines.clear()
        currentFunction = mutableListOf("GLOBAL")
        functionsVarsMap.clear()
        funcVarsLines.clear()
        args.clear()

        memory = Memory(null, "GLOBAL SCOPE")
        currentLine = -1
        ioLines = 0
        pragma = getPragmaDefaults()
        pragmaUpdate()
    }

    fun run() {
        tryParseInput()

        if (currentLine == -1)
            notifyClients()

        while (currentLine < instructions!!.size - 1 && !waitingForInput)
            runOnce()
    }

    fun runOnce(): Boolean {
        tryParseInput()

        if (currentLine >= instructions!!.size - 1)
            return false

        runIteration()
        return true
    }

    private fun runIteration() {
        val block = instructions!![++currentLine]

        try {
            block.evaluate(this)
        } catch (e: RuntimeError) {
            throw RuntimeError(
                "${e.message}\nAt line: ${currentLine + 1}, " +
                        "At instruction: ${block.instruction}"
            )
        } catch (e: Exception) {
            throw UnhandledError(
                "At line: ${currentLine + 1}, At instruction: ${block.instruction}\n\n${e.stackTraceToString()}"
            )
        }
    }

    fun split(str: String): List<String> {
        var isString = false
        val parsed = mutableListOf<String>()
        var tempStr = ""

        for (symbol in str) {
            if (symbol == '"') {
                isString = !isString
            }

            if (symbol == ',' && !isString) {
                parsed.add(tempStr)
                tempStr = ""
                continue
            }
            tempStr += symbol
        }
        parsed.add(tempStr)
        return parsed
    }

    private fun tryParseInput() {
        if (input.isEmpty())
            return

        val block = instructions!![currentLine]
        val rawList = input.split(",")
        val rawCommands = block.expression.split(",")
        if (rawList.size != rawCommands.size) {
            throw RuntimeError("At expression: ${block.expression}")
        }
        for (i in rawList.indices) {
            parseRawBlock(rawCommands[i] + "=\"" + rawList[i] + "\"")
        }
        input = ""
    }

    fun parsePragma(raw: String) {
        val split = raw.replace(" ", "").split("=")
        if (split[1] !in listOf("true", "false") && split[0] !in listOf("IO_LINES")) {
            throw RuntimeError("Bad preprocessor directive value")
        }

        val variable = split[0]

        if (variable !in pragma) {
            throw RuntimeError("No such preprocessor directive found")
        }
        pragma[split[0]] = split[1]
    }

    fun notifyClients() {
        setChanged()
        notifyObservers()
    }

    fun parseFunc(expression: String): Map<String, List<String>> {
        val split = expression.replace(")", "").replace(" ", "").split("(").toMutableList()

        val name = listOf(split.removeFirst())
        val args = split.joinToString().split(",")
        if (args.firstOrNull() == "") {
            return mapOf("name" to name, "args" to listOf())
        }

        return mapOf("name" to name, "args" to args)
    }

    fun increaseIoLines() {
        ++ioLines
    }

    fun decreaseIoLines() {
        --ioLines
    }

    fun throwOutOfCycleError(message: String) {
        try {
            throw BlockOutOfCycleContextError(message)
        } catch (e: BlockOutOfCycleContextError) {
            throw RuntimeError(e.message.toString())
        }
    }

    fun checkStatement(statement: String): Boolean {
        var booleanBlock = parseRawBlock(statement)
        booleanBlock = BooleanValuable(booleanBlock.convertToBool(booleanBlock))

        if (booleanBlock.value == "true") {
            return true
        }
        return false
    }

    fun parseRawBlock(raw: String, initialize: Boolean = false): Valuable {
        val data = parseMap[raw] ?: InterpreterParser.convertInfixToPostfixNotation(
            InterpreterParser.tokenizeString(raw)
        )
        parseMap[raw] = data

        val stack = mutableListOf<IDataPresenter>()

        for (operation in data) {
            val parsedValue = operation.evaluateExpressionToBlock(memory)
            if (parsedValue != null) {
                stack.add(parsedValue)
                continue
            }
            operation as Operator

            try {
                if (stack.isEmpty())
                    throwOperationError("Expected correct expression but bad operation was found")

                val operand2 = stack.removeLast().getData()

                if (operation.getParsedUnary()) {
                    stack.add(operation.act(operand2, null)!!)
                    continue
                }

                if (stack.isEmpty())
                    throwOperationError("Expected correct expression but bad operation was found")

                var operand1 = stack.removeLast()

                if (operation is AssignOperator) {
                    operation.assign(operand1, operand2, initialize, memory)
                    return operand2
                }

                operand1 = operand1.getData()

                stack.add(operation.act(operand1, operand2)!!)
            } catch (e: Exception) {
                throw RuntimeError("${e.message}\nAt expression: $raw")
            }
        }

        if (stack.size != 1)
            throwOperationError("Expected correct expression but bad operation was found", raw)

        try {
            return stack.removeLast().getData()
        } catch (e: StackCorruptionError) {
            throw RuntimeError("${e.message}\nAt expression: $raw")
        }
    }

    private fun throwOperationError(message: String, raw: String = "") {
        try {
            throw OperationError(message)
        } catch (e: Exception) {
            if (raw.isEmpty()) {
                throw RuntimeError("${e.message}")
            }
            throw RuntimeError("${e.message}\nAt expression: $raw")
        }
    }
}
