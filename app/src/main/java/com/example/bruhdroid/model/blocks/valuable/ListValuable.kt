package com.example.bruhdroid.model.blocks.valuable

import com.example.bruhdroid.exception.RuntimeError
import com.example.bruhdroid.exception.TypeError
import com.example.bruhdroid.model.blocks.valuable.numeric.IntegerValuable

class ListValuable(initFrom: Valuable, listLink: ListValuable? = null):
    Valuable("", ValuableType.LIST, listLink) {
    override var array: MutableList<Valuable> = mutableListOf()

    init {
        if (initFrom is IntegerValuable) {
            initArray(initFrom.convertToInt(initFrom))
        } else if (initFrom is ListValuable) {
            copyArray(initFrom.array)
        }
    }

    override fun getVisibleValue(): String {
        val str = mutableListOf<String>()
        array.forEach { el -> str.add(el.getVisibleValue()) }
        return str.toString()
    }

    fun update(old: Valuable, new: Valuable) {
        val copied = new.clone()
        copied.listLink = this
        array[getValuableIndex(old)] = copied
    }

    override fun clone(): ListValuable {
        val valuable = ListValuable(this, listLink)
        valuable.array = array
        return valuable
    }

    override fun getLength(): Valuable {
        return IntegerValuable(array.size)
    }

    override fun convertToBool(valuable: Valuable): Boolean {
        return valuable.array.isNotEmpty()
    }

    override fun convertToString(valuable: Valuable): String {
        return valuable.array.toString()
    }

    override fun convertToArray(valuable: Valuable): List<Valuable> {
        return array
    }

    override fun sorted(): Valuable {
        val valuable = ListValuable(this)
        valuable.array = srt()
        return valuable
    }

    override fun sort(): Valuable {
        array = srt()
        return this
    }


    private fun srt(): MutableList<Valuable> {
        return array.sortedBy { i ->
            try {
                i.value.toFloat()
            } catch (e: Exception) {
                throw TypeError("Expected INT or FLOAT but found ${i.type}")
            }
        }.toMutableList()
    }

    private fun initArray(count: Int) {
        for (i in 0 until count) {
            array.add(NullValuable())
        }
    }

    private fun copyArray(newArr: MutableList<Valuable>) {
        array = mutableListOf()
        for (el in newArr) {
            el.listLink = this
            array.add(el)
        }
    }

    private fun getValuableIndex(valuable: Valuable): Int {
        for (ind in array.indices) {
            if (array[ind].hashCode() == valuable.hashCode())
                return ind
        }

        throw RuntimeError("No such element")
    }
}