package com.example.bruhdroid

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.View

class MyDragShadowBuilder(private val v: View) : View.DragShadowBuilder(v) {
    private val shadow = ColorDrawable()

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = view.width
        val height: Int = view.height

        shadow.setBounds(0, 0, width, height)
        shadow.alpha = 200

        size.set(width, height)
        touch.set(width / 2, height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {
        v.draw(canvas)
        shadow.draw(canvas)
    }
}