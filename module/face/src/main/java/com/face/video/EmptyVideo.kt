package com.face.video

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.face.R
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge
import com.zzkj.structure.util.ImgLoader.loadImage
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.util.UUID


/**
 * 无任何控制ui的播放
 */
class EmptyVideo : StandardGSYVideoPlayer, View.OnClickListener {

    private var uUKey: String? = null

    var mCoverImage: ImageView? = null
    var tvFace: TextView? = null
    var mianContainer: RelativeLayout? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    override fun init(context: Context) {
        uUKey = UUID.randomUUID().toString()
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

    fun getVideoKey(): String {
        return "EmptyVideo$$uUKey"
    }

    override fun getGSYVideoManager(): GSYVideoViewBridge? {
        CustomManager.getCustomManager(getVideoKey()).initContext(context.applicationContext)
        return CustomManager.getCustomManager(getVideoKey())
    }

    fun loadBgImage1(res: Int) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
        mCoverImage?.setImageResource(res)
    }


    fun loadBgImage2(res: Int) {
        PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
        mCoverImage?.setImageResource(res)
    }

    fun loadBgImage3(res: Int) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT)
        mCoverImage?.setImageResource(res)
    }

    fun loadBgImage4(res: String) {
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
//        mCoverImage?.scaleType= ImageView.ScaleType.FIT_XY
        mCoverImage?.loadImage(res, placeholderResId=com.key.R.drawable.img_default_m)
    }

    fun loadBgImage5(res: String) {
        tvFace?.visibility = VISIBLE
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        mCoverImage?.visibility = VISIBLE
        //全屏会裁剪视频画面
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
        mCoverImage?.loadImage(res, placeholderResId=com.key.R.drawable.img_default_m)
    }




    override fun getLayoutId(): Int {
        return R.layout.empty_control_video
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
//        setDisplay(surface)
//        // ⚠️ 这里可以考虑恢复播放器状态（重新绑定 surface）
//        if (currentState == GSYVideoView.CURRENT_STATE_PAUSE
//            || currentState == GSYVideoView.CURRENT_STATE_ERROR
//        ) {
//            // surface 可用了，重新尝试播放
//            startPlayLogic()
//        }
//        LogUtils.e(">>>>>>>>>surface:${currentState}")
    }

    override fun touchDoubleUp(e: MotionEvent) {
        //super.touchDoubleUp();
        //不需要双击暂停
    }

    companion object {
        const val Tag: String = "EmptyVideo"
    }
}
