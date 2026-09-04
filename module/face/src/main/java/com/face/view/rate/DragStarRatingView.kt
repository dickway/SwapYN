package com.face.view.rate

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.key.R
import kotlin.math.abs
import kotlin.math.roundToInt

class DragStarRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var numStars = 5
    var starSize = dp2px(32f)
    var starSpacing = dp2px(32f)
    private var lastStarIndex: Int
        get() = rating.toInt().coerceAtMost(numStars - 1)
        set(_) {}

    private var downX = 0f
    private var isDragging = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop


    var rating = 5f
        get() = field
        set(value) {
            field = value.coerceIn(0f, numStars.toFloat())
            invalidate()
            onRatingChanged?.invoke(field)
        }

    private val emptyStar = BitmapFactory.decodeResource(
        resources, R.drawable.img_star_empty
    )
    private val halfStar = BitmapFactory.decodeResource(
        resources, R.drawable.img_star_half
    )
    private val fullStar = BitmapFactory.decodeResource(
        resources, R.drawable.img_star_full
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var onRatingChanged: ((Float) -> Unit)? = null

    // ================== 测量 ==================
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (starSize * numStars) + starSpacing * (numStars - 1)
        setMeasuredDimension(width, starSize)
    }

    // ================== 绘制 ==================
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until numStars) {
            val left = i * (starSize + starSpacing)
            val rect = Rect(left, 0, left + starSize, starSize)

            val starBitmap = when {
                rating >= i + 1 -> fullStar
                rating >= i + 0.5f -> halfStar
                else -> emptyStar
            }

            canvas.drawBitmap(starBitmap, null, rect, paint)
        }
    }

    // ================== 触摸 ==================
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                isDragging = false
                parent.requestDisallowInterceptTouchEvent(true)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isDragging && abs(event.x - downX) > touchSlop) {
                    isDragging = true
                }
                if (isDragging) {
                    updateRatingByDrag(event.x)
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    handleClick(event.x)
                }
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    private fun updateRatingByDrag(x: Float) {
        val clampedX = x.coerceIn(0f, width.toFloat())
        val percent = clampedX / width
        val rawRating = percent * numStars
        rating = (rawRating * 2).roundToInt() / 2f
    }

    private fun handleClick(x: Float) {
        val starUnit = starSize + starSpacing
        val index = (x / starUnit).toInt()
            .coerceIn(0, numStars - 1)

        rating = when {
            rating == index + 0.5f -> index + 1f
            rating == index + 1f -> index + 0.5f
            else -> index + 0.5f
        }
    }



    private fun updateRatingByTouch(x: Float) {
        val totalWidth = width.toFloat()
        val clampedX = x.coerceIn(0f, totalWidth)

        val percent = clampedX / totalWidth
        val rawRating = percent * numStars

        // ⭐ 半星取整
        rating = (rawRating * 2).roundToInt() / 2f
    }

    private fun dp2px(dp: Float): Int =
        (dp * resources.displayMetrics.density).toInt()
}
