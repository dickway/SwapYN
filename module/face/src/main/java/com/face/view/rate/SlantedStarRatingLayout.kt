package com.face.view.rate

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class SlantedStarRatingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val stars = ArrayList<SlantedStarView>(5)

    init {
        orientation = HORIZONTAL
        repeat(5) {
            val star = SlantedStarView(context).apply {
                layoutParams = LayoutParams(dp(24), dp(24)).apply {
                    marginEnd = dp(4)
                }
            }
            addView(star)
            stars.add(star)
        }
    }

    fun setRating(rating: Float) {
        stars.forEachIndexed { index, star ->
//            star.percent = (rating - index).coerceIn(0f, 1f)
            val raw = (rating - index).coerceIn(0f, 1f)
            star.percent = compensateSlantNegative(raw)
        }
    }

    private fun compensateSlantNegative(p: Float): Float {
        if (p <= 0f) return 0f
        if (p >= 1f) return 1f

        // 只对前半段做补偿，避免中高段被拉扯
        val factor = (1f - p).coerceIn(0f, 1f)

        return (p - 0.28f * p * factor * factor)
            .coerceIn(0f, 1f)- 0.12f
    }




    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()
}
