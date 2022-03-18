package com.bignerdranch.android.draganddraw

import android.graphics.PointF

/** This class is where we define data will be used to draw a single Box. **/

class Box(private val start: PointF) {

    // When a user touches BoxDrawingView, a new box will be created and added to the list of existing boxes.

    var end: PointF = start

    val left: Float
        get() = start.x.coerceAtMost(end.x)

    val right: Float
        get() = start.x.coerceAtLeast(end.x)

    val top: Float
        get() = start.y.coerceAtMost(end.y)

    val bottom: Float
        get() = start.y.coerceAtLeast(end.y)

}