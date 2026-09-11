package com.face.video

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.face.R
import com.flyjingfish.openimagefulllib.VideoFragmentCreateImpl
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.enums.MediaType
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge
import com.zzkj.structure.util.ImgLoader.loadImage
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

/**
 * 无任何控制ui的播放
 */
class CacheVideo : StandardGSYVideoPlayer, View.OnClickListener {
    var mCoverImage: ImageView? = null
    var tvFace: TextView? = null
    var mianContainer: RelativeLayout? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    override fun init(context: Context) {
        super.init(context)

        mCoverImage = findViewById(R.id.thumbImage)
        tvFace = findViewById(R.id.tvFace)
        mianContainer = findViewById(R.id.rl_video_player_root)

        if (mThumbImageViewLayout != null &&
            (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)
        ) {
            mThumbImageViewLayout.visibility = VISIBLE
        }
        onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange: Int ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> LogUtils.d("AUDIOFOCUS_GAIN")
                AudioManager.AUDIOFOCUS_LOSS -> LogUtils.d("AUDIOFOCUS_LOSS")
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> LogUtils.d("AUDIOFOCUS_LOSS_TRANSIENT")
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> LogUtils.d("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
            }
        }
    }

    override fun getGSYVideoManager(): GSYVideoViewBridge? {
        CustomManager.getCustomManager(Tag).initContext(context.applicationContext)
        return CustomManager.getCustomManager(Tag)
    }

    fun loadBgImage(url: String, res: Int, widths: Int, heights: Int) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
//        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
        tvFace?.visibility = VISIBLE
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
        mCoverImage?.apply {
            loadImage(
                url,
                placeholderResId = res
            )
//            if (url.contains("png")) {
//
//            } else {
//                Glide.with(BaseApp.INSTANCE)
//                    .load(url)
//                    .placeholder(res)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 磁盘缓存
//                    .skipMemoryCache(true) // 使用内存缓存
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(this)
//            }

//            if ((widths / heights) >= 0.75) {
//                //视频画面比例自适应
//                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT)
//                layoutParams.width = widths
//                layoutParams.height = heights
//            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.cache_control_video
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false
    }

//    override fun onSurfaceUpdated(surface: Surface?) {
//        super.onSurfaceUpdated(surface)
//
//        mCoverImage?.apply {
//            if (this.visibility == VISIBLE) {
//                postDelayed({
//                    this.visibility = GONE
//                }, 50)
//            }
//        }
//        tvFace?.apply {
//            if (this.visibility == VISIBLE) {
//                postDelayed({
//                    this.visibility = GONE
//                }, 50)
//            }
//        }
//    }

    override fun onSurfaceUpdated(surface: Surface?) {
        super.onSurfaceUpdated(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
            postDelayed({
                mThumbImageViewLayout.visibility = GONE
            }, 200)
        }
    }

    override fun setViewShowState(view: View?, visibility: Int) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return
        }
        super.setViewShowState(view, visibility)
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.visibility == VISIBLE) {
                mThumbImageViewLayout.visibility = GONE
            }
        }
    }

    override fun touchDoubleUp(e: MotionEvent) {
        //super.touchDoubleUp();
        //不需要双击暂停
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            if (event.pointerCount > 1) {
                v?.parent?.requestDisallowInterceptTouchEvent(true)
            } else {
                v?.parent?.requestDisallowInterceptTouchEvent(false)
            }
            scaleGestureDetector.onTouchEvent(it)
        }
        return super.onTouch(v, event)
    }

    private val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            // 触发了双指缩放手势，可能是放大也可能是缩小
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (detector.scaleFactor > 1) {
                openImg(mOriginUrl)
            }
            return true
        }
    })

    private fun openImg(urlImg: String) {
        if (urlImg.isNotEmpty()) {
            OpenImage.with(mContext)
                .setOpenImageStyle(R.style.Theme_Zoom)
                .setNoneClickView()
                .disableClickClose()
                .setImageUrlList(mutableListOf(urlImg), MediaType.VIDEO)//RecyclerView的数据
                .setVideoFragmentCreate(VideoFragmentCreateImpl())
                .setClickPosition(0)  //点击的ImageView所在数据的位置
                .show()
        }
    }

    companion object {
        const val Tag: String = "CacheVideo"
    }
}
