package com.face.video

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.face.R
import com.flyjingfish.openimagefulllib.GSYVideoOpenPlayer
import com.flyjingfish.openimagefulllib.ScaleDrawable
import com.flyjingfish.openimagefulllib.ScaleRelativeLayout
import com.flyjingfish.openimagefulllib.VideoPlayerAttacher
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge
import com.zzkj.structure.util.ImgLoader.loadImage
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import androidx.core.view.isVisible

class ZoomPauseVideo : GSYVideoOpenPlayer {

    var onVideoError: videoError? = null
    var mCoverImage: ImageView? = null
    var tvFace: TextView? = null
    var surfaceContaine: ScaleDrawable? = null
    var scaleRelativeLayout: ScaleRelativeLayout? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getGSYVideoManager(): GSYVideoViewBridge? {
        CustomManager.getCustomManager(videoKey).initContext(context.applicationContext)
        return CustomManager.getCustomManager(videoKey)
    }

    override fun init(context: Context) {
        super.init(context)
        mCoverImage = findViewById(R.id.thumbImage)
        tvFace = findViewById(R.id.tvFace)
        scaleRelativeLayout = findViewById(R.id.rl_video_player_root)
        surfaceContaine = findViewById(R.id.surface_container)
        if (mThumbImageViewLayout != null &&
            (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)
        ) {
            mThumbImageViewLayout.visibility = VISIBLE
        }

        //改成了双击触发
        scaleRelativeLayout?.setOnClickListener {
            clickStartIcon()
        }

        //改成了单击触发
        scaleRelativeLayout?.setOnLongClickListener {
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                setViewShowState(mStartButton, VISIBLE)
            }
            false
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.zoom_pause_video
    }

    fun loadBgImage(url: String, res: Int, widths: Int, heights: Int, onE: videoError) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        tvFace?.visibility = VISIBLE
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
        videoWidth = widths
        videoHeight = heights
        mCoverImage?.apply {
            loadImage(
                url,
                placeholderResId = res
            )
            layoutParams.width = widths
            layoutParams.height = heights
        }
        onVideoError = onE
    }


    override fun onSurfaceUpdated(surface: Surface?) {
        super.onSurfaceUpdated(surface)
//        LogUtils.e(">>>>>>>>>>>onSurfaceUpdated:${mCurrentState}")
//        isZoomPlaying=(mCurrentState==CURRENT_STATE_PAUSE)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.isVisible) {
            mThumbImageViewLayout.postDelayed(Runnable {
                mThumbImageViewLayout.visibility = INVISIBLE
            }, 200)
        }
    }

    override fun setViewShowState(view: View?, visibility: Int) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return
        }
        if (view == mStartButton && visibility != VISIBLE && mCurrentState == CURRENT_STATE_PAUSE) {
            return
        }
        super.setViewShowState(view, visibility)
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.isVisible) {
                // 延迟一段时间再隐藏封面（比如 300ms）
                mThumbImageViewLayout.postDelayed(Runnable {
                    mThumbImageViewLayout.visibility = INVISIBLE
                }, 200)
            }
        }
    }


    override fun onError(what: Int, extra: Int) {
        onVideoError?.onError()
    }

    fun getAttacher(): VideoPlayerAttacher? {
        return scaleRelativeLayout?.attacher
    }
}
