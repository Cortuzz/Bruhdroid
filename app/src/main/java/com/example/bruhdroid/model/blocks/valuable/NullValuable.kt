package com.example.bruhdroid.model.blocks.valuable

import com.example.bruhdroid.model.blocks.ValuableType
import com.example.bruhdroid.exception.TypeError

class NullValuable(listLink: ListValuable? = null):
    Valuable("", ValuableType.UNDEFINED, listLink) {
    override fun clone(): Valuable {
        return NullValuable(listLink)
    }

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