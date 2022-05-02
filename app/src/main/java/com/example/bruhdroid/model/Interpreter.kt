package com.example.bruhdroid.model

import com.example.bruhdroid.model.src.*
import com.example.bruhdroid.model.src.blocks.*
import java.util.*

class Interpreter(private var blocks: List<Block>? = null, val debugMode: Boolean = false) :
    Observable() {
    var output = ""
    var input = ""
    var waitingForInput = false
    var memory = Memory(null)
    private var appliedConditions: MutableList<Boolean> = mutableListOf()
    private var cycleLines: MutableList<Int> = mutableListOf()
    private var currentLine = -1

    fun initBlocks(_blocks: List<Block>) {
        output = ""
        input = ""
        waitingForInput = false
        appliedConditions.clear()
        cycleLines.clear()
        memory = Memory(null)
        currentLine = -1
        blocks = _blocks
    }

    fun runOnce(): Boolean {
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
                "${e.message}\nAt line: ${block.line}, " +
                        "At instruction: ${block.instruction}"
            )
        } catch (e: Exception) {
            throw RuntimeError(
                "${e}\nAt line: ${block.line}, " +
                        "At instruction: ${block.instruction}"
            )
        }
        return true
    }

    fun run() {
        while (currentLine < blocks!!.size - 1) {
            val block = blocks!![++currentLine]

            try {
                if (parse(block)) {
                    skipFalseBranches()
                }
            } catch (e: RuntimeError) {
                throw RuntimeError(
                    "${e.message}\nAt line: ${block.line}, " +
                            "At instruction: ${block.instruction}"
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
            Type.UNDEFINED -> "UNDEFINED"
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

    private fun parse(block: Block): Boolean {
        when (block.instruction) {
            Instruction.PRINT -> {
                val rawList = split(block.expression)

                for (raw in rawList) {
                    output += "${getVisibleValue(parseRawBlock(raw))} "
                }
                output += "\n"
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
                memory = Memory(memory)
                return !statement
            }
            Instruction.ELIF -> {
                if (appliedConditions.last()) {
                    return true
                }
                val statement = checkStatement(block.expression)
                appliedConditions[appliedConditions.lastIndex] = statement
                memory = memory.prevMemory!!
                memory = Memory(memory)
                return !statement
            }
            Instruction.ELSE -> {
                if (appliedConditions.last()) {
                    return true
                }
                memory = memory.prevMemory!!
                memory = Memory(memory)
                return false
            }
            Instruction.WHILE -> {
                memory = Memory(memory)
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
                skipCycle()
            }
            Instruction.CONTINUE -> {
                currentLine = cycleLines.removeLast() - 1
                memory = memory.prevMemory!!
            }
            else -> parseRawBlock(block.expression)
        }

        return false
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

    private fun tryFindInMemory(memory: Memory, block: Block): Block {
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

    private fun parseRawBlock(raw: String, initialize: Boolean = false): Valuable {
        println(Notation.tokenizeString(raw))
        val data = Notation.convertToRpn(raw)

        var count = 0
        val stack = mutableListOf<Block>()
        var tempStr = ""

        while (count < data.length) {
            if (data[count].isDigit() || data[count].isLetter() || data[count] == '"') {
                var isString = false
                if (data[count] == '"') {
                    isString = true
                }

                while (data[count].isDigit() || data[count].isLetter() || data[count] in "\"." || isString) {
                    if (data[count] == '"' && tempStr.isNotEmpty()) {
                        isString = false
                    }
                    tempStr += data[count]
                    count++
                }
                if (tempStr.last() == '"' && tempStr.first() == '"') {
                    // Maybe substring is better solution
                    stack.add(Valuable(tempStr.replace("\"", ""), Type.STRING))
                } else if (tempStr.contains("[A-Za-z]".toRegex())) {
                    stack.add(Variable(tempStr))
                } else {
                    if (tempStr.contains('.')) {
                        stack.add(Valuable(tempStr, Type.FLOAT))
                    } else {
                        stack.add(Valuable(tempStr, Type.INT))
                    }
                }

                tempStr = ""
                count--
            } else if (data[count] != ' ') {
                try {
                    var operand2 = stack.removeLast()

                    if (operand2 is Variable) {
                        try {
                            operand2 = tryFindInMemory(memory, operand2)
                        } catch (e: StackCorruptionError) {
                            throw RuntimeError("${e.message}\nAt expression: $raw")
                        }
                    }
                    operand2 as Valuable

                    if (data[count] in "∓±") {
                        stack.add(
                            when (data[count]) {
                                '±' -> +operand2
                                '∓' -> -operand2
                                else -> throw Exception()
                            }
                        )
                        count += 2
                        continue
                    }
                    var operand1 = stack.removeLast()

                    if (data[count] == '≈') {
                        if (operand1 is Valuable) {
                            operand1.value = operand2.value
                            operand1.type = operand2.type
                            operand1.array = operand2.array
                        } else if (operand1 is Variable) {
                            if (initialize) {
                                pushToLocalMemory(operand1.name, operand2.type, operand2.clone())
                            } else {
                                tryPushToAnyMemory(
                                    memory,
                                    operand1.name,
                                    operand2.type,
                                    operand2.clone()
                                )
                            }
                        }

                        return operand2
                    }

                    if (data[count] == '#') {
                        operand1 as Variable
                        if (initialize) {
                            pushToLocalMemory(operand1.name, Type.LIST, operand2)
                        } else {
                            tryPushToAnyMemory(memory, operand1.name, Type.LIST, operand2)
                        }

                        return operand2
                    }

                    if (operand1 is Variable) {
                        operand1 = tryFindInMemory(memory, operand1)
                    }
                    operand1 as Valuable

                    val result: Valuable? = when (data[count]) {
                        '?' -> operand1.array[operand2.value.toInt()]
                        '+' -> operand1 + operand2
                        '-' -> operand1 - operand2
                        '*' -> operand1 * operand2
                        '/' -> operand1 / operand2

                        '&' -> operand1.and(operand2)
                        '|' -> operand1.or(operand2)

                        '=' -> Valuable(operand1 == operand2, Type.BOOL)
                        '≠' -> Valuable(operand1 != operand2, Type.BOOL)
                        '<' -> Valuable(operand1 < operand2, Type.BOOL)
                        '>' -> Valuable(operand1 > operand2, Type.BOOL)
                        '≤' -> Valuable(operand1 < operand2 || operand1 == operand2, Type.BOOL)
                        '≥' -> Valuable(operand1 > operand2 || operand1 == operand2, Type.BOOL)
                        '≈' -> {
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
                    count++
                } catch (e: TypeError) {
                    throw RuntimeError("${e.message}\nAt expression: $raw")
                }
            }
            count++
        }

        val last = stack.removeLast()
        if (last is Variable) {
            try {
                return tryFindInMemory(memory, last) as Valuable
            } catch (e: StackCorruptionError) {
                throw RuntimeError("${e.message}\nAt expression: $raw")
            }
        }
        return last as Valuable
    }
}
