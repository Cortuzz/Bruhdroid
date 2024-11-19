package com.example.bruhdroid.model.src.blocks.valuable

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError

class NullValuable(
    varValue: Any,
): Valuable(varValue, Type.UNDEFINED) {
    override operator fun unaryPlus(): Valuable {
        throw TypeError("Unary plus can't be applied to type $type")
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return false
    }

    override fun convertToString(valuable: Valuable): String {
        throw TypeError("Expected not-null type but ${valuable.type} was found")
    }
}