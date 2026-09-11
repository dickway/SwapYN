package com.face.ui.fragment

import android.os.Bundle
import androidx.core.view.postDelayed
import androidx.lifecycle.Observer
import coil.load
import com.blankj.utilcode.util.LogUtils
import com.base.BlurTransformation
import com.face.BR
import com.face.R
import com.face.bean.MediaByBean
import com.face.databinding.FragmentSwapNewBinding
import com.face.net.Repository
import com.face.ui.VipActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.VibrationUtil
import com.face.video.CustomManager
import com.face.video.VideoHeaderManager
import com.face.video.ZoomVideo
import com.face.view.DisclaimerDialog
import com.face.view.ReportDialog
import com.face.view.ScoreDialog
import com.face.viewmodel.activity.SwapNewViewModel
import com.flyjingfish.openimagefulllib.GSYVideoOpenPlayer.GONE
import com.flyjingfish.openimagefulllib.GSYVideoOpenPlayer.VISIBLE
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.toast
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

class SwapNewFragment :
    BaseBindingFragment<FragmentSwapNewBinding, SwapNewViewModel>(
        R.layout.fragment_swap_new,
        SwapNewViewModel::class.java
    ) {


    private val type: String by lazy {
        arguments?.getString("type") ?: ""
    }

    companion object {
        fun newInstance(type: String): SwapNewFragment {
            val fragment = SwapNewFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }


    //Activity的ViewModel
    val mModelActivity: SwapNewViewModel by lazy {
        getViewModel(SwapNewViewModel::class.java, requireActivity())
    }


    override fun init(savedInstanceState: Bundle?) {

        mModelActivity.isReport.value = GVM.INSTANT.isShowTool.value == 0

        mBinding?.apply {
            //图标阴影效果
            imgCollect.setImageResource(if (GVM.INSTANT.isShowTool.value != 0) R.drawable.checkbox_collect else R.drawable.checkbox_collect2)
            tvCollect.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvDisclaimer.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvReport.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))

//            layoutFace.singleClick {
//                if (!mModelActivity.isCollect.value) {
//                    onCollectClick()
//                }
//            }
            imgCollect.singleClick {
                onCollectClick()
            }

            btnVip.singleClick {
                GVM.INSTANT.payPage.value = "Share_Popular_Templates"
                VipActivity.jump(requireActivity())
            }

        }

        mModelActivity.isBlurView.observe(this, Observer {
            if (!it && !mModel.isInPageVip) {
                playPosition(mModelActivity.mediaByBean.value ?: MediaByBean())
            }
        })

        mModelActivity.mediaByBean.observe(this, Observer {
            it?.apply {
                if (mModelActivity.isBlurView.value) {
                    mBinding?.imgVague?.load(imageUrl) {
                        transformations(BlurTransformation(radius = 8, scale = 0.2f))
                    }
                } else {
                    playPosition(it)
                }
                mModel.proShow.value = pro != 0

            }
        })
    }


    override fun onPause() {
        super.onPause()
        mBinding?.videoFace?.onVideoPause()
    }

    override fun onStart() {
        GVM.INSTANT.swapOpenTime.value = System.currentTimeMillis()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        mBinding?.videoFace?.onVideoResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding?.videoFace?.getAttacher()?.unRegisterDisplayListener()
        mBinding?.imgFace?.attacher?.unRegisterDisplayListener()
        CustomManager.clearAllVideo(ZoomVideo.Tag)
    }

    fun onVideoSoundClick() {
        GVM.INSTANT.swapNeedMute.value = !GVM.INSTANT.swapNeedMute.value
        mBinding?.videoFace?.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
    }

    fun onToAct() {
        DisclaimerDialog().showIgnoreState(this)
    }

    fun onReport() {
        ReportDialog { black, content ->
            mModel.getReport(mModelActivity.mediaByBean.value?.id, content, black)
        }.showIgnoreState(this)
    }

    fun onScore() {
        ScoreDialog(mModelActivity.mediaByBean.value?.getScoreFloat()).showIgnoreState(this)
    }

    fun onCollectClick() {
        if (mModelActivity.isLoad.value) {//需要加载完成后才能点击
            val collect = mModelActivity.isCollect.value
            val collectId = mModelActivity.collectStr
            val mediaId = mModelActivity.mediaByBean.value?.id ?: ""
            mModelActivity.isCollect.value = !mModelActivity.isCollect.value

            launchRequestOnIO({
                if (collect) {
                    Repository.removeUserCollect(collectId)
                } else {
                    Repository.saveCollect(mediaId, "media")
                }
            }) {
                onSuccess = {
                    mModelActivity.collectStr = it.toString()
                    if (!collect) toast(getStringX(R.string.collect_success))
                }
                onFailed = { _, _, errorMsg ->
                    mModelActivity.isCollect.value = !mModelActivity.isCollect.value
                    toast(errorMsg)
                }
            }
        }
    }


    private fun playPosition(mediaByBean: MediaByBean) {
        mBinding?.apply {
            val widths = getScreenWidth()
            val heights = getScreenHeight()
            val realValue = mediaByBean.width / mediaByBean.notZeroHeight().toDouble() //实际比例
            var imgW: Int
            var imgH: Int
            layoutFace.apply {
                if (realValue < 0.75) {//根据图片大小优先显示宽
                    imgW = widths
                    imgH = heights
                } else {
                    imgW = widths
                    imgH = mediaByBean.height * widths / mediaByBean.notZeroWidth()
                }
            }
            imgFace.visibility = VISIBLE
            videoFace.visibility = GONE
            if (mediaByBean.mediaType == "video" && type == "Swap") {
                imgFace.visibility = GONE
                videoFace.visibility = VISIBLE
                videoFace.loadBgImage(
                    mediaByBean.webpUrl.ifEmpty { mediaByBean.imageUrl },
                    com.key.R.drawable.img_default_m,
                    imgW,
                    imgH
                )
                videoFace.getAttacher()?.setOnShockListener {
                    VibrationUtil.vibrateOnce(requireContext().applicationContext, 100)
                }
                videoFace.getAttacher()?.setOnZoomListener { iszoom ->
                    if (iszoom) {
                        mBinding?.zoomView?.visibility = VISIBLE
                    } else {
                        mBinding?.zoomView?.visibility = GONE
                    }
                }
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setVideoTitle("")
                    .setRotateViewAuto(false)
                    .setLockLand(false)
                    .setPlayTag("RecyclerView2List")
                    .setLooping(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(false)
                    .setPlayPosition(0)
                    .setMapHeadData(VideoHeaderManager.headers)
                    .setVideoAllCallBack(object : GSYSampleCallBack() {
                        override fun onStartPrepared(url: String?, vararg objects: Any?) {
                            super.onStartPrepared(url, *objects)
                            GVM.INSTANT.videoOpenTime.value = System.currentTimeMillis()
                        }

                        override fun onClickBlank(url: String?, vararg objects: Any?) {
                            super.onClickBlank(url, *objects)
                            onCollectClick()
                        }

                        override fun onPrepared(url: String, vararg objects: Any) {
                            super.onPrepared(url, *objects)
                            val pageTime =
                                System.currentTimeMillis() - GVM.INSTANT.swapOpenTime.value
                            val videoTimeC =
                                System.currentTimeMillis() - GVM.INSTANT.videoOpenTime.value
                            videoFace.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
                            LogUtils.e("onPrepared:${pageTime}：${videoTimeC}   ${mediaByBean.videoUrl}")
                            if (GVM.INSTANT.isForeground.value != false)//离开app不上传
                                EventUtil.loadTime(pageTime.toString(), videoTimeC.toString(), url)
                        }

                        override fun onPlayError(url: String?, vararg objects: Any?) {
                            super.onPlayError(url, *objects)
                            LogUtils.e("onPlayError:${objects.contentToString()}  $url")
                            videoFace.postDelayed(500L) {
                                videoFace.setUp(
                                    mediaByBean.videoUrl.replace("%20", " "),
                                    true,
                                    null,
                                    VideoHeaderManager.headers,
                                    ""
                                )
                                videoFace.isLooping = true
                                videoFace.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
                                videoFace.startPlayLogic()
                            }
                        }
                    }).build(videoFace)
                videoFace.setUp(mediaByBean.videoUrl, true, null, VideoHeaderManager.headers, "")
                videoFace.isLooping = true
                videoFace.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
                videoFace.startPlayLogic()
                //广告播放中暂停视频
                if (mModelActivity.showInterstitial) videoFace.onVideoPause()
                mModelActivity.showInterstitial = false
            } else {
                mBinding?.imgFace?.setDoubleClickListener {
                    if (!mModelActivity.isCollect.value) {
                        onCollectClick()
                    }
                }
                mBinding?.imgFace?.loadImage(
                    mediaByBean.imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )
                mBinding?.imgFace?.attacher?.setOnShockListener {
                    VibrationUtil.vibrateOnce(requireContext().applicationContext, 100)
                }
                mBinding?.imgFace?.attacher?.setOnZoomListener { iszoom ->
                    if (iszoom) {
                        mBinding?.zoomView?.visibility = VISIBLE
                    } else {
                        mBinding?.zoomView?.visibility = GONE
                    }
                }
            }
        }
    }

    fun replyZoom() {
        VibrationUtil.vibrateOnce(requireContext().applicationContext, 200)
        mBinding?.videoFace?.getAttacher()?.replyZoom()
        mBinding?.imgFace?.attacher?.replyZoom()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.avm, mModelActivity)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}