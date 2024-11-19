package com.example.bruhdroid.model.src.blocks.valuable

import com.example.bruhdroid.model.src.Type
import com.example.bruhdroid.model.src.TypeError

class BooleanValuable(
    varValue: Any,
): Valuable(varValue, Type.BOOL) {
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