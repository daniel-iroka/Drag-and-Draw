package com.bignerdranch.android.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/** This Class is where we setup our custom View and write the Implementation for listening to touch events from the USER and draw boxes on the Screen.**/

private const val TAG = "BoxDrawingView"
private const val BOX_STATE = "box"
private const val VIEW_STATE = "view"

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) :
        View(context, attrs) {

    private var currentBox: Box? = null
    private var boxen = mutableListOf<Box>()  // list of boxes to be drawn out on the screen
//    private var box = boxen as ArrayList

    private val boxPaint = Paint().apply {
        color = 0x22ff0000
    }
    private val backGroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }


    init {
        isSaveEnabled = true
    }


    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
//        bundle.putParcelableArrayList(BOX_STATE, box)
        bundle.putParcelable(VIEW_STATE, super.onSaveInstanceState())

        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var viewState = state
        if (viewState is Bundle) {
//            box = viewState.getParcelableArrayList(BOX_STATE)!!
            viewState = viewState.getParcelable(VIEW_STATE)!!
        }
        super.onRestoreInstanceState(state)
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