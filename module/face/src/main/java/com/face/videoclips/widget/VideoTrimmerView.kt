package com.face.videoclips.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.face.R
import com.face.adapter.other.VideoClipsAdapter
import com.face.util.EventUtil.videoPath
import com.face.videoclips.interfaces.IVideoTrimmerView
import com.face.videoclips.interfaces.VideoTrimListener
import com.face.videoclips.trim.VideoTrimmerUtil
import com.face.videoclips.trim.VideoTrimmerUtil.MAX_COUNT_RANGE
import com.face.videoclips.trim.VideoTrimmerUtil.MAX_SHOOT_DURATION
import com.face.videoclips.trim.VideoTrimmerUtil.RECYCLER_VIEW_PADDING
import com.face.videoclips.trim.VideoTrimmerUtil.VIDEO_FRAMES_WIDTH
import com.face.videoclips.trim.VideoTrimmerUtil.getVideoCodec
import com.face.videoclips.trim.VideoTrimmerUtil.shootVideoThumbInBackground
import com.face.videoclips.trim.VideoTrimmerUtil.trimVideo
import com.face.videoclips.widget.RangeSeekBarView.OnRangeSeekBarChangeListener
import com.face.videoclips.widget.RangeSeekBarView.Thumb
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.toast
import java.io.File
import kotlin.math.abs

class VideoTrimmerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IVideoTrimmerView {
    private val mMaxWidth: Int = VIDEO_FRAMES_WIDTH
    private var mContext: Context? = null
    private var mVideoView: ZVideoView? = null
    private var mPlayView: ImageView? = null
    private var mVideoThumbRecyclerView: RecyclerView? = null
    private var mRangeSeekBarView: RangeSeekBarView? = null
    private var mSeekBarLayout: LinearLayout? = null
    private var mRedProgressIcon: ImageView? = null
    private var mVideoShootTipTv: TextView? = null
    private var mAverageMsPx = 0f //一张图片所占毫秒
    private var averagePxMs = 0f //每px所占用的ms毫秒
    private var mSourceUri: Uri? = null
    private var mOnTrimVideoListener: VideoTrimListener? = null
    private var mDuration = 0
    private var mVideoThumbAdapter: VideoClipsAdapter? = null

    //new
    private var mLeftProgressPos: Long = 0
    private var mRightProgressPos: Long = 0
    private var mRedProgressBarPos: Long = 0
    private var scrollPos: Long = 0
    private val mScaledTouchSlop = 0
    private var lastScrollX = 0
    private var isSeeking = false
    private var isOverScaledTouchSlop = false
    private var mThumbsTotalCount = 0
    private var mRedProgressAnimator: ValueAnimator? = null
    private val mAnimationHandler = Handler()

    private var isFrist = true

