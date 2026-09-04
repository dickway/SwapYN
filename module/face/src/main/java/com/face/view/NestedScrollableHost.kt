package com.face.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class NestedScrollableHost  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var initialX = 0f
    private var initialY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val child = getChildAt(0)
        if (child is RecyclerView) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = ev.x
                    initialY = ev.y
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(ev.x - initialX)
                    val dy = abs(ev.y - initialY)
                    val orientation = (parent as? ViewPager2)?.orientation ?: ViewPager2.ORIENTATION_VERTICAL
                    val disallowIntercept = if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                        dx > dy
                    } else {
                        dy > dx
                    }
                    if (orientation == ViewPager2.ORIENTATION_VERTICAL) {
                        val atTop = !child.canScrollVertically(-1)
//                        val atBottom = !child.canScrollVertically(1)
                        val movingDown = (ev.y - initialY) > 0
                        val movingUp = !movingDown

                        val shouldDisallow = when {
                            movingDown && atTop -> false // 到顶还往下滑，允许 ViewPager2 处理
//                            movingUp && atBottom -> false // 到底还往上滑，允许 ViewPager2 处理
                            else -> true
                        }

                        // 只在滑动方向明确时设置拦截
                        if (dy > 4) { // 防止轻微滑动被误判
                            parent.requestDisallowInterceptTouchEvent(shouldDisallow)
                        }

                    } else {
                        if (dx > 4) { // 防止轻微横滑也触发
                            parent.requestDisallowInterceptTouchEvent(disallowIntercept)
                        }
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

}