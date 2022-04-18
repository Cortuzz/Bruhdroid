package com.example.bruhdroid.blocks

import com.example.bruhdroid.Instruction
import com.example.bruhdroid.blocks.variables.Str
import com.example.bruhdroid.blocks.variables.Integer

abstract class Variable(open val name: String = "") :
    Block(Instruction.VAR,null, null) {

    abstract operator fun plus(operand: Integer): Variable

    abstract operator fun minus(operand: Integer): Variable

    abstract operator fun times(operand: Integer): Variable

    abstract operator fun times(operand: Str): Variable

    abstract operator fun div(operand: Integer): Variable

    abstract operator fun rem(operand: Integer): Variable

    abstract operator fun plus(operand: Str): Variable
}
