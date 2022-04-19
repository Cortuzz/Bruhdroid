package com.example.bruhdroid.blocks

import com.example.bruhdroid.Instruction
import com.example.bruhdroid.Type
import com.example.bruhdroid.blocks.variables.Str
import com.example.bruhdroid.blocks.variables.Integer

class Variable(var varValue: Any, private val type: Type) :
    Block(Instruction.VAR,null, null) {
    var value: String = varValue.toString()

    operator fun plus(operand: Variable): Variable {
        if (type == Type.STRING && operand.type == Type.STRING) {
            return Variable(value + operand.value, type)
        }
        if (type == Type.INT && operand.type == Type.INT) {
            return Variable(value.toInt() + operand.value.toInt(), type)
        }
        throw Exception()
    }

    operator fun minus(operand: Variable): Variable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Variable(value.toInt() - operand.value.toInt(), type)
        }
        throw Exception()
    }

    operator fun times(operand: Variable): Variable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Variable(value.toInt() * operand.value.toInt(), type)
        }
        if (type == Type.INT && operand.type == Type.STRING) {
            return Variable(operand.value.repeat(value.toInt()), type)
        }
        if (type == Type.STRING && operand.type == Type.INT) {
            return Variable(value.repeat(operand.value.toInt()), type)
        }
        throw Exception()
    }

    operator fun div(operand: Variable): Variable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Variable(value.toInt() / operand.value.toInt(), type)
        }
        throw Exception()
    }

    operator fun rem(operand: Variable): Variable {
        if (type == Type.INT && operand.type == Type.INT) {
            return Variable(value.toInt() % operand.value.toInt(), type)
        }
        throw Exception()
    }
}
