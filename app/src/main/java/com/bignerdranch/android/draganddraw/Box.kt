package com.bignerdranch.android.draganddraw

import android.graphics.PointF

/** This class is where we define data will be used to draw a single Box. **/

class Box(private val start: PointF) {

    // When a user touches BoxDrawingView, a new box will be created and added to the list of existing boxes.

    var end: PointF = start

    val left: Float
        get() = Math.min(start.x, end.x)

    val right: Float
        get() = Math.max(start.x, end.x)

    val top: Float
        get() = Math.min(start.y, end.y)

    val bottom: Float
        get() = Math.max(start.y, end.y)

}