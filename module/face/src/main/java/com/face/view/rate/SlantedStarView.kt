package com.face.view.rate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.PathParser
import androidx.core.graphics.withClip

class SlantedStarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    val STAR_PATH ="M19.525,2.024a1.816,1.816,0,0,1,2.892," +
            "0l6.6,8.689a1.816,1.816,0,0,0,.778.59l10.027," +
            "3.964a1.815,1.815,0,0,1,.871,2.653l-5.952," +
            "9.493a1.815,1.815,0,0,0-.276.886l-.489,11.271a1.816," +
            "1.816,0,0,1-2.319,1.665L21.476,38.288a1.816,1.816,0,0,0-1.01," +
            "0L10.285,41.234a1.816,1.816,0,0,1-2.319-1.665L7.477,28.3a1.815," +
            "1.815,0,0,0-.276-.886L1.249,17.92a1.815,1.815,0,0,1,.871-2.653L12.147," +
            "11.3a1.816,1.816,0,0,0,.778-.59Z"
//        "M12,17.27L18.18,21L16.54,13.97L22," +
//                "9.24L14.81,8.63L12,2L9.19," +
//                "8.63L2,9.24L7.46,13.97L5.82,21Z"

    /** 0f ~ 1f */
    var percent: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    /** 斜切强度 */
    var slantRatio = -0.35f

    private val starPath by lazy {
        PathParser.createPathFromPathData(STAR_PATH)
    }

    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4DFFFFFF")
        style = Paint.Style.FILL
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.FILL
    }

    private val drawPath = Path()
    private val clipPath = Path()
    private val bounds = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1️⃣ 计算 SVG 边界
        starPath.computeBounds(bounds, true)

        // 2️⃣ 缩放 SVG 到 View 大小
        drawPath.reset()
        drawPath.addPath(starPath)

        val matrix = Matrix().apply {
            setScale(
                width / bounds.width(),
                height / bounds.height()
            )
            postTranslate(-bounds.left, -bounds.top)
        }
        drawPath.transform(matrix)

        // 3️⃣ 画空星
        canvas.drawPath(drawPath, emptyPaint)

        if (percent <= 0f) return

        // 4️⃣ 构建斜切裁剪路径
        val w = width.toFloat()
        val h = height.toFloat()
        val px = w * percent

        clipPath.reset()
        clipPath.moveTo(0f, h)
        clipPath.lineTo(px, h)
        clipPath.lineTo(px - h * slantRatio, 0f)
        clipPath.lineTo(0f, 0f)
        clipPath.close()

        // 5️⃣ 斜切填充
        canvas.withClip(clipPath) {
            drawPath(drawPath, fillPaint)
        }
    }
}
