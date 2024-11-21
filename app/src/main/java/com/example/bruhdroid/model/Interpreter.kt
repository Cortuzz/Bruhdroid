package com.example.bruhdroid.model

import android.annotation.SuppressLint
import com.example.bruhdroid.exception.*
import com.example.bruhdroid.model.blocks.Block
import com.example.bruhdroid.model.blocks.IDataPresenter
import com.example.bruhdroid.model.blocks.BlockInstruction
import com.example.bruhdroid.model.blocks.instruction.InputInstruction
import com.example.bruhdroid.model.blocks.instruction.Instruction
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
    var waitingForInput = false
    var memory = Memory(null, "GLOBAL SCOPE")

    private var blocks = _blocks?.toMutableList()
    val memoryPresentor = MemoryPresentor()
    private var input = ""
    var currentLine = -1

    var pragma: MutableMap<String, String> = mutableMapOf(
        "INIT_MESSAGE" to "true",
        "IO_MESSAGE" to "true",
        "IO_LINES" to "10"
    )

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

    fun initBlocks(_blocks: List<Block>) {
        clear()
        blocks = _blocks.toMutableList()
    }

    fun getBlockAtCurrentLine(): Block? {
        return blocks?.get(getLine())
    }

    fun getBlocksSize(): Int? {
        return blocks?.size
    }

    fun getLine(): Int {
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

    fun run() {
        tryParseInput()

        if (currentLine == -1)
            notifyClients()

        while (currentLine < blocks!!.size - 1 && !waitingForInput)
            runOnce()
    }

    fun runOnce(): Boolean {
        tryParseInput()

        if (currentLine >= blocks!!.size - 1)
            return false

        runIteration()
        return true
    }

    private fun runIteration() {
        val block = blocks!![++currentLine]

        try {
            if (parse(block))
                skipFalseBranches()
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

    fun skipCycle() {
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

    fun skipFunc() {
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

    fun removeFunctionMemory() {
        while (true) {
            if (memory.scope.contains("METHOD")) {
                break
            }
            memory = memory.prevMemory!!
        }
        memory = memory.prevMemory!!
    }

    fun increaseIoLines() {
        ++ioLines
    }

    fun decreaseIoLines() {
        --ioLines
    }

    private fun parse(block: Block): Boolean {
        block as Instruction
        block.initInterpreter(this)
        return block.evaluate()
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
