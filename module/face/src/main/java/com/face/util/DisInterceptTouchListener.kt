package com.face.util

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class DisInterceptTouchListener : View.OnTouchListener {

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v !is ViewGroup) {
            return false
        }
        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                v.parent.requestDisallowInterceptTouchEvent(true)
//                v.onTouchEvent(event)
//                return true
//            }
            MotionEvent.ACTION_MOVE -> {
                v.parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }
}

