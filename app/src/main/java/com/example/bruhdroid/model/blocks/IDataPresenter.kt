package com.example.bruhdroid.model.blocks

import com.example.bruhdroid.model.blocks.valuable.Valuable

interface IDataPresenter {
    fun getData(): Valuable

    fun tryGetData(): Valuable?
}