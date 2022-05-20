package com.example.bruhdroid.model

import androidx.core.text.isDigitsOnly
import com.example.bruhdroid.CodingActivity
import com.example.bruhdroid.model.src.*
import com.example.bruhdroid.model.src.blocks.*
import java.lang.IndexOutOfBoundsException
import java.util.*

class Interpreter(_blocks: List<Block>? = null) :
    Observable() {
    var parseMap: MutableMap<String, List<String>> = mutableMapOf()
    var blocks = _blocks?.toMutableList()
    var output = ""
    var input = ""
    var waitingForInput = false
    var memory = Memory(null, "GLOBAL SCOPE")
    var currentLine = -1
    var debug = false
    var ioLines = 0

    private var pragma : MutableMap<String, String> = mutableMapOf(
        "INIT_MESSAGE" to "true",
        "IO_MESSAGE" to "true",
        "IO_LINES" to "10"
        )
    private var appliedConditions: MutableList<Boolean> = mutableListOf()
    private var cycleLines: MutableList<Int> = mutableListOf()

    fun initBlocks(_blocks: List<Block>) {
        clear()
        blocks = _blocks.toMutableList()
    }

    fun getMemoryData(mem: Memory = memory): String {
        val data = parseStack(mem.stack).ifEmpty {"EMPTY"}

        if (mem.prevMemory == null) {
            return "${mem.scope}: $data"
        }
        return "${mem.scope}: $data\n\n${getMemoryData(mem.prevMemory)}"
    }

    private fun parseStack(stack: MutableMap<String, Valuable>): String {
        var data = ""
        for (pair in stack) {
            data += "\n${pair.key} = ${getVisibleValue(pair.value)}: ${pair.value.type}"
        }
        return data
    }


    private fun pragmaClear() {
        pragma = mutableMapOf(
        "INIT_MESSAGE" to "true",
        "IO_MESSAGE" to "true",
        "IO_LINES" to "10"
        )
    }

    private fun pragmaUpdate() {
        output = if (pragma["INIT_MESSAGE"] == "true") {
            ioLines = 5
            "⢸⣿⡟⠛⢿⣷⠀⢸⣿⡟⠛⢿⣷⡄⢸⣿⡇⠀⢸⣿⡇⢸⣿⡇⠀⢸⣿⡇⠀\n" +
            "⢸⣿⣧⣤⣾⠿⠀⢸⣿⣇⣀⣸⡿⠃⢸⣿⡇⠀⢸⣿⡇⢸⣿⣇⣀⣸⣿⡇⠀\n" +
            "⢸⣿⡏⠉⢹⣿⡆⢸⣿⡟⠛⢻⣷⡄⢸⣿⡇⠀⢸⣿⡇⢸⣿⡏⠉⢹⣿⡇⠀\n" +
            "⢸⣿⣧⣤⣼⡿⠃⢸⣿⡇⠀⢸⣿⡇⠸⣿⣧⣤⣼⡿⠁⢸⣿⡇⠀⢸⣿⡇⠀\n" +
            "⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣀ \n"
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
        memory = Memory(null, "GLOBAL SCOPE")
        currentLine = -1
        ioLines = 0
        pragmaClear()
        pragmaUpdate()
    }

    fun runOnce(): Boolean {
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

            if (block.instruction == Instruction.IF) {
                count++
            }
            if (block.instruction == Instruction.END) {
                count--
            }

            if (count == 0 || (count == 1 && (block.instruction == Instruction.ELIF ||
                        block.instruction == Instruction.ELSE))
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

            if (block.instruction == Instruction.WHILE) {
                count++
            }
            if (block.instruction == Instruction.END_WHILE) {
                count--
            }

            if (count == 0) {
                return
            }
        }
    }

    private fun getVisibleValue(valuable: Valuable): String {
        val rawValue = valuable.value

        return when (valuable.type) {
            Type.STRING -> "\"$rawValue\""
            Type.BOOL -> rawValue.uppercase()
            Type.UNDEFINED -> "Ты еблан?"
            Type.LIST -> {
                val str = mutableListOf<String>()
                valuable.array.forEach { el -> str.add(getVisibleValue(el)) }
                str.toString()
            }
            else -> rawValue
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
        val splitted = raw.replace(" ", "").split("=")
        if (splitted[1] !in listOf("true", "false") && splitted[0] !in listOf("IO_LINES")) {
            TODO()
        }

        val variable = splitted[0]

        if (variable !in pragma) {
            TODO()
        }
        pragma[splitted[0]] = splitted[1]
    }

    private fun notifyIfNotDebug() {
        if (!debug) {
            setChanged()
            notifyObservers()
        }
    }

    private fun parse(block: Block): Boolean {
        when (block.instruction) {
            Instruction.PRAGMA -> {
                if (currentLine != 0) {
                    TODO()
                }
                val rawList = split(block.expression)
                for (raw in rawList) {
                    parsePragma(raw)
                }
                pragmaUpdate()
                notifyIfNotDebug()
            }
            Instruction.PRINT -> {
                val rawList = split(block.expression)
                if (pragma["IO_MESSAGE"] == "true") {
                    output += "I/O: "
                }

                for (raw in rawList) {
                    output += "${getVisibleValue(parseRawBlock(raw))} "
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
            Instruction.INPUT -> {
                waitingForInput = true
            }
            Instruction.INIT -> {
                val rawList = split(block.expression)

                for (raw in rawList) {
                    parseRawBlock(raw, true)
                }
            }
            Instruction.SET -> {
                val rawList = split(block.expression)

                for (raw in rawList) {
                    parseRawBlock(raw)
                }
            }
            Instruction.IF -> {
                val statement = checkStatement(block.expression)
                appliedConditions.add(statement)
                memory = Memory(memory, "IF SCOPE")
                return !statement
            }
            Instruction.ELIF -> {
                if (appliedConditions.last()) {
                    return true
                }
                val statement = checkStatement(block.expression)
                appliedConditions[appliedConditions.lastIndex] = statement
                memory = memory.prevMemory!!
                memory = Memory(memory, "ELIF SCOPE")
                return !statement
            }
            Instruction.ELSE -> {
                if (appliedConditions.last()) {
                    return true
                }
                memory = memory.prevMemory!!
                memory = Memory(memory, "ELSE SCOPE")
                return false
            }
            Instruction.WHILE -> {
                memory = Memory(memory, "WHILE SCOPE")
                if (checkStatement(block.expression)) {
                    cycleLines.add(currentLine)
                } else {
                    skipCycle()
                }
            }
            Instruction.END -> {
                appliedConditions.removeLast()
                memory = memory.prevMemory!!
            }
            Instruction.END_WHILE -> {
                currentLine = cycleLines.removeLast() - 1
                memory = memory.prevMemory!!
            }
            Instruction.BREAK -> {
                try{skipCycle()}
                catch (e: Exception) {
                    throwOutOfCycleError("It is not possible to use BREAK outside the context of a loop")}
            }
            Instruction.CONTINUE -> {
                try {
                    currentLine = cycleLines.removeLast() - 1
                    memory = memory.prevMemory!!
                } catch (e: Exception) {
                    throwOutOfCycleError("It is not possible to use CONTINUE block outside the context of a loop")}
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
        booleanBlock = Valuable(booleanBlock.convertToBool(booleanBlock), Type.BOOL)

        if (booleanBlock.value == "true") {
            return true
        }
        return false
    }

    private fun pushToLocalMemory(name: String, type: Type = Type.UNDEFINED, valueBlock: Block) {
        valueBlock as Valuable
        valueBlock.type = type

        memory.push(name, valueBlock)
    }

    private fun tryPushToAnyMemory(
        memory: Memory,
        name: String,
        type: Type,
        valueBlock: Block
    ): Boolean {
        valueBlock as Valuable
        valueBlock.type = type

        if (memory.get(name) != null) {
            memory.push(name, valueBlock)
            return true
        }

        if (memory.prevMemory == null) {
            memory.throwStackError(name)
            return false
        }

        return tryPushToAnyMemory(memory.prevMemory, name, type, valueBlock)
    }

    private fun tryFindInMemory(memory: Memory, block: Block): Valuable {
        block as Variable
        val address = block.name
        val value = memory.get(address)

        if (value != null) {
            return value
        }

        if (memory.prevMemory == null) {
            memory.throwStackError(address)
            throw Exception()
        }

        return tryFindInMemory(memory.prevMemory, block)
    }

    private fun getValue(data: String): Block? {
        if (data in listOf("abs", "exp", "sorted", "ceil", "floor")) {
            return null
        }

        return if (data == "rand()") {
            Valuable(Math.random(), Type.FLOAT)
        } else if (data.last() == '"' && data.first() == '"') {
            // Maybe substring is better solution
            Valuable(data.replace("\"", ""), Type.STRING)
        } else if (data.contains("^[A-Za-z]+\$".toRegex())) {
            Variable(data)
        } else {
            when {
                data.contains("[\\d]+\\.[\\d]+".toRegex()) -> Valuable(data, Type.FLOAT)
                data.contains("[\\d]+".toRegex()) -> Valuable(data, Type.INT)
                else -> null
            }
        }
    }

    private fun parseRawBlock(raw: String, initialize: Boolean = false): Valuable {
        val data = parseMap[raw]?: Notation.convertToRpn(Notation.tokenizeString(raw))
        parseMap[raw] = data

        val stack = mutableListOf<Block>()
        val unary = listOf("±", "∓", ".toInt()", ".toFloat()", ".toBool()", ".toString()", ".sort()",
            "abs", "exp", "sorted", "ceil", "floor")

        for (value in data) {
            if (value.isEmpty()) {
                continue
            }
            val parsedValue = getValue(value)
            if (parsedValue != null) {
                stack.add(parsedValue)
            } else {
                try {
                    var operand2 = try{stack.removeLast()}
                    catch (e: Exception)
                    {throwOperationError("Expected correct expression but bad operation was found")}

                    if (operand2 is Variable) {
                        try {
                            operand2 = tryFindInMemory(memory, operand2)
                        } catch (e: StackCorruptionError) {
                            throw RuntimeError("${e.message}")
                        }
                    }
                    operand2 as Valuable

                    if (value in unary) {
                        stack.add(
                            when (value) {
                                "±" -> +operand2
                                "∓" -> -operand2
                                ".toInt()" -> Valuable(operand2.convertToInt(operand2), Type.INT)
                                ".toFloat()" -> Valuable(operand2.convertToFloat(operand2), Type.FLOAT)
                                ".toString()" -> Valuable(operand2.value, Type.STRING)
                                ".toBool()" -> Valuable(operand2.convertToBool(operand2), Type.BOOL)
                                ".sort()" -> operand2.sort()
                                "abs" -> operand2.absolute()
                                "exp" -> operand2.exponent()
                                "sorted" -> operand2.sorted()
                                "floor" -> operand2.floor()
                                "ceil" -> operand2.ceil()
                                else -> {throwOperationError("Expected correct expression but bad operation was found")
                                    throw Exception()
                                }
                            }
                        )
                        continue
                    }
                    var operand1 = try{stack.removeLast()}
                    catch (e: Exception)
                    {throwOperationError("Expected correct expression but bad operation was found")}

                    if (value in listOf("=", "/=", "+=", "-=", "*=", "%=", "//=")) {
                        if (operand1 is Valuable) {
                            operand1.value = operand2.value
                            operand1.type = operand2.type
                            operand1.array = operand2.array
                        } else if (operand1 is Variable) {
                            val operand = when (value) {
                                "=" -> operand2
                                "/=" -> tryFindInMemory(memory, operand1) / operand2
                                "*=" -> tryFindInMemory(memory, operand1) * operand2
                                "+=" -> tryFindInMemory(memory, operand1) + operand2
                                "-=" -> tryFindInMemory(memory, operand1) - operand2
                                else -> {throwOperationError("Expected correct expression but bad operation was found")
                                    throw Exception()
                                }
                            }

                            if (initialize) {
                                pushToLocalMemory(operand1.name, operand2.type, operand.clone())
                            } else {
                                tryPushToAnyMemory(
                                    memory,
                                    operand1.name,
                                    operand2.type,
                                    operand.clone()
                                )
                            }
                        }

                        return operand2
                    }

                    if (value == "#") {
                        operand1 as Variable
                        if (initialize) {
                            pushToLocalMemory(operand1.name, Type.LIST, operand2)
                        } else {
                            tryPushToAnyMemory(memory, operand1.name, Type.LIST, operand2)
                        }

                        return operand2
                    }

                    if (operand1 is Variable) {
                        try {
                            operand1 = tryFindInMemory(memory, operand1)
                        } catch (e: StackCorruptionError) {
                            throw RuntimeError("${e.message}")
                        }
                    }
                    operand1 as Valuable

                    val result: Valuable? = when (value) {
                        "?" -> {
                            try {operand1.array[operand2.value.toInt()]}
                            catch (e: IndexOutOfBoundsException) {
                                throw IndexOutOfRangeError("${e.message}")
                            }
                        }
                        "+" -> operand1 + operand2
                        "-" -> operand1 - operand2
                        "*" -> operand1 * operand2
                        "/" -> operand1 / operand2
                        "//" -> operand1.intDiv(operand2)
                        "%" -> operand1 % operand2

                        "&&" -> operand1.and(operand2)
                        "||" -> operand1.or(operand2)

                        "==" -> Valuable(operand1 == operand2, Type.BOOL)
                        "!=" -> Valuable(operand1 != operand2, Type.BOOL)
                        "<" -> Valuable(operand1 < operand2, Type.BOOL)
                        ">" -> Valuable(operand1 > operand2, Type.BOOL)
                        "<=" -> Valuable(operand1 < operand2 || operand1 == operand2, Type.BOOL)
                        ">=" -> Valuable(operand1 > operand2 || operand1 == operand2, Type.BOOL)
                        "=" -> {
                            if (initialize) {
                                pushToLocalMemory(operand1.value, operand2.type, operand2.clone())
                            } else {
                                tryPushToAnyMemory(
                                    memory,
                                    operand1.value,
                                    operand2.type,
                                    operand2.clone()
                                )
                            }
                            operand2
                        }
                        else -> null
                    }

                    stack.add(result!!)
                } catch (e: Exception) {
                    throw RuntimeError("${e.message}\nAt expression: $raw")
                }
            }
        }

        if (stack.isEmpty()) {
            throwOperationError("Expected correct expression but bad operation was found", raw)
        }
        if (stack.size > 1) {
            throwOperationError("Expected correct expression but bad operation was found", raw)
        }
        val last = stack.removeLast()
        if (last is Variable) {
            try {
                return tryFindInMemory(memory, last)
            } catch (e: StackCorruptionError) {
                throw RuntimeError("${e.message}\nAt expression: $raw")
            }
        }
        return last as Valuable
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
