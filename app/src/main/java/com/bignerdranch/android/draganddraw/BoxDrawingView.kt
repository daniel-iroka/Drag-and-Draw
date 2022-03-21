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

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) :
        View(context, attrs) {

    private var currentBox: Box? = null
    private var boxen = arrayListOf<Box>()  // list of boxes to be drawn out on the screen
    private var customView = View(context)

    private val boxPaint = Paint().apply {
        color = 0x22ff0000
    }
    private val backGroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }


    init {
        isSaveEnabled = true
        customView.contentDescription = R.string.place_finger_content_description.toString()
    }



    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelableArrayList(BOX_STATE, boxen)
        bundle.putParcelable("view state", super.onSaveInstanceState())

        super.onSaveInstanceState()
        return bundle
    }


    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)

        var viewState = state
        if (viewState is Bundle) {
            boxen = viewState.getParcelableArrayList(BOX_STATE)!!
            viewState = viewState.getParcelable("view state")!!
        }
        val test = boxen
        Log.i(TAG, "Bundle received: $boxen and $test and finally $viewState")

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
        var cursor = 500.0000f


        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                // Reset drawing state
                currentBox = Box(current).also {
                    boxen.add(it)
                }
                customView.contentDescription = R.string.place_finger_content_description.toString()
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                // update the currentBox.end as the user moves his/her finger around the screen
                updateCurrentBox(current)

                /** LEFT RIGHT IMPLEMENTATION **/
                if (current.x > cursor) {
                    customView.contentDescription = R.string.move_right_content_description.toString()
                    cursor = current.x

                    Log.i(TAG, "Movement is Right")

                } else if (current.x < cursor){
                    customView.contentDescription = R.string.move_left_content_description.toString()
                    cursor = current.x
                    Log.i(TAG, "Movement is Left")

                }

                /** UP DOWN IMPLEMENTATION **/
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                // tells the last report of the currentBox as the user's finger leaves the screen
                updateCurrentBox(current)
                currentBox = null
                customView.contentDescription = R.string.leave_content_description.toString()
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