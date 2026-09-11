package com.face.videoclips.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.blankj.utilcode.util.LogUtils
import com.face.R
import com.face.videoclips.trim.VideoTrimmerUtil
import com.zzkj.structure.util.TimeUtil.convertSecondsToTime
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getScreenWidth
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SingSeekBarView : View {
    private var mActivePointerId = INVALID_POINTER_ID
    private var mMinShootTime: Long = VideoTrimmerUtil.MIN_SHOOT_DURATION
    private var absoluteMinValuePrim = 0.0
    private var absoluteMaxValuePrim = 0.0
    private var normalizedMinValue = 0.0 //点坐标占总长度的比例值，范围从0-1
    private var normalizedMaxValue = 1.0 //点坐标占总长度的比例值，范围从0-1
    private var normalizedMinValueTime = 0.0
    private var normalizedMaxValueTime = 1.0 // normalized：规格化的--点坐标占总长度的比例值，范围从0-1
    private var mScaledTouchSlop = 0
    private var thumbImageLeft: Bitmap? = null
//    private var thumbImageRight: Bitmap? = null
    private var paint: Paint? = null
//    private var rectPaint: Paint? = null
    private val mVideoTrimTimePaintL = Paint()
    private val mShadow = Paint()
    private var thumbWidth = 0
    private var thumbHalfWidth = 0f
    private val padding = 0f
    private var mStartPosition: Long = 0
    private var mEndPosition: Long = 0
    private val thumbPaddingTop = 0f
    private var isTouchDown = false
    private var mDownMotionX = 0f
    private var mIsDragging = false
    private var pressedThumb: Thumb? = null
    private var isMin = false
    private var min_width = 1.0 //最小裁剪距离
    private var isTouch = false
    /**
     * 供外部activity调用，控制是都在拖动的时候打印log信息，默认是false不打印
     */
    var isNotifyWhileDragging = false
    private var mSingSeekBarChangeListener: OnSingSeekBarChangeListener? = null
    private val whiteColorRes =getColorX(R.color.colorWhite)
    enum class Thumb {
        MIN}

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, absoluteMinValuePrim: Long, absoluteMaxValuePrim: Long) : super(
        context
    ) {
        this.absoluteMinValuePrim = absoluteMinValuePrim.toDouble()
        this.absoluteMaxValuePrim = absoluteMaxValuePrim.toDouble()
        isFocusable = true
        setFocusableInTouchMode(true)
        init()
    }

    private fun init() {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        thumbImageLeft = BitmapFactory.decodeResource(resources, com.key.R.drawable.img_icon_st5)
        val newWidth: Int = 10.dp
        thumbWidth = newWidth
        thumbHalfWidth = (thumbWidth / 2).toFloat()
        val shadowColor = context.resources.getColor(R.color.colorTransparent)
        mShadow.isAntiAlias = true
        mShadow.setColor(shadowColor)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        mVideoTrimTimePaintL.strokeWidth = 3f
        mVideoTrimTimePaintL.setARGB(255, 51, 51, 51)
        mVideoTrimTimePaintL.textSize = 10.dp.toFloat()
        mVideoTrimTimePaintL.isAntiAlias = true
        mVideoTrimTimePaintL.setColor(whiteColorRes)
        mVideoTrimTimePaintL.textAlign = Paint.Align.LEFT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 300
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        var height = 120
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec)
        }
        setMeasuredDimension(width, height)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rangeL = normalizedToScreen(normalizedMinValue)
        val leftRect = Rect(0, height, rangeL.toInt(), 0)
        canvas.drawRect(leftRect, mShadow)
        drawThumb(normalizedToScreen(normalizedMinValue), false, canvas, true)
        drawVideoTrimTimeText(canvas)
    }

    private fun drawThumb(screenCoord: Float, pressed: Boolean, canvas: Canvas, isLeft: Boolean) {
        canvas.drawBitmap(
            thumbImageLeft!!,
            screenCoord-5.dp,
//            screenCoord - if (isLeft) 0 else thumbWidth,
            Companion.paddingTop.toFloat(),
            paint
        )
    }

    private fun drawVideoTrimTimeText(canvas: Canvas) {
        val leftThumbsTime = convertSecondsToTime(mStartPosition)
        var x= normalizedToScreen(normalizedMinValue)
        if (x>(getScreenWidth() -50.dp).toFloat()){//拖动到底后文字不超出屏幕
            x= (getScreenWidth() -50.dp).toFloat()
        }
        canvas.drawText(
            leftThumbsTime,
            x,
            TextPositionY.toFloat(),
            mVideoTrimTimePaintL
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isTouchDown) {
            return super.onTouchEvent(event)
        }
        if (event.pointerCount > 1) {
            return super.onTouchEvent(event)
        }
        if (!isEnabled) return false
        if (absoluteMaxValuePrim <= mMinShootTime) {
            return super.onTouchEvent(event)
        }

        if (!isTouch){
            isTouch=true
//            thumbImageLeft = BitmapFactory.decodeResource(resources, R.drawable.img_video_thumb_l)
//            thumbImageRight = BitmapFactory.decodeResource(resources, R.drawable.img_video_thumb_r)
//            rectPaint!!.setColor(whiteColorLine)
        }

        val pointerIndex: Int // 记录点击点的index
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //记住最后一个手指点击屏幕的点的坐标x，mDownMotionX
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                mDownMotionX = event.getX(pointerIndex)
                // 判断touch到的是最大值thumb还是最小值thumb
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null) return super.onTouchEvent(event)
                setPressed(true) // 设置该控件被按下了
                onStartTrackingTouch() // 置mIsDragging为true，开始追踪touch事件
                trackTouchEvent(event)
                attemptClaimDrag()
                if (mSingSeekBarChangeListener != null) {
                    mSingSeekBarChangeListener!!.onSingSeekBarValuesChanged(
                        this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_DOWN, isMin,
                        pressedThumb
                    )
                }
            }

            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    // Scroll to follow the motion event
                    pointerIndex = event.findPointerIndex(mActivePointerId)
                    val x = event.getX(pointerIndex) // 手指在控件上点的X坐标
                    // 手指没有点在最大最小值上，并且在控件上有滑动事件
                    if (abs((x - mDownMotionX).toDouble()) > mScaledTouchSlop) {
                        setPressed(true)
                        invalidate()
                        onStartTrackingTouch()
                        trackTouchEvent(event)
                        attemptClaimDrag()
                    }
                }
                if (isNotifyWhileDragging && mSingSeekBarChangeListener != null) {
                    mSingSeekBarChangeListener!!.onSingSeekBarValuesChanged(
                        this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_MOVE,
                        isMin, pressedThumb
                    )
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    setPressed(false)
                } else {
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                invalidate()
                if (mSingSeekBarChangeListener != null) {
                    mSingSeekBarChangeListener!!.onSingSeekBarValuesChanged(
                        this, selectedMinValue, selectedMaxValue, MotionEvent.ACTION_UP, isMin,
                        pressedThumb
                    )
                }
                pressedThumb = null // 手指抬起，则置被touch到的thumb为空
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.pointerCount - 1
                // final int index = ev.getActionIndex();
                mDownMotionX = event.getX(index)
                mActivePointerId = event.getPointerId(index)
                invalidate()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(event)
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    setPressed(false)
                }
                invalidate() // see above explanation
            }

            else -> {}
        }
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.action and ACTION_POINTER_INDEX_MASK shr ACTION_POINTER_INDEX_SHIFT
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mDownMotionX = ev.getX(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    private fun trackTouchEvent(event: MotionEvent) {
        if (event.pointerCount > 1) return
        val pointerIndex = event.findPointerIndex(mActivePointerId) // 得到按下点的index
        var x = 0f
        x = try {
            event.getX(pointerIndex)
        } catch (e: Exception) {
            return
        }
        if (Thumb.MIN == pressedThumb) {
            // screenToNormalized(x)-->得到规格化的0-1的值
            setNormalizedMinValue(screenToNormalized(x, 0))
        }
//        else if (Thumb.MAX == pressedThumb) {
//            setNormalizedMaxValue(screenToNormalized(x, 1))
//        }
    }

    private fun screenToNormalized(screenCoord: Float, position: Int): Double {
        val width = width
        return if (width <= 2 * padding) {
            // prevent division by zero, simply return 0.
            0.0
        } else {
            isMin = false
            var current_width = screenCoord.toDouble()
            val rangeL = normalizedToScreen(normalizedMinValue)
            val min =
                mMinShootTime / (absoluteMaxValuePrim - absoluteMinValuePrim) * (width - thumbWidth)
            min_width = if (absoluteMaxValuePrim > 5 * 60 * 1000) { //大于5分钟的精确小数四位
                val dfUS = DecimalFormat.getInstance(Locale.CHINESE) as DecimalFormat
                dfUS.applyPattern("0.0000");
                dfUS.format(min).toDouble()
            } else {
                Math.round(min + 0.5).toDouble()
            }
            if (position == 0) {
                if (isInThumbRangeLeft(screenCoord, normalizedMinValue, 0.5)) {
                    return normalizedMinValue
                }
                val rightPosition: Float = 0F
                val left_length = valueLength - (rightPosition + min_width)
                if (current_width > rangeL) {
                    current_width = rangeL + (current_width - rangeL)
                } else if (current_width <= rangeL) {
                    current_width = rangeL - (rangeL - current_width)
                }
                if (current_width > left_length) {
                    isMin = true
                    current_width = left_length
                }
                if (current_width < thumbWidth / 3) {
                    current_width = 0.0
                }
                val resultTime = (current_width - padding) / (width - thumbWidth)
                normalizedMinValueTime = min(1.0, max(0.0, resultTime))
                val result = (current_width - padding) / (width - 2 * padding)
                min(1.0, max(0.0, result)) // 保证该该值为0-1之间，但是什么时候这个判断有用呢？
            } else {
                if (isInThumbRange(screenCoord, normalizedMaxValue, 0.5)) {
                    return normalizedMaxValue
                }
                var paddingRight = getWidth() - current_width
                if (paddingRight < thumbWidth / 3) {
                    current_width = getWidth().toDouble()
                    paddingRight = 0.0
                }
                var resultTime = (paddingRight - padding) / (width -  thumbWidth)
                resultTime = 1 - resultTime
                normalizedMaxValueTime = min(1.0, max(0.0, resultTime))
                val result = (current_width - padding) / (width - 2 * padding)
                min(1.0, max(0.0, result)) // 保证该该值为0-1之间，但是什么时候这个判断有用呢？
            }
        }
    }

    private val valueLength: Int
        private get() = width - 1 * thumbWidth

    /**
     * 计算位于哪个Thumb内
     *
     * @param touchX touchX
     * @return 被touch的是空还是最大值或最小值
     */
    private fun evalPressedThumb(touchX: Float): Thumb? {
//        var result: Thumb? = null
//        val minThumbPressed = isInThumbRange(touchX, normalizedMinValue, 2.0) // 触摸点是否在最小值图片范围内
//        val maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue, 2.0)
//        if (minThumbPressed && maxThumbPressed) {
//            // 如果两个thumbs重叠在一起，无法判断拖动哪个，做以下处理
//            // 触摸点在屏幕右侧，则判断为touch到了最小值thumb，反之判断为touch到了最大值thumb
//            result = Thumb.MIN
//        } else if (minThumbPressed) {
//            result = Thumb.MIN
//        } else if (maxThumbPressed) {
//            result = Thumb.MAX
//        }
        return Thumb.MIN
    }

    private fun isInThumbRange(
        touchX: Float,
        normalizedThumbValue: Double,
        scale: Double
    ): Boolean {
        // 当前触摸点X坐标-最小值图片中心点在屏幕的X坐标之差<=最小点图片的宽度的一般
        // 即判断触摸点是否在以最小值图片中心为原点，宽度一半为半径的圆内。
        return abs((touchX - normalizedToScreen(normalizedThumbValue)).toDouble()) <= thumbHalfWidth * scale
    }

    private fun isInThumbRangeLeft(
        touchX: Float,
        normalizedThumbValue: Double,
        scale: Double
    ): Boolean {
        // 当前触摸点X坐标-最小值图片中心点在屏幕的X坐标之差<=最小点图片的宽度的一般
        // 即判断触摸点是否在以最小值图片中心为原点，宽度一半为半径的圆内。
        return abs((touchX - normalizedToScreen(normalizedThumbValue) - thumbWidth).toDouble()) <= thumbHalfWidth * scale
    }

    /**
     * 试图告诉父view不要拦截子控件的drag
     */
    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    fun onStartTrackingTouch() {
        mIsDragging = true
    }

    fun onStopTrackingTouch() {
        mIsDragging = false
    }

    fun setMinShootTime(min_cut_time: Long) {
        mMinShootTime = min_cut_time
    }

    private fun normalizedToScreen(normalizedCoord: Double): Float {
        return (getPaddingLeft() + normalizedCoord * (width - getPaddingLeft() - getPaddingRight())).toFloat()
    }

    private fun valueToNormalized(value: Long): Double {
        return if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            0.0
        } else (value - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim)
    }

    fun setStartEndTime(start: Long, end: Long) {
        mStartPosition = start / 1000
        mEndPosition = end / 1000
    }

