package com.example.bruhdroid.model.blocks.valuable

class BooleanValuable(
    varValue: Any,
    listLink: ListValuable? = null
): Valuable(varValue, ValuableType.BOOL, listLink) {
    override fun clone(): Valuable {
        return BooleanValuable(value, listLink)
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.value.toBoolean()
    }

    override fun convertToFloat(valuable: Valuable): Float {
        if (valuable.value == "true")
            return 1f
        return 0f
    }

    override fun convertToInt(valuable: Valuable): Int {
        if (valuable.value == "true")
            return 1
        return 0
    }
}