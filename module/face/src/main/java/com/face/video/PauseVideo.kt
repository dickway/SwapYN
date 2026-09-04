package com.face.video

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.size.Size
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.face.R
import com.face.video.EmptyVideo.Companion.Tag
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import androidx.core.view.isVisible

/**
 * 有暂停控制ui的播放
 */
class PauseVideo : StandardGSYVideoPlayer {
    var mCoverImage: ImageView? = null
    var tvFace: TextView? = null
    var onVideoError: videoError? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    override fun init(context: Context) {
        super.init(context)
        mCoverImage = findViewById(R.id.thumbImage)
        tvFace = findViewById(R.id.tvFace)

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


    override fun getLayoutId(): Int {
        return R.layout.pause_control_video
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

    override fun onSurfaceUpdated(surface: Surface?) {
        super.onSurfaceUpdated(surface)
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.isVisible) {
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
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.isVisible) {
                mThumbImageViewLayout.visibility = GONE
            }
        }
    }

    override fun onError(what: Int, extra: Int) {
        onVideoError?.onError()
//        super.onError(what, extra)
    }

    override fun getGSYVideoManager(): GSYVideoViewBridge? {
        CustomManager.getCustomManager(Tag).initContext(context.applicationContext)
        return CustomManager.getCustomManager(Tag)
    }

    fun loadBgImage(url: String?, res: Int, widths: Int = 0, heights: Int = 0, onE: videoError) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        if (url != "") {
            tvFace?.visibility = VISIBLE
            mCoverImage?.visibility = VISIBLE
            mCoverImage?.loadImage(
                url,
                placeholderResId = res,
                size= Size(widths,heights)
            )
            onVideoError = onE
        }
    }


    private fun togglePlayPause() {
        if (currentState == CURRENT_STATE_PLAYING) {
            onVideoPause()
        } else if (currentState == CURRENT_STATE_PAUSE) {
            onVideoResume()
        } else if (currentState == CURRENT_STATE_NORMAL) {
            onVideoError?.onError()
        }
    }

    override fun onClick(v: View?) {
        togglePlayPause()
    }

    companion object {
        const val Tag: String = "PauseVideo"
    }
}
