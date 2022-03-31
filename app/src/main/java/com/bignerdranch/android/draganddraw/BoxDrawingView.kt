package com.bignerdranch.android.draganddraw

import android.annotation.SuppressLint
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
private const val INVALID_POINTER_ID = 0

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) :
        View(context, attrs) {

    private var currentBox: Box? = null
    private var boxen = mutableListOf<Box>()  // list of boxes to be drawn out on the screen OR arrayList()(Nana's solution)
    private var customView = View(context)

    private var mActivePointerId = INVALID_POINTER_ID
    private var curMPosX : Float = 0f
    private var curMPosY : Float = 0f

    private var mLastTouchX : Float = 0f
    private var mLastTouchY : Float = 0f
    private var mDegrees : Float = 360f


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


    // PROJECT CHALLENGE, BUT STILL NOT WORKING...
    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putParcelableArrayList("boxState", ArrayList(boxen))

        super.onSaveInstanceState()
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var viewState = state
        if (viewState is Bundle) {
            boxen = viewState.getParcelableArrayList("boxState")!!
            viewState = viewState.getParcelable("superState")!!
        }
        val test = boxen
        Log.i(TAG, "Bundle received: $boxen and $test and finally $viewState")

        super.onRestoreInstanceState(viewState)
    }


    override fun onDraw(canvas: Canvas) {
        // Fill in the background
        canvas.drawPaint(backGroundPaint)

        boxen.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
            canvas.save()
            canvas.rotate(mDegrees,  curMPosX, curMPosY)
            canvas.restore()
        }
        Log.i(TAG, "Our canvas point X and point Y are $curMPosX and $curMPosY")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        var multiTouchAction = ""
        var cursor = 500.0000f

        val mAction = event.action

        when(mAction and MotionEvent.ACTION_MASK) {
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
                // NOT THE BEST SOLUTION BUT BASED ON A CHALLENGE
                if (current.x > cursor) {
                    customView.contentDescription = R.string.move_right_content_description.toString()
                    cursor = current.x

                    Log.i(TAG, "Movement is Right")

                } else if (current.x < cursor){
                    customView.contentDescription = R.string.move_left_content_description.toString()
                    cursor = current.x
                    Log.i(TAG, "Movement is Left")

                } else {
                    customView.contentDescription = ""
                }


                // Now we will find the index of the active pointer and fetch its position
                val pointerIndex = event.findPointerIndex(mActivePointerId)
                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)

                val dx = x - mLastTouchX
                val dy = y - mLastTouchY


                curMPosX += dx
                curMPosY += dy

                mLastTouchX += dx
                mLastTouchY += dy
                invalidate()

            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                // tells the last report of the currentBox as the user's finger leaves the screen
                updateCurrentBox(current)
                currentBox = null
                customView.contentDescription = R.string.leave_content_description.toString()

                mActivePointerId = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null

                mActivePointerId = INVALID_POINTER_ID
            }

            // BASED ON PROJECT CHALLENGE.
            MotionEvent.ACTION_POINTER_DOWN -> {
                multiTouchAction = "ACTION_POINTER_DOWN"

                val curX = event.x
                val curY = event.y

                mLastTouchX = curX
                mLastTouchY = curY

                // now we will save the ID of our pointer
                mActivePointerId = event.getPointerId(0)

            }

            MotionEvent.ACTION_POINTER_UP -> {
                multiTouchAction = "ACTION_POINTER_UP"

                // TODO - IF I AM DONE WITH THIS CHALLENGE, I WILL CHECK THE MEANING OF "shr", "bitwise" and "bitCount" operators

                // Here we will extract the index of the pointer that left the touch sensor
                val pointerIndex = (mAction and MotionEvent.ACTION_POINTER_INDEX_MASK) shr
                        MotionEvent.ACTION_POINTER_INDEX_SHIFT

                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = event.getX(newPointerIndex)
                    mLastTouchY = event.getY(newPointerIndex)
                    mActivePointerId = event.getPointerId(newPointerIndex)
                }

            }
        }

        // this is a log message for each of the 4 Event actions
        Log.i(TAG, "$action at x=${current.x}, y=${current.y}")
        Log.i(TAG , "$multiTouchAction at x=${current.x}, y=${current.y}")
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