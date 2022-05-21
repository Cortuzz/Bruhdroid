package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.*
import com.example.bruhdroid.model.src.blocks.*
import java.lang.IndexOutOfBoundsException
import java.util.*

class Interpreter(_blocks: List<Block>? = null) : Observable() {
    var blocks = _blocks?.toMutableList()
    var output = ""
    var input = ""
    var waitingForInput = false
    var memory = Memory(null, "GLOBAL SCOPE")
    var currentLine = -1
    var debug = false


    private var pragma: MutableMap<String, String> = mutableMapOf(
        "INIT_MESSAGE" to "true",
        "IO_MESSAGE" to "true",
        "IO_LINES" to "10"
    )

    private var parseMap: MutableMap<String, List<String>> = mutableMapOf()
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

    fun getMemoryData(mem: Memory = memory): String {
        val data = parseStack(mem.stack).ifEmpty { "EMPTY" }

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

            if (block.instruction in listOf(Instruction.WHILE, Instruction.FOR)) {
                count++
            }
            if (block.instruction in listOf(Instruction.END_WHILE, Instruction.END_FOR)) {
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
            Type.UNDEFINED -> "NULL"
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

            if (block.instruction == Instruction.FUNC) {
                count++
            }
            if (block.instruction == Instruction.FUNC_END) {
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
            Instruction.PRAGMA -> {
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
            Instruction.FUNC -> {
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
            Instruction.FUNC_CALL -> {
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
            Instruction.FUNC_END -> {}
            Instruction.RETURN -> {
                val value = parseRawBlock(block.expression)
                val funcName = currentFunction.removeLast()
                val varName = functionsVarsMap[funcName]!!.removeLast()
                currentLine = funcVarsLines.removeLast()

                removeFunctionMemory()
                pushToLocalMemory(varName, value.type, value)
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
                memory = Memory(memory, "WHILE ITERATION SCOPE")
                if (checkStatement(block.expression)) {
                    cycleLines.add(currentLine)
                } else {
                    skipCycle()
                }
            }
            Instruction.FOR -> {
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
            Instruction.END -> {
                appliedConditions.removeLast()
                memory = memory.prevMemory!!
            }
            Instruction.END_WHILE -> {
                currentLine = cycleLines.removeLast() - 1
                memory = memory.prevMemory!!
            }
            Instruction.END_FOR -> {
                currentLine = cycleLines.removeLast() - 1
                memory = memory.prevMemory!!
            }
            Instruction.BREAK -> {
                try {
                    skipCycle()
                } catch (e: Exception) {
                    throwOutOfCycleError("It is not possible to use BREAK outside the context of a loop")
                }
            }
            Instruction.CONTINUE -> {
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
        booleanBlock = Valuable(booleanBlock.convertToBool(booleanBlock), Type.BOOL)

        if (booleanBlock.value == "true") {
            return true
        }
        return false
    }

    private fun pushToLocalMemory(name: String, type: Type = Type.UNDEFINED, valueBlock: Block) {
        valueBlock as Valuable

        if (type == Type.LIST) {
            val block = valueBlock.clone()
            block.type = type
            memory.push(name, block)
            return
        }

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
        if (data in listOf("abs", "exp", "sorted", "ceil", "floor", "len")) {
            return null
        }

        return if (data == "rand()") {
            Valuable(Math.random(), Type.FLOAT)
        } else if (data in listOf("true", "false")) {
            Valuable(data, Type.BOOL)
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
        val data = parseMap[raw] ?: Notation.convertToRpn(Notation.tokenizeString(raw))
        parseMap[raw] = data

        val stack = mutableListOf<Block>()
        val unary = listOf(
            "±", "∓", ".toInt()", ".toFloat()", ".toBool()", ".toString()", ".sort()", ".toList()",
            "abs", "exp", "sorted", "ceil", "floor", "len"
        )

        for (value in data) {
            if (value.isEmpty()) {
                continue
            }
            val parsedValue = getValue(value)
            if (parsedValue != null) {
                stack.add(parsedValue)
            } else {
                try {
                    var operand2 = try {
                        stack.removeLast()
                    } catch (e: Exception) {
                        throwOperationError("Expected correct expression but bad operation was found")
                    }

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
                                ".toFloat()" -> Valuable(
                                    operand2.convertToFloat(operand2),
                                    Type.FLOAT
                                )
                                ".toString()" -> Valuable(
                                    operand2.convertToString(operand2),
                                    Type.STRING
                                )
                                ".toBool()" -> Valuable(operand2.convertToBool(operand2), Type.BOOL)
                                ".toList()" -> {
                                    val array = operand2.convertToArray(operand2).toMutableList()
                                    val listVal = Valuable(array.size, Type.LIST)
                                    listVal.array = array
                                    listVal
                                }
                                ".sort()" -> operand2.sort()
                                "abs" -> operand2.absolute()
                                "exp" -> operand2.exponent()
                                "sorted" -> operand2.sorted()
                                "floor" -> operand2.floor()
                                "ceil" -> operand2.ceil()
                                "len" -> operand2.getLength()
                                else -> {
                                    throwOperationError("Expected correct expression but bad operation was found")
                                    throw Exception()
                                }
                            }
                        )
                        continue
                    }
                    var operand1 = try {
                        stack.removeLast()
                    } catch (e: Exception) {
                        throwOperationError("Expected correct expression but bad operation was found")
                    }

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
                                "%=" -> tryFindInMemory(memory, operand1) % operand2
                                "//=" -> tryFindInMemory(memory, operand1).intDiv(operand2)
                                else -> {
                                    throwOperationError("Expected correct expression but bad operation was found")
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
                            try {
                                operand1.array[operand2.value.toInt()]
                            } catch (e: IndexOutOfBoundsException) {
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
