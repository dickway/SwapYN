package com.face.videoclips.widget

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri

import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils

import com.face.R
import com.face.adapter.other.FramerateAdapter
import com.face.adapter.other.VideoSingAdapter
import com.face.bean.TargetImgBean
import com.face.util.SPUtils
import com.face.videoclips.interfaces.IFramerateView
import com.face.videoclips.interfaces.IVideoTrimmerView
import com.face.videoclips.trim.VideoProgressCoroutine
import com.face.videoclips.trim.VideoTrimmerUtil
import com.face.videoclips.trim.VideoTrimmerUtil.MAX_COUNT_RANGE
import com.face.videoclips.trim.VideoTrimmerUtil.RECYCLER_VIEW_PADDING

import com.face.videoclips.trim.VideoTrimmerUtil.shootVideoThumbInBackground
import com.face.videoclips.widget.SingSeekBarView.Thumb
import com.luck.picture.lib.config.PictureMimeType
import com.scwang.smart.refresh.layout.util.SmartUtil.dp2px
import com.zzkj.structure.util.ktx.dp

import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.toast

import kotlin.math.abs


class VideoFramerateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IVideoTrimmerView {
    private val mMaxWidth: Int = VideoTrimmerUtil.VIDEO_FRAMES_WIDTH
    private var mContext: Context? = null
    private var mVideoView: ZVideoView? = null
    private var mPlayView: ImageView? = null
    private var mVideoThumbRecyclerView: RecyclerView? = null
    private var mRangeSeekBarView: SingSeekBarView? = null
    private var mSeekBarLayout: LinearLayout? = null
//    private var mRedProgressIcon: ImageView? = null

    private var mAverageMsPx = 0f //每毫秒所占的px
    private var averagePxMs = 0f //每px所占用的ms毫秒
    private var mSourceUri: Uri? = null
    private var mDuration = 0
    private var mVideoThumbAdapter: VideoSingAdapter? = null
    private var framerateAdapter: FramerateAdapter? = null


    private var mLeftProgressPos: Long = 0
    private var mRightProgressPos: Long = 0
    private var scrollPos: Long = 0
    private val mScaledTouchSlop = 0
    private var lastScrollX = 0
    private var isSeeking = false
    private var isOverScaledTouchSlop = false

    var mThumbsTotalCount = 0
    var maxShootDuration = 3000 * 1000L //视频最多多长时间180s
    var listScreenshot: RecyclerView? = null
    var conImg: ConstraintLayout? = null
    var imgOpen: ImageView? = null
    var tvNum: TextView? = null
    var finishBtn: TextView? = null

    var IFramerateView: IFramerateView? = null
    var postImg = mutableListOf(TargetImgBean(name = "add"))
    var hintClick: TextView? = null
    var jiao: ImageView? = null

    var isDrag: Boolean = true

    var tracker: VideoProgressCoroutine? = null

    private fun init(context: Context) {
        this.mContext = context
        LayoutInflater.from(context).inflate(R.layout.video_framerate_view, this, true)

        hintClick = findViewById(R.id.hintClick)
        jiao = findViewById(R.id.jiao)
        if (SPUtils.levitateView) {
            SPUtils.levitateView = false
            jiao?.visibility = View.VISIBLE
            hintClick?.visibility = View.VISIBLE
        }

        listScreenshot = findViewById(R.id.listScreenshot)
        tvNum = findViewById(R.id.tvNum)
        finishBtn = findViewById(R.id.finishBtn)
        mVideoView = findViewById(R.id.video_loader)
        mPlayView = findViewById(R.id.icon_video_play)
        mSeekBarLayout = findViewById(R.id.seekBarLayout)
        mVideoThumbRecyclerView = findViewById(R.id.video_frames_recyclerView)
        mVideoThumbRecyclerView?.setLayoutManager(
            LinearLayoutManager(
                null,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        tracker = VideoProgressCoroutine(mVideoView) { pos ->
//            mLeftProgressPos = max(mLeftProgressPos, pos.toLong())
//            mRangeSeekBarView?.setStartEndTime(mLeftProgressPos, mRightProgressPos)
//
//           val vs= min(mLeftProgressPos/mDuration.toDouble(),0.92)
//            LogUtils.d("当前播放进度pos：$pos   mLeftProgressPos:$mLeftProgressPos  vs:$vs")
//            mRangeSeekBarView?.setNormalizedMinValue(vs)
        }

        mVideoThumbAdapter = VideoSingAdapter()
        mVideoThumbRecyclerView?.setAdapter(mVideoThumbAdapter)
        mVideoThumbRecyclerView?.addOnScrollListener(mOnScrollListener)

        framerateAdapter = FramerateAdapter()
        listScreenshot?.adapter = framerateAdapter
        sxNum()
        setUpListeners()
    }

    private fun initRangeSeekBarView() {
        if (mRangeSeekBarView != null) return
        mLeftProgressPos = 0

        mThumbsTotalCount = MAX_COUNT_RANGE
        mRightProgressPos = mDuration.toLong()

        mRangeSeekBarView = SingSeekBarView(mContext, mLeftProgressPos, mRightProgressPos)
        mRangeSeekBarView?.selectedMinValue = mLeftProgressPos
        mRangeSeekBarView?.selectedMaxValue = mRightProgressPos
        mRangeSeekBarView?.setStartEndTime(mLeftProgressPos, mRightProgressPos)
        mRangeSeekBarView?.setMinShootTime(0)
        mRangeSeekBarView?.isNotifyWhileDragging = true
        mRangeSeekBarView?.setOnSingSeekBarChangeListener(mOnSingSeekBarChangeListener)
        mSeekBarLayout?.addView(mRangeSeekBarView)

        if (mThumbsTotalCount - MAX_COUNT_RANGE > 0) {
            mAverageMsPx =
                (mDuration - maxShootDuration).toFloat() / (mThumbsTotalCount - MAX_COUNT_RANGE)
        } else {
            mAverageMsPx = 0f
        }
        averagePxMs = ((mMaxWidth) * 1.0f / (mRightProgressPos - mLeftProgressPos))
    }

    fun initVideoByURI(videoURI: Uri) {
        mSourceUri = videoURI
        mVideoView?.setVideoURI(videoURI)
        mVideoView?.requestFocus()
    }

    fun initImg(view: ConstraintLayout, img: ImageView, i: IFramerateView) {
        conImg = view
        imgOpen = img
        IFramerateView = i
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
        ) { bitmap: Bitmap?, interval: Int ->
            if (bitmap != null) {
                mVideoThumbAdapter?.addBitmaps(bitmap, interval)
            }
        }
    }


    private fun videoPrepared() {
        seekTo(mLeftProgressPos)
        val lp = mVideoView?.layoutParams
        lp?.width = getScreenWidth()

        mVideoView?.layoutParams = lp
        mDuration = mVideoView?.duration ?: 0
        IFramerateView?.onTimeOut(mDuration)
//        if (!restoreState) {
//            seekTo((mRedProgressBarPos.toInt()).toLong())
//        } else {
//            restoreState = false
//            seekTo((mRedProgressBarPos.toInt()).toLong())
//        }
        initRangeSeekBarView()
        startShootVideoThumbs(mContext, mSourceUri, mThumbsTotalCount, 0, mDuration.toLong())
    }

    private fun videoCompleted() {
        seekTo(mLeftProgressPos)
//        setPlayPauseViewIcon(false)
        mVideoView?.start()
    }

    private fun onVideoReset() {
        mVideoView?.pause()
        setPlayPauseViewIcon(false)
    }

    private fun playVideoOrPause() {
        isDrag = false
        if (mVideoView?.isPlaying == true) {
            mVideoView?.pause()
            tracker?.stop()
        } else {
            mVideoView?.start()
            tracker?.start()
        }
        setPlayPauseViewIcon(mVideoView?.isPlaying)
    }

    fun onVideoPause() {
        if (mVideoView?.isPlaying == true) {
            tracker?.stop()
            seekTo(mLeftProgressPos) //复位
            mVideoView?.pause()
            setPlayPauseViewIcon(false)
        }
    }

    private fun setUpListeners() {
        framerateAdapter?.onItemClick = { view, bean, _ ->
            jiao?.visibility = View.GONE
            hintClick?.visibility = View.GONE
            if (bean?.name == "add") {
                saveBitmap()
            } else {
                imgOpen?.setImageBitmap(bean?.mBitmap)
                conImg?.visibility = View.VISIBLE
            }
        }
        framerateAdapter?.onDeleteClick = { _, bean ->
            postImg.remove(bean)
            if (postImg.last().name != "add") {
                postImg.add(TargetImgBean(name = "add"))
            }
            sxNum()
        }
        finishBtn?.singleClick {
            val list = postImg.filter { it.name != "add" }
            IFramerateView?.onData(list)
        }
        mVideoView?.setOnPreparedListener { mp ->
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            videoPrepared()
        }
        mVideoView?.setOnCompletionListener {
//            videoCompleted()
            tracker?.stop()
            mLeftProgressPos = 0
            seekTo(mLeftProgressPos) //复位
            setPlayPauseViewIcon(false)
        }


        mPlayView?.setOnClickListener { playVideoOrPause() }
    }


    fun saveBitmap() {
        try {
            val msec = mVideoView?.currentPosition ?: 0
            val mediaMetadataRetriever = MediaMetadataRetriever()
            if (PictureMimeType.isContent(mSourceUri?.path)) {
                mediaMetadataRetriever.setDataSource(context, mSourceUri)
            } else {
                mediaMetadataRetriever.setDataSource(mSourceUri?.path)
            }

            val mediaOption = if (!isDrag || mVideoView?.isPlaying == true) {
                MediaMetadataRetriever.OPTION_CLOSEST//播放时需要取精确值
            } else {
                MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
            }

            val bitmap = mediaMetadataRetriever.getFrameAtTime(
                msec * 1000L,
                mediaOption
            )

            if (postImg.size < 4) postImg.add(0, TargetImgBean("img", mBitmap = bitmap))
            if (postImg.size > 3) postImg.removeIf { it.name == "add" }
            mediaMetadataRetriever.release()
        } catch (e: Throwable) {
            toast("${e.message}")
        }
        sxNum()
    }

    fun sxNum() {
        val num = postImg.filter { it.name != "add" }.size
        tvNum?.text = String.format(
            resources.getString(R.string.screenshot),
            num.toString()
        )
        finishBtn?.isSelected = num > 0
        framerateAdapter?.submitList(postImg)
    }


    private fun seekTo(msec: Long) {
        mVideoView?.seekTo(msec.toInt())
    }

    private fun setPlayPauseViewIcon(isPlaying: Boolean?) {
        mPlayView?.setImageResource(if (isPlaying == true) com.key.R.drawable.img_icon_pause else com.key.R.drawable.img_icon_play)
    }

    private val mOnSingSeekBarChangeListener: SingSeekBarView.OnSingSeekBarChangeListener =
        object : SingSeekBarView.OnSingSeekBarChangeListener {
            override fun onSingSeekBarValuesChanged(
                bar: SingSeekBarView?,
                minValue: Long,
                maxValue: Long,
                action: Int,
                isMin: Boolean,
                pressedThumb: Thumb?
            ) {
                isDrag = true
                mLeftProgressPos = minValue + scrollPos
                when (action) {
                    MotionEvent.ACTION_DOWN -> isSeeking = false
                    MotionEvent.ACTION_MOVE -> {
                        isSeeking = true
                        seekTo(((mLeftProgressPos).toInt()).toLong())
                    }

                    MotionEvent.ACTION_UP -> {
                        isSeeking = false
                        seekTo((mLeftProgressPos.toInt()).toLong())
                    }

                    else -> {}
                }
                if (mLeftProgressPos > mDuration - 100) {//像素可能会有误差 这里修正1s
                    mLeftProgressPos = mDuration.toLong()
                }
                mRangeSeekBarView?.setStartEndTime(mLeftProgressPos, mRightProgressPos)
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

                } else {
                    isSeeking = true
                    scrollPos = (mAverageMsPx * firstItemPosition).toLong()
                    mLeftProgressPos = mRangeSeekBarView!!.selectedMinValue + scrollPos

                    if (mVideoView?.isPlaying == true) {
                        mVideoView?.pause()
                        setPlayPauseViewIcon(false)
                    }
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

    fun stopPlay() {
        mVideoView?.stopPlayback()
    }

    override fun onDestroy() {
        mVideoView?.stopPlayback()
    }


    init {
        init(context)
    }
}
