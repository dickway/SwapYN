package com.face.view.voicewaveview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.zzkj.structure.util.ktx.dp


class Wave @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val PAINT_W = 1.5f.dp.toFloat()
    }

    var rWidth = 1.5f.dp.toFloat()//矩形边框

    var voiceTime = 0//设置截取间隔时间

    var currentTime = 0//当前选中第几秒

    var totalTime = 0//音频总时间

    var singWidth = 0f//1秒占据多少宽度

    private var values: List<Float> = emptyList()

    fun setValues(sampleValues: List<Float>) {
        values = sampleValues
        requestLayout()
    }


    private val paint = Paint().apply {
        strokeWidth = PAINT_W
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#80FFFFFF")
        isAntiAlias = true
    }

    //实心部分
    private val rPaint1 = Paint().apply {
        color = Color.parseColor("#29FF3434")
        style = Paint.Style.FILL
    }

    //边框
    private val rPaint2 = Paint().apply {
        color = Color.parseColor("#FF3434")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = rWidth
    }

    private val redPaint = Paint().apply {
        color = Color.parseColor("#FF3434")
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeWidth = PAINT_W
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (values.isEmpty()) return

        if (singWidth < 1) singWidth = measuredWidth / totalTime.toFloat()
        val rectWith = singWidth * voiceTime

        var startWith = currentTime * singWidth
        if (currentTime >= totalTime - voiceTime && totalTime >= voiceTime) {//如果滑动的时间超过了最大截取时间就不移动了
            startWith = (totalTime - voiceTime) * singWidth - 6f
        }
        var endWith = startWith + rectWith
        if (totalTime <= voiceTime) {//时间小于截取时间，就全选中
            startWith = 3f
            endWith = totalTime * singWidth - 6f
        }

        var startX = 3f//边距
        canvas.save()
        canvas.translate(3f, height / 2f)
        values.forEach {
            val h = (it * height * 1.5).toFloat()
            val sy = -h / 7 * 3
            val ey = h / 7 * 3
            if (startX >= startWith && startX <= (startWith + rectWith)) {
                canvas.drawLine(startX, sy, startX, ey, redPaint)
            } else {
                canvas.drawLine(startX, sy, startX, ey, paint)
            }
            startX += (PAINT_W * 3.5).toFloat()
        }

        // 绘制实心矩形,指定矩形的四个角
        val rectF =
            RectF(startWith, -height.toFloat() / 2, endWith, height.toFloat() / 2)
        canvas.drawRoundRect(rectF, 3f, 3f, rPaint1)

        // 绘制边框,指定矩形的四个角
        val rectF2 =
            RectF(
                startWith,
                -height.toFloat() / 2 + rWidth / 2,
                endWith,
                height.toFloat() / 2 - rWidth / 2
            )
        canvas.drawRoundRect(rectF2, 3f, 3f, rPaint2)

        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = values.size * PAINT_W * 3.5
        val h = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(w.toInt(), h)
    }

    /**
     * 设置当前选中时间
     * @param currentNum
     */
    fun setCurrentProgress(currentNum: Int) {
        currentTime = currentNum
        postInvalidate()
    }
}