//    fun setNormalizedValue(value: Double) {
//        setNormalizedMinValue(screenToNormalized(value, 0))
//    }

    fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = max(0.0, min(1.0, min(value, normalizedMaxValue)))
        invalidate() // 重新绘制此view
    }

    fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = max(0.0, min(1.0, max(value, normalizedMinValue)))
        invalidate() // 重新绘制此view
    }

    var selectedMinValue: Long
        get() = normalizedToValue(normalizedMinValueTime)
        set(value) {
            if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
                setNormalizedMinValue(0.0)
            } else {
                setNormalizedMinValue(valueToNormalized(value))
            }
        }
    var selectedMaxValue: Long
        get() = normalizedToValue(normalizedMaxValueTime)
        set(value) {
            if (0.0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
                setNormalizedMaxValue(1.0)
            } else {
//                setNormalizedMaxValue(valueToNormalized(value))
            }
        }

    private fun normalizedToValue(normalized: Double): Long {
        return (absoluteMinValuePrim + normalized * (absoluteMaxValuePrim - absoluteMinValuePrim)).toLong()
    }

    fun setTouchDown(touchDown: Boolean) {
        isTouchDown = touchDown
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("SUPER", super.onSaveInstanceState())
        bundle.putDouble("MIN", normalizedMinValue)
        bundle.putDouble("MAX", normalizedMaxValue)
        bundle.putDouble("MIN_TIME", normalizedMinValueTime)
        bundle.putDouble("MAX_TIME", normalizedMaxValueTime)
        return bundle
    }

    override fun onRestoreInstanceState(parcel: Parcelable) {
        val bundle = parcel as Bundle
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"))
        normalizedMinValue = bundle.getDouble("MIN")
        normalizedMaxValue = bundle.getDouble("MAX")
        normalizedMinValueTime = bundle.getDouble("MIN_TIME")
        normalizedMaxValueTime = bundle.getDouble("MAX_TIME")
    }

    interface OnSingSeekBarChangeListener {
        fun onSingSeekBarValuesChanged(
            bar: SingSeekBarView?,
            minValue: Long,
            maxValue: Long,
            action: Int,
            isMin: Boolean,
            pressedThumb: Thumb?
        )
    }

    fun setOnSingSeekBarChangeListener(listener: OnSingSeekBarChangeListener?) {
        mSingSeekBarChangeListener = listener
    }

    companion object {
        private val TAG = SingSeekBarView::class.java.getSimpleName()
        const val INVALID_POINTER_ID = 255
        const val ACTION_POINTER_INDEX_MASK = 0x0000ff00
        const val ACTION_POINTER_INDEX_SHIFT = 8
        private val TextPositionY: Int = 14.dp
        private val paddingTop: Int = 10.dp
    }
}
