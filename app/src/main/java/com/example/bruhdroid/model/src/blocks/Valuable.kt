package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.Instruction
import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError

class Valuable(varValue: Any, var type: Type) :
    Block(Instruction.VAL,null, null) {
    var value: String = varValue.toString()

    operator fun plus(operand: Valuable): Valuable {
        if (type == Type.STRING && operand.type == Type.STRING) {
            return Valuable(value + operand.value, type)
        }
        if (type == Type.INT && operand.type == Type.INT) {
            return Valuable(value.toInt() + operand.value.toInt(), type)
        }
        throw TypeError("Expected $type but found ${operand.type}")
    }

    operator fun minus(operand: Valuable): Valuable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Valuable(value.toInt() - operand.value.toInt(), type)
        }
        throw TypeError("Expected INT or FLOAT but found STRING")
    }

    operator fun times(operand: Valuable): Valuable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Valuable(value.toInt() * operand.value.toInt(), type)
        }
        if (type == Type.INT && operand.type == Type.STRING) {
            return Valuable(operand.value.repeat(value.toInt()), operand.type)
        }
        if (type == Type.STRING && operand.type == Type.INT) {
            return Valuable(value.repeat(operand.value.toInt()), type)
        }

        if (type != Type.STRING) {
            throw TypeError("Expected INT but found ${operand.type}")
        }
        throw TypeError("Expected INT but found $type")
    }

    operator fun div(operand: Valuable): Valuable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Valuable(value.toInt() / operand.value.toInt(), type)
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }

    operator fun rem(operand: Valuable): Valuable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Valuable(value.toInt() % operand.value.toInt(), type)
        }
        throw TypeError("Expected INT or FLOAT but found ${operand.type}")
    }
}