    private fun init(context: Context) {
        this.mContext = context
        isFrist = true
        LayoutInflater.from(context).inflate(R.layout.video_trimmer_view, this, true)

        mVideoView = findViewById(R.id.video_loader)
        mPlayView = findViewById(R.id.icon_video_play)
        mSeekBarLayout = findViewById(R.id.seekBarLayout)
        mRedProgressIcon = findViewById(R.id.positionIcon)
        mVideoShootTipTv = findViewById(R.id.video_shoot_tip)
        mVideoThumbRecyclerView = findViewById(R.id.video_frames_recyclerView)
        mVideoThumbRecyclerView?.setLayoutManager(
            LinearLayoutManager(
                mContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        mVideoThumbAdapter = VideoClipsAdapter()
        mVideoThumbRecyclerView?.setAdapter(mVideoThumbAdapter)
        mVideoThumbRecyclerView?.addOnScrollListener(mOnScrollListener)

        setUpListeners()
    }

    private fun initRangeSeekBarView() {
        if (mRangeSeekBarView != null) return
        mLeftProgressPos = 0
        if (mDuration <= MAX_SHOOT_DURATION) {
            mThumbsTotalCount = MAX_COUNT_RANGE
            mRightProgressPos = mDuration.toLong()
        } else {
            mThumbsTotalCount =
                (mDuration * 1.0f * MAX_COUNT_RANGE / (MAX_SHOOT_DURATION * 1.0f)).toInt()
            mRightProgressPos = MAX_SHOOT_DURATION
        }
        //        mVideoThumbRecyclerView.addItemDecoration(new SpacesItemDecoration(RECYCLER_VIEW_PADDING, mThumbsTotalCount));
        mRangeSeekBarView = RangeSeekBarView(mContext, mLeftProgressPos, mRightProgressPos)
        mRangeSeekBarView!!.selectedMinValue = mLeftProgressPos
        mRangeSeekBarView!!.selectedMaxValue = mRightProgressPos
        mRangeSeekBarView!!.setStartEndTime(mLeftProgressPos, mRightProgressPos)
        mRangeSeekBarView!!.setMinShootTime(VideoTrimmerUtil.MIN_SHOOT_DURATION)
        mRangeSeekBarView!!.isNotifyWhileDragging = true
        mRangeSeekBarView!!.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener)
        mSeekBarLayout!!.addView(mRangeSeekBarView)
        if (mThumbsTotalCount - MAX_COUNT_RANGE > 0) {
            mAverageMsPx =
                (mDuration - MAX_SHOOT_DURATION).toFloat() / (mThumbsTotalCount - MAX_COUNT_RANGE)
        } else {
            mAverageMsPx = 0f
        }
        averagePxMs = ((mMaxWidth) * 1.0f / (mRightProgressPos - mLeftProgressPos))
    }

    fun initVideoByURI(videoURI: Uri?) {
        mSourceUri = videoURI
        mVideoView!!.setVideoURI((videoURI)!!)
        mVideoView!!.requestFocus()
        mVideoShootTipTv!!.text = mContext!!.resources.getString(R.string.video_shoot_tip)
    }

    private fun startShootVideoThumbs(
        context: Context?,
        videoUri: Uri?,
        totalThumbsCount: Int,
        startPosition: Long,
        endPosition: Long
    ) {
        shootVideoThumbInBackground(
            context, videoUri, totalThumbsCount.toFloat(), startPosition, endPosition
        ) { bitmap: Bitmap?, frameTime: Int ->
            if (bitmap != null) {
                mVideoThumbAdapter?.addBitmaps(bitmap=bitmap)
            }
        }
    }

    private fun onCancelClicked() {
        mOnTrimVideoListener!!.onCancel()
    }

    private fun videoPrepared(mp: MediaPlayer) {
        val lp = mVideoView!!.layoutParams
        lp.width = getScreenWidth()

        mVideoView!!.layoutParams = lp
        mDuration = mVideoView!!.duration
        seekTo((mRedProgressBarPos.toInt()).toLong())
        initRangeSeekBarView()
        if (isFrist) {
            isFrist = false
            startShootVideoThumbs(mContext, mSourceUri, mThumbsTotalCount, 0, mDuration.toLong())
        }
    }

    private fun videoCompleted() {
        seekTo(mLeftProgressPos)
        setPlayPauseViewIcon(false)
    }

    private fun onVideoReset() {
        mVideoView!!.pause()
        setPlayPauseViewIcon(false)
    }

    private fun playVideoOrPause() {
        mRedProgressBarPos = mVideoView!!.currentPosition.toLong()
        if (mVideoView!!.isPlaying) {
            mVideoView!!.pause()
            pauseRedProgressAnimation()
        } else {
            mVideoView!!.start()
            playingRedProgressAnimation()
        }
        setPlayPauseViewIcon(mVideoView!!.isPlaying)
    }

    private fun playingRedProgressAnimation() {
        pauseRedProgressAnimation()
        playingAnimation()
        mAnimationHandler.post(mAnimationRunnable)
    }

    private fun playingAnimation() {
        if (mRedProgressIcon!!.visibility == GONE) {
            mRedProgressIcon!!.visibility = VISIBLE
        }
        val params = mRedProgressIcon!!.layoutParams as LayoutParams
        val start: Int =
            (RECYCLER_VIEW_PADDING + (mRedProgressBarPos - scrollPos) * averagePxMs).toInt()
        val end: Int =
            (RECYCLER_VIEW_PADDING + (mRightProgressPos - scrollPos) * averagePxMs).toInt()
        mRedProgressAnimator = ValueAnimator.ofInt(start, end)
            .setDuration((mRightProgressPos - scrollPos) - (mRedProgressBarPos - scrollPos))
        mRedProgressAnimator?.interpolator = LinearInterpolator()
        mRedProgressAnimator?.addUpdateListener { animation ->
            params.leftMargin = animation.animatedValue as Int
            mRedProgressIcon?.layoutParams = params
        }
        mRedProgressAnimator?.start()
    }

    fun onVideoPause() {
        if (mVideoView?.isPlaying == true) {
            seekTo(mLeftProgressPos) //复位
            mVideoView?.pause()
            setPlayPauseViewIcon(false)
            mRedProgressIcon?.visibility = GONE
        }
    }


    fun setOnTrimVideoListener(onTrimVideoListener: VideoTrimListener?) {
        mOnTrimVideoListener = onTrimVideoListener
    }

    private fun setUpListeners() {
        findViewById<View>(R.id.cancelBtn).setOnClickListener { onCancelClicked() }

        findViewById<View>(R.id.finishBtn).setOnClickListener { onSaveClicked() }
        mVideoView?.setOnPreparedListener { mp ->
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            videoPrepared(mp)
        }
        mVideoView?.setOnCompletionListener { videoCompleted() }
        mPlayView?.setOnClickListener { playVideoOrPause() }
    }

    private fun onSaveClicked() {
        if (mVideoView?.isPlaying == true) {
            mVideoView?.pause()
            pauseRedProgressAnimation()
            setPlayPauseViewIcon(mVideoView?.isPlaying)
        }
        if (mRightProgressPos - mLeftProgressPos < VideoTrimmerUtil.MIN_SHOOT_DURATION) {
            toast(mContext?.getString(R.string.video_hint))
        } else {
            mVideoView?.pause()
            mVideoView?.pause()
            val file = File(mSourceUri?.path ?: "")  // 替换为实际的文件路径
            var fileSizeInMB = 0f
            if (file.exists()) {
                val fileSizeInBytes = file.length()  // 获取文件大小，单位是字节
                fileSizeInMB = (fileSizeInBytes.toDouble() / (1024 * 1024)).toFloat() // 转换为MB
            }
            val videoInfo =
                "SIZE:" + fileSizeInMB + "----" +
                "PATH:" + mSourceUri?.path + "----" +
                "DECODE:" + getVideoCodec(mSourceUri?.path)

            videoPath(videoInfo, 1)
            trimVideo(
                (mContext)!!,
                (mSourceUri)!!,
                mLeftProgressPos,
                mRightProgressPos,
                mOnTrimVideoListener
            )
        }
    }


    private fun seekTo(msec: Long) {
        mVideoView?.seekTo(msec.toInt())
    }

    private fun setPlayPauseViewIcon(isPlaying: Boolean?) {
        mPlayView?.setImageResource(if (isPlaying == true) com.key.R.drawable.img_icon_pause else com.key.R.drawable.img_icon_play)
    }

    private val mOnRangeSeekBarChangeListener: OnRangeSeekBarChangeListener =
        object : OnRangeSeekBarChangeListener {
            override fun onRangeSeekBarValuesChanged(
                bar: RangeSeekBarView?, minValue: Long, maxValue: Long, action: Int, isMin: Boolean,
                pressedThumb: Thumb?
            ) {
                mLeftProgressPos = minValue + scrollPos
                mRedProgressBarPos = mLeftProgressPos
                mRightProgressPos = maxValue + scrollPos
                when (action) {
                    MotionEvent.ACTION_DOWN -> isSeeking = false
                    MotionEvent.ACTION_MOVE -> {
                        isSeeking = true
                        seekTo(((if (pressedThumb == Thumb.MIN) mLeftProgressPos else mRightProgressPos).toInt()).toLong())
                    }

                    MotionEvent.ACTION_UP -> {
                        isSeeking = false
                        seekTo((mLeftProgressPos.toInt()).toLong())
                    }

                    else -> {}
                }
                mRangeSeekBarView!!.setStartEndTime(mLeftProgressPos, mRightProgressPos)
            }
        }

    private val mOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isSeeking = false

                val firstItemPosition = calcDistance()

                val scrollX = calcScrollXDistance()

                //达不到滑动的距离
                if (abs((lastScrollX - scrollX).toDouble()) < mScaledTouchSlop) {
                    isOverScaledTouchSlop = false
                    return
                }
                isOverScaledTouchSlop = true
                //初始状态,why ? 因为默认的时候有空白！
                if (scrollX == -RECYCLER_VIEW_PADDING) {
                    scrollPos = 0
                    mLeftProgressPos = mRangeSeekBarView!!.selectedMinValue + scrollPos
                    mRightProgressPos = mRangeSeekBarView!!.selectedMaxValue + scrollPos
                    mRedProgressBarPos = mLeftProgressPos
                } else {
                    isSeeking = true
                    scrollPos = (mAverageMsPx * firstItemPosition).toLong()
                    mLeftProgressPos = mRangeSeekBarView!!.selectedMinValue + scrollPos
                    mRightProgressPos = mRangeSeekBarView!!.selectedMaxValue + scrollPos

                    LogUtils.e(
                        ">>>MaxValue:${mRangeSeekBarView!!.selectedMaxValue}" +
                                ">>>mRightProgressPos:$mRightProgressPos" +
                                ">>>scrollPos:$scrollPos"
                    )

                    mRedProgressBarPos = mLeftProgressPos
                    if (mVideoView?.isPlaying == true) {
                        mVideoView?.pause()
                        setPlayPauseViewIcon(false)
                    }
                    mRedProgressIcon?.visibility = GONE
                    seekTo(mLeftProgressPos)
                    mRangeSeekBarView?.setStartEndTime(mLeftProgressPos, mRightProgressPos)
                    mRangeSeekBarView?.invalidate()
                }
                lastScrollX = scrollX
            }
        }

    /**
     * 水平滑动了多少px
     */
    private fun calcScrollXDistance(): Int {
        val layoutManager = mVideoThumbRecyclerView?.layoutManager as LinearLayoutManager?
        val position = layoutManager!!.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemWidth = firstVisibleChildView!!.width
        return (position) * itemWidth - firstVisibleChildView.left
    }

    /**
     * 当前可见item
     */
    private fun calcDistance(): Float {
        val layoutManager = mVideoThumbRecyclerView?.layoutManager as LinearLayoutManager?
        val position = layoutManager!!.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val offsetY = firstVisibleChildView!!.left.toFloat()
        val itemWidth = firstVisibleChildView.width.toFloat()
        val its = abs((offsetY / itemWidth).toDouble()).toFloat()
        return position + its
    }

    private fun pauseRedProgressAnimation() {
        mRedProgressIcon?.clearAnimation()
        if (mRedProgressAnimator != null && mRedProgressAnimator!!.isRunning) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable)
            mRedProgressAnimator!!.cancel()
        }
    }

    private val mAnimationRunnable = Runnable { this.updateVideoProgress() }

    init {
        init(context)
    }

    private fun updateVideoProgress() {
        val currentPosition = mVideoView?.currentPosition?.toLong() ?: 0L
        if (currentPosition >= (mRightProgressPos)) {
            mRedProgressBarPos = mLeftProgressPos
            pauseRedProgressAnimation()
            onVideoPause()
        } else {
            mAnimationHandler.post(mAnimationRunnable)
        }
    }

    fun stopPlay() {
        mVideoView?.stopPlayback()
    }

    override fun onDestroy() {
        mVideoView?.stopPlayback()
    }
}
