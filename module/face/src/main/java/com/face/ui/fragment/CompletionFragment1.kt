package com.face.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.bean.TaskBean
import com.face.databinding.FragmentCompletion1Binding
import com.face.key.AiTaskType
import com.face.key.Constants
import com.face.ui.FeedbackActivity
import com.face.ui.SwapFaceNewActivity
import com.face.ui.VipActivity
import com.face.ui.tcustom.CustomStartActivity
import com.face.util.EventUtil
import com.face.util.FileUtil
import com.face.util.GVM
import com.face.util.GoogleToPlay
import com.face.util.NotificationUtil
import com.face.util.SPUtils
import com.face.util.VibrationUtil
import com.face.video.CustomManager
import com.face.video.VideoHeaderManager
import com.face.video.ZoomPauseVideo
import com.face.view.BaseRedDialog
import com.face.view.CommentDialog
import com.face.view.CompletionPointsDialog
import com.face.view.CustomDialog
import com.face.view.RatingDialog
import com.face.viewmodel.activity.ComViewModel
import com.face.viewmodel.fragment.CompletionViewModel
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.io.File

class CompletionFragment1 : BaseBindingFragment<FragmentCompletion1Binding, CompletionViewModel>(
    R.layout.fragment_completion1, CompletionViewModel::class.java
) {
    var dataId = ""
    var collectBean = TaskBean()
    var dataNum = 0//多图是第几张

    private var videoView: ZoomPauseVideo? = null
    private lateinit var launcherPermission: ActivityResultLauncher<String>

    private var playerKey: String = ""

    //Activity的ViewModel
    val mModelActivity: ComViewModel by lazy {
        getViewModel(ComViewModel::class.java, requireActivity())
    }


    override fun init(savedInstanceState: Bundle?) {

        dataNum = activity?.intent?.getIntExtra("task_num", 0) ?: 0
        dataId = activity?.intent?.getStringExtra("task_id") ?: ""
        collectBean = activity?.intent?.getParcelableExtra("collectBean") ?: TaskBean()

        SPUtils.notComment = false
        mBinding?.apply {
            txtLike.setOnClickListener {
                mModelActivity.isSlide.postValue(true)
            }


            imgCollect.setImageResource(if (mModel.isShowAB.value != 0) R.drawable.checkbox_collect else R.drawable.checkbox_collect2)

            tvCollect.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvSwap.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvPoint.text =
                "${mModel.useBean.value?.pointsDownload}${getString(R.string.aipoints)}"
            tvBalance.text = getString(
                com.face.R.string.balance_num,
                GVM.INSTANT.userInfo.value.tflops.toString()
            )

            val fullText =
                getString(R.string.completion_cutosm_txt1) + " " + getString(R.string.completion_cutosm_txt2)
            val clickableText = getString(R.string.completion_cutosm_txt2)
            // 找到需要添加点击事件的文字的开始和结束位置
            val startIndex = fullText.indexOf(clickableText)
            val endIndex = startIndex + clickableText.length

            val spannableString = SpannableString(fullText)

            // 设置点击事件
//            spannableString.setSpan(object : ClickableSpan() {
//                override fun onClick(widget: View) {
//                    onCustom()
//                }
//            }, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

            // 设置文字颜色
            spannableString.setSpan(
                ForegroundColorSpan(getColorX(R.color.color11FFEE)),
                startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            // 添加下划线
            spannableString.setSpan(
                UnderlineSpan(),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // 设置TextView的文本为可点击
            tv3.text = spannableString
            tv3.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        }

        //单个权限申请
        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {//同意
                    val downloadFile = File(Constants.savePath, mModel.downName.value)
                    FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
                } else {//拒绝
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.parse("package:" + requireActivity().packageName))
                    startActivity(intent)
                    toast(getString(R.string.permissions_storage))
                    mActivity?.finish()
                }
            }

        mModel.isDownload.observe(this) {//下载图片完成处理图片存储路径
            if (it) {
                val downloadFile = File(Constants.savePath, mModel.downName.value)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FileUtil.copyFileToDownloadDir(downloadFile.path, mModel.pathName.value)
                    reviewApp()
                } else {
                    checkPermission()
                }

            }
        }

        mModel.isZoom.observe(this) {
            if (it) {
                mBinding?.apply {
                    line1.visibility = View.INVISIBLE
                    clNoVip.visibility = View.INVISIBLE
                    btnVipD.visibility = View.INVISIBLE
                }
            } else {
                mBinding?.apply {
                    if (mModel.isShowAB.value != 0) {
                        line1.visibility = VISIBLE
                    }
                    if (!GVM.INSTANT.isVip.value) {
                        clNoVip.visibility = VISIBLE
                    } else {
                        btnVipD.visibility = VISIBLE
                    }
                }
            }
        }


        videoView = mBinding?.videoFace
        playerKey = videoView?.videoKey ?: ""


        mModel.taskNum.value = dataNum
        mModel.taskid.value = dataId

        mModel.taskBean.value = collectBean
        mModel.getData()


        mModel.taskBean.observe(this) { it ->
            if (it.id != "") {
                mModel.isRate.value = (it.getIsTaskShareState())
                mModel.isScore.value = it.params.rating > 0
                mModel.score.value = it.media.getScoreFloat()

                SPUtils.fishTask = SPUtils.fishTask + "," + dataId//记录看过的完成素材
                SPUtils.notificationList.forEach { beanNot ->
                    if (beanNot.taskId == dataId) {//移除素材完成提醒
                        NotificationUtil.deleteNotification(beanNot.notificatioId)
                        SPUtils.notificationList -= beanNot
                    }
                }

                if (it.taskType != AiTaskType.GEN_PERSONPIC && it.taskType != "") {//不是自定义pro才上报
                    EventUtil.inPage(
                        "success_single",
                        mapOf(
                            "task_id" to dataId,
                            "task_type" to it.taskType,
                            "media_type" to it.media.mediaType
                        )
                    )
                }

                mModel.showUrl.value = if (GVM.INSTANT.isVip.value) {
                    it.result.mData[mModel.taskNum.value]
                } else {
                    it.result.dataWater.getOrNull(mModel.taskNum.value)
                        ?: it.result.mData.getOrNull(mModel.taskNum.value)
                        ?: ""
                }

                mModel.downloadUrl.value = it.result.mData[mModel.taskNum.value]
                mModel.isShowVideo.value = it.media.mediaType == "video"

                mModel.getUserCollectUrl()

                videoView?.post {
                    val insets = ViewCompat.getRootWindowInsets(videoView!!)
                    val statusBarHeight =
                        insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0

                    var widths = getScreenWidth()
                    var heights = getScreenHeight() + statusBarHeight
                    var height =
                        widths * it.media.height / it.media.notZeroWidth()//控件最大高度,比例是设计图给的
                    if (height > (heights * 0.75)) {
                        height = heights
                    }

                    if (it.media.mediaType == "video") {
                        videoView?.getAttacher()?.setOnZoomListener { iszoom ->
                            mModel.isZoom.value = iszoom
                            mModelActivity.isZoom.value = iszoom
                        }
                        videoView?.getAttacher()?.setOnShockListener {
                            VibrationUtil.vibrateOnce(requireContext().applicationContext, 100)
                        }
                        onLoadViedo(it?.result?.dataWebp ?: "", widths, height)
                    } else {
                        mBinding?.imgCompletion?.attacher?.setOnZoomListener { iszoom ->
                            mModel.isZoom.value = iszoom
                            mModelActivity.isZoom.value = iszoom
                        }
                        mBinding?.imgCompletion?.attacher?.setOnShockListener {
                            VibrationUtil.vibrateOnce(requireContext().applicationContext, 100)
                        }

                        mBinding?.imgCompletion?.loadImage(
                            mModel.showUrl.value,
                            placeholderResId = com.key.R.drawable.img_default_m
                        )
                    }
                }
            }
        }

    }

    fun replyZoom() {
        VibrationUtil.vibrateOnce(requireContext().applicationContext, 200)
        mBinding?.videoFace?.getAttacher()?.replyZoom()
        mBinding?.imgCompletion?.attacher?.replyZoom()
    }

    fun onRate() {
        mModel.taskBean.value?.let { task ->
            val media = task.media ?: return@let
            if (task.id.isNotEmpty() && media.mediaId.isNotEmpty()) {
                RatingDialog(
                    task.id,
                    media.mediaId,
                    mModel.isScore.value,
                    mModel.score.value
                ) { s ->
                    mModel.isScore.value = true
                    mModel.score.value = s
                }.showIgnoreState(this)
            }
        }
    }

    private fun onLoadViedo(showImgVideo: String, widths: Int = 0, heights: Int = 0) {
        if (playerKey.isNotEmpty()) CustomManager.clearAllVideo(playerKey)
        videoView?.loadBgImage(
            showImgVideo,
            com.key.R.drawable.img_default_m,
            widths = widths,
            heights = heights,
        ) {
            LogUtils.e(">>>>>>>切换为软解码重试")
            // 1. 设置 IJK 为核心
            PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
//// 2. 禁用硬解
//            GSYVideoType.disableMediaCodec()
//            GSYVideoType.disableMediaCodecTexture()
//
//// 3. 设置 IJK 参数（软解优化）
//            val options = mutableListOf<VideoOptionModel>()
//            options += VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 0)
//            options += VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0)
//            options += VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0)
//            options += VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0)
//            GSYVideoManager.instance().optionModelList = options
            videoView?.setUp(
                mModel.showUrl.value.replace("%20", " "),
                true,
                null,
                VideoHeaderManager.headers,
                ""
            )
            videoView?.startPlayLogic()
            videoView?.postDelayed({
                videoView?.gsyVideoManager?.player?.setNeedMute(!mModel.isVideo.value)
            }, 150)
        }
        GSYVideoOptionBuilder()
            .setIsTouchWiget(false)
            .setVideoTitle("")
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setPlayTag("ZoomPauseVideo")
            .setLooping(true)
            .setShowFullAnimation(false)
            .setCacheWithPlay(true)
            .setNeedLockFull(false)
            .setPlayPosition(0)
            .setMapHeadData(VideoHeaderManager.headers)
            .setUrl(mModel.showUrl.value)
            .setVideoAllCallBack(object : GSYSampleCallBack() {

                override fun onClickBlank(url: String?, vararg objects: Any?) {
                    super.onClickBlank(url, *objects)
                    videoView?.showAllWidget()
                    CustomManager.onPause(playerKey)
                }

                override fun onPlayError(url: String?, vararg objects: Any?) {
                    super.onPlayError(url, *objects)
                    videoView?.setUp(
                        mModel.showUrl.value.replace("%20", " "),
                        true,
                        null,
                        VideoHeaderManager.headers,
                        ""
                    )
                    videoView?.startPlayLogic()
                }
            })
            .build(videoView)
        videoView?.postDelayed({
            videoView?.startPlayLogic()
        }, 200)
    }


    fun onPointDownload() {
        if (GVM.INSTANT.userInfo.value.tflops < (mModel.useBean.value?.pointsDownload ?: 3)) {
            CompletionPointsDialog().showIgnoreState(this)
        } else {
            BaseRedDialog(
                getString(R.string.dialog_aip_red_title),
                String.format(
                    resources.getString(R.string.dialog_aip_red_content),
                    mModel.useBean.value?.pointsDownload ?: ""
                ),
                getString(R.string.confirm),
                getString(R.string.cancel),
                onBtnOK = {
                    mModel.isUsePoint = true
                    download()
                }
            ).showIgnoreState(mActivity)
        }

    }

    fun download() {
        GVM.INSTANT.payPage.value =
            "Download_material_${mModel.taskBean.value?.taskType}_${mModel.taskBean.value?.media?.mediaType}"
        if (!mModel.isDownloadSate.value) {
            if (GVM.INSTANT.userInfo.value.isVip() || mModel.isUsePoint) {
                SPUtils.downloadNum += 1
                mModel.download()
            } else {
                VipActivity.jump(requireActivity())
            }
        }
    }

    fun reviewApp() {
        if (SPUtils.commentNum > 0 &&
            SPUtils.commentNum < SPUtils.downloadNum &&
            !SPUtils.notComment
        ) {
            CommentDialog(
                onOpenCode = { onOpenCode() },
                onFeedback = { onFeedback() })
                .showIgnoreState(this)
        }
    }

    fun onCustom() {
        CustomDialog(
            onPhotoAct = {
                CustomStartActivity.jump(requireContext(), "image")
            },
            onVideoAct = {
                CustomStartActivity.jump(requireContext(), "video")
            }
        ).showIgnoreState(this)
    }

    fun onFeedback() {
        openActivity<FeedbackActivity>()
    }

    fun onOpenCode() {
        GoogleToPlay.launchGooglePlay(requireActivity())
    }

    fun onSwapClick() {
        SwapFaceNewActivity.jump(
            requireContext(),
            mModel.taskBean.value?.media?.mediaId ?: "",
            typeCom = "completion",
            taskContent = mModel.taskBean.value?.content ?: ""
        )
    }

    fun onVideoClick() {
        if (mModel.isVideo.value) {
            videoView?.gsyVideoManager?.player?.setNeedMute(true)
        } else {
            videoView?.gsyVideoManager?.player?.setNeedMute(false)
        }
        mModel.isVideo.value = !mModel.isVideo.value
    }

    override fun onPause() {
        super.onPause()
        CustomManager.onPause(playerKey)
        mBinding?.videoFace?.getAttacher()?.replyZoom()
        mBinding?.imgCompletion?.attacher?.replyZoom()
    }

    override fun onResume() {
        super.onResume()
        SPUtils.isMaterial = true
        mModel.isUsePoint = false
        mModel.isShowAB.postValue(SPbaseUtils.loadAB)
    }

    override fun onDestroyView() {
        if (playerKey.isNotEmpty()) {
            CustomManager.clearAllVideo(playerKey)
        }
        mBinding?.videoFace?.getAttacher()?.unRegisterDisplayListener()
        mBinding?.imgCompletion?.attacher?.unRegisterDisplayListener()
        super.onDestroyView()
    }

    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val writePermission: Boolean = (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        if (writePermission) {
            val downloadFile = File(Constants.savePath, mModel.downName.value)
            FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
            reviewApp()
        } else {
            launcherPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}