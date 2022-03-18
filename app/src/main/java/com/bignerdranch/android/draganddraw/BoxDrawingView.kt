package com.bignerdranch.android.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/** This Class is where we setup our custom View and write the Implementation for listening to touch events from the USER and draw boxes on the Screen.**/

private const val TAG = "BoxDrawingView"

// TODO - WHEN I COME BACK, I WILL DO THE CHALLENGES AND AFTER I AM DONE I WILL RESEARCH ON MOTION EVENTS LATER LATER SHA.

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) :
        View(context, attrs) {

    private var currentBox: Box? = null
    private val boxen = mutableListOf<Box>()  // list of boxes to be drawn out on the screen
    private val boxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val backGroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    override fun onDraw(canvas: Canvas) {
        // Fill in the background
        canvas.drawPaint(backGroundPaint)

        boxen.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                // Reset drawing state
                currentBox = Box(current).also {
                    boxen.add(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                // update the currentBox.end as the user moves his/her finger around the screen
                updateCurrentBox(current)
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                // tells the last report of the currentBox as the user's finger leaves the screen
                updateCurrentBox(current)
                currentBox = null
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
        }

        // this is a log message for each of the 4 Event actions
        Log.i(TAG, "$action at x=${current.x}, y=${current.y}")

        return true
    }


    // will update the list of boxes... I think ???
    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            invalidate()
        }
    }
}