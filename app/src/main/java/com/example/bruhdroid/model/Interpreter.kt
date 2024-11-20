package com.example.bruhdroid.model

import android.annotation.SuppressLint
import com.example.bruhdroid.exception.*
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.Variable
import com.example.bruhdroid.model.memory.Memory
import com.example.bruhdroid.model.memory.MemoryPresentor
import com.example.bruhdroid.model.operation.Operation
import com.example.bruhdroid.model.operation.operator.AssignOperator
import com.example.bruhdroid.model.operation.operator.Operator
import com.example.bruhdroid.model.blocks.valuable.BooleanValuable
import com.example.bruhdroid.model.blocks.valuable.Valuable
import java.util.*

class Interpreter(_blocks: List<Block>? = null) : Observable() {

    var output = ""
        private set
    var waitingForInput = false
        private set
    var memory = Memory(null, "GLOBAL SCOPE")
        private set

    private var blocks = _blocks?.toMutableList()
    private val memoryPresentor = MemoryPresentor()
    private var input = ""
    private var currentLine = -1
    private var debug = false

    private var pragma: MutableMap<String, String> = mutableMapOf(
        "INIT_MESSAGE" to "true",
        "IO_MESSAGE" to "true",
        "IO_LINES" to "10"
    )

    private var parseMap: MutableMap<String, List<Operation>> = mutableMapOf()
    private var ioLines = 0
    private var appliedConditions: MutableList<Boolean> = mutableListOf()
    private var cycleLines: MutableList<Int> = mutableListOf()
    private var functionLines = mutableMapOf<String, MutableList<Int>>()
    private var currentFunction = mutableListOf("GLOBAL")
    private var functionsVarsMap = mutableMapOf<String, MutableList<String>>()
    private var funcVarsLines = mutableListOf<Int>()
    private var args = mutableListOf<List<String>>()
    private var forLines = mutableListOf<Int>()

    fun initBlocks(_blocks: List<Block>) {
        clear()
        blocks = _blocks.toMutableList()
    }

    fun getBlockAtCurrentLine(): Block? {
        return blocks?.get(getCurrentLine())
    }

    fun getBlocksSize(): Int? {
        return blocks?.size
    }

    fun getCurrentLine(): Int {
        return currentLine + 1
    }

    fun handleUserInput(inp: String) {
       input = inp
       waitingForInput = false
    }

    fun getMemoryData(): String {
        return memoryPresentor.getMemoryData(memory)
    }

    private fun pragmaClear() {
        pragma = mutableMapOf(
            "INIT_MESSAGE" to "true",
            "IO_MESSAGE" to "true",
            "IO_LINES" to "10"
        )
    }

    @SuppressLint("SuspiciousIndentation")
    private fun pragmaUpdate() {
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
        blocks?.clear()

        functionLines.clear()
        currentFunction = mutableListOf("GLOBAL")
        functionsVarsMap.clear()
        funcVarsLines.clear()
        args.clear()

        memory = Memory(null, "GLOBAL SCOPE")
        currentLine = -1
        ioLines = 0
        pragmaClear()
        pragmaUpdate()
    }

    fun runOnce(debugMode: Boolean): Boolean {
        debug = debugMode

        if (input.isNotEmpty()) {
            parseInput()
        }

        if (currentLine >= blocks!!.size - 1) {
            return false
        }

        val block = blocks!![++currentLine]

        try {
            if (parse(block)) {
                skipFalseBranches()
            }
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

        return true
    }

    fun run() {
        run(false)
    }

    fun run(debugMode: Boolean) {
        debug = debugMode

        if (input.isNotEmpty()) {
            parseInput()
        }

        if (currentLine == -1) {
            notifyIfNotDebug()
        }

        while (currentLine < blocks!!.size - 1 && !waitingForInput) {
            val block = blocks!![++currentLine]

            try {
                if (parse(block)) {
                    skipFalseBranches()
                }
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
    }

    private fun skipFalseBranches() {
        var count = 1
        while (currentLine < blocks!!.size - 1) {
            val block = blocks!![++currentLine]

            if (block.instruction == BlockInstruction.IF) {
                count++
            }
            if (block.instruction == BlockInstruction.END) {
                count--
            }

            if (count == 0 || (count == 1 && (block.instruction == BlockInstruction.ELIF ||
                        block.instruction == BlockInstruction.ELSE))
            ) {
                currentLine--
                return
            }
        }
    }

    private fun skipCycle() {
        var count = 1
        memory = memory.prevMemory!!
        while (currentLine < blocks!!.size - 1) {
            val block = blocks!![++currentLine]

            if (block.instruction in listOf(BlockInstruction.WHILE, BlockInstruction.FOR)) {
                count++
            }
            if (block.instruction in listOf(BlockInstruction.END_WHILE, BlockInstruction.END_FOR)) {
                count--
            }

            if (count == 0) {
                return
            }
        }
    }

    private fun split(str: String): List<String> {
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

    private fun parseInput() {
        val block = blocks!![currentLine]
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

    private fun parsePragma(raw: String) {
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

    private fun notifyIfNotDebug() {
        if (!debug) {
            setChanged()
            notifyObservers()
        }
    }

    private fun parseFunc(expression: String): Map<String, List<String>> {
        val split = expression.replace(")", "").replace(" ", "").split("(").toMutableList()

        val name = listOf(split.removeFirst())
        val args = split.joinToString().split(",")
        if (args.firstOrNull() == "") {
            return mapOf("name" to name, "args" to listOf())
        }

        return mapOf("name" to name, "args" to args)
    }

    private fun skipFunc() {
        var count = 1
        memory = memory.prevMemory!!
        while (currentLine < blocks!!.size - 1) {
            val block = blocks!![++currentLine]

            if (block.instruction == BlockInstruction.FUNC) {
                count++
            }
            if (block.instruction == BlockInstruction.FUNC_END) {
                count--
            }

            if (count == 0) {
                return
            }
        }
    }

    private fun removeFunctionMemory() {
        while (true) {
            if (memory.scope.contains("METHOD")) {
                break
            }
            memory = memory.prevMemory!!
        }
        memory = memory.prevMemory!!
    }

    private fun parse(block: Block): Boolean {
        when (block.instruction) {
            BlockInstruction.PRAGMA -> {
                val rawList = split(block.expression)
                for (raw in rawList) {
                    parsePragma(raw)
                }
                pragmaUpdate()
                notifyIfNotDebug()
            }
            BlockInstruction.PRINT -> {
                val rawList = split(block.expression)
                if (pragma["IO_MESSAGE"] == "true") {
                    output += "I/O: "
                }

                for (raw in rawList) {
                    output += "${memoryPresentor.getVisibleValue(parseRawBlock(raw))} "
                }
                ++ioLines
                output += "\n"
                val lines = pragma["IO_LINES"]
                if (lines != null) {
                    if (lines != "inf" && ioLines > lines.toInt()) {
                        --ioLines
                        val ind = output.indexOf("\n")
                        output = output.substring(ind + 1)
                    }
                }
                notifyIfNotDebug()
            }
            BlockInstruction.FUNC -> {
                val name = parseFunc(block.expression)["name"]?.get(0)
                val argNames = parseFunc(block.expression)["args"]!!
                memory = Memory(memory, "METHOD $name SCOPE")

                if (name in functionLines) {
                    functionLines[name]!!.add(currentLine)
                } else {
                    functionLines[name!!] = mutableListOf(currentLine)
                }

                if (currentFunction.last() != name) {
                    skipFunc()
                    return false
                }
                val args = args.removeLast()
                for (i in args.indices) {
                    val value = args[i]
                    val arg = argNames[i]
                    parseRawBlock("$arg = $value", true)
                }
            }
            BlockInstruction.FUNC_CALL -> {
                val exp = block.expression.split("=").toMutableList()

                val name = exp.removeFirst().replace(" ", "")
                val data = parseFunc(exp.joinToString())

                if (exp.isEmpty()) {
                    val parsed = parseFunc(name)
                    val parsedName = parsed["name"]!![0]
                    args.add(parsed["args"]!!)

                    funcVarsLines.add(currentLine)
                    currentFunction.add(parsedName)
                    currentLine = functionLines[parsedName]!!.removeLast() - 1
                } else {
                    val funcName = data["name"]!![0]
                    args.add(data["args"]!!)

                    funcVarsLines.add(currentLine)
                    if (funcName in functionsVarsMap) {
                        functionsVarsMap[funcName]!!.add(name)
                    } else {
                        functionsVarsMap[funcName] = mutableListOf(name)
                    }

                    currentFunction.add(funcName)
                    currentLine = functionLines[funcName]!!.removeLast() - 1
                }
            }
            BlockInstruction.FUNC_END -> {}
            BlockInstruction.RETURN -> {
                val value = parseRawBlock(block.expression)
                val funcName = currentFunction.removeLast()
                val varName = functionsVarsMap[funcName]!!.removeLast()
                currentLine = funcVarsLines.removeLast()

                removeFunctionMemory()
                memory.pushToLocalMemory(varName, value.type, value)
            }
            BlockInstruction.INPUT -> {
                waitingForInput = true
            }
            BlockInstruction.INIT -> {
                val rawList = split(block.expression)

                for (raw in rawList) {
                    parseRawBlock(raw, true)
                }
            }
            BlockInstruction.SET -> {
                val rawList = split(block.expression)

                for (raw in rawList) {
                    parseRawBlock(raw)
                }
            }
            BlockInstruction.IF -> {
                val statement = checkStatement(block.expression)
                appliedConditions.add(statement)
                memory = Memory(memory, "IF SCOPE")
                return !statement
            }
            BlockInstruction.ELIF -> {
                if (appliedConditions.last()) {
                    return true
                }
                val statement = checkStatement(block.expression)
                appliedConditions[appliedConditions.lastIndex] = statement
                memory = memory.prevMemory!!
                memory = Memory(memory, "ELIF SCOPE")
                return !statement
            }
            BlockInstruction.ELSE -> {
                if (appliedConditions.last()) {
                    return true
                }
                memory = memory.prevMemory!!
                memory = Memory(memory, "ELSE SCOPE")
                return false
            }
            BlockInstruction.WHILE -> {
                memory = Memory(memory, "WHILE ITERATION SCOPE")
                if (checkStatement(block.expression)) {
                    cycleLines.add(currentLine)
                } else {
                    skipCycle()
                }
            }
            BlockInstruction.FOR -> {
                val raw = block.expression.split(",")
                if (currentLine !in forLines) {
                    memory = Memory(memory, "FOR SCOPE")
                    parseRawBlock(raw[0], true)
                    forLines.add(currentLine)
                } else {
                    parseRawBlock(raw[2])
                }

                memory = Memory(memory, "FOR ITERATION SCOPE")
                if (checkStatement(raw[1])) {
                    cycleLines.add(currentLine)
                } else {
                    memory = memory.prevMemory!!
                    forLines.remove(currentLine)
                    skipCycle()
                }
            }
            BlockInstruction.END -> {
                appliedConditions.removeLast()
                memory = memory.prevMemory!!
            }
            BlockInstruction.END_WHILE -> {
                currentLine = cycleLines.removeLast() - 1
                memory = memory.prevMemory!!
            }
            BlockInstruction.END_FOR -> {
                currentLine = cycleLines.removeLast() - 1
                memory = memory.prevMemory!!
            }
            BlockInstruction.BREAK -> {
                try {
                    skipCycle()
                } catch (e: Exception) {
                    throwOutOfCycleError("It is not possible to use BREAK outside the context of a loop")
                }
            }
            BlockInstruction.CONTINUE -> {
                try {
                    currentLine = cycleLines.removeLast() - 1
                    memory = memory.prevMemory!!
                } catch (e: Exception) {
                    throwOutOfCycleError("It is not possible to use CONTINUE block outside the context of a loop")
                }
            }
            else -> parseRawBlock(block.expression)
        }

        return false
    }

    private fun throwOutOfCycleError(message: String) {
        try {
            throw BlockOutOfCycleContextError(message)
        } catch (e: BlockOutOfCycleContextError) {
            throw RuntimeError(e.message.toString())
        }
    }

    private fun checkStatement(statement: String): Boolean {
        var booleanBlock = parseRawBlock(statement)
        booleanBlock = BooleanValuable(booleanBlock.convertToBool(booleanBlock))

        if (booleanBlock.value == "true") {
            return true
        }
        return false
    }

    private fun parseRawBlock(raw: String, initialize: Boolean = false): Valuable {
        val data = parseMap[raw] ?: Notation.convertInfixToPostfixNotation(Notation.tokenizeString(raw))
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

                if (operation.unary) {
                    stack.add(operation.act(operand2, null)!!)
                    continue
                }

                if (stack.isEmpty())
                    throwOperationError("Expected correct expression but bad operation was found")

                var operand1 = stack.removeLast()

                if (operation is AssignOperator) {
                    operation.assign(operand1 as Variable, operand2, initialize, memory)
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
