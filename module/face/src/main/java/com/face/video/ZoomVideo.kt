package com.face.video

import android.content.Context
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.apkfuns.logutils.LogUtils
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

class ZoomVideo : GSYVideoOpenPlayer {

    var mCoverImage: ImageView? = null
    var tvFace: TextView? = null
    var surfaceContaine: ScaleDrawable? = null
    var scaleRelativeLayout: ScaleRelativeLayout? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getGSYVideoManager(): GSYVideoViewBridge? {
        CustomManager.getCustomManager(Tag).initContext(context.applicationContext)
        return CustomManager.getCustomManager(Tag)
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
        scaleRelativeLayout?.setOnClickListener {
            mVideoAllCallBack.onClickBlank(mUrl)
        }
    }

//    fun hintLayout() {
//        tvFace?.visibility = GONE
//        mCoverImage?.visibility = GONE
//    }

    fun showLayout() {
//        changeUiToNormal()
        mThumbImageViewLayout.visibility = VISIBLE
    }

    override fun getLayoutId(): Int {
        return R.layout.zoom_control_video
    }

    fun loadBgImage(url: String, res: Int, widths: Int, heights: Int) {
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
    }

    fun getAttacher(): VideoPlayerAttacher? {
        return scaleRelativeLayout?.attacher
    }

    override fun onSurfaceUpdated(surface: Surface?) {
        super.onSurfaceUpdated(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.isVisible) {
            postDelayed({
                mThumbImageViewLayout.visibility = INVISIBLE
            }, 300)
        }
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.isVisible) {
            postDelayed({
                mThumbImageViewLayout.visibility = INVISIBLE
            }, 300)
        }
    }

    override fun setViewShowState(view: View?, visibility: Int) {
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return
        }
        super.setViewShowState(view, visibility)
    }

    companion object {
        const val Tag: String = "ZoomVideo"
    }
}
