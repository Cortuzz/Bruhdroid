package com.example.bruhdroid.model.src.blocks

import com.example.bruhdroid.model.src.blocks.valuable.Valuable

interface IDataPresenter {
    fun getData(): Valuable

    fun tryGetData(): Valuable?
}