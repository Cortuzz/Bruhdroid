package com.example.bruhdroid

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View

class CodingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding)

    }
    val dragListener=View.OnDragListener{view,event->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED->{
                println("hello")
                true
            }
            else->true
        }
    }

    private fun dragBlock() {
        val dragBlock: View = findViewById(R.id.blockView)
        dragBlock.setOnLongClickListener {
            println("lel")
            val dragShadowBuilder=View.DragShadowBuilder(it)
            it.startDragAndDrop(null,dragShadowBuilder,it,0)
            it.visibility=View.INVISIBLE
            true
        }
    }
}