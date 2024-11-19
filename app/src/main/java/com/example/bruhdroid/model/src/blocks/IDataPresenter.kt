package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.memory.Memory

interface IDataPresenter {
    fun getData(): Valuable

    fun tryGetData(): Valuable?
}