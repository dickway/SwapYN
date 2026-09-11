package com.face.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.enums.MediaType
import com.face.BR
import com.face.R
import com.face.bean.DynamicAddBean
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityCompletionToolBinding
import com.face.key.AiTaskType
import com.face.key.Constants
import com.face.ui.live.LiveStartActivity
import com.face.ui.paperwork.PaperworkStartActivity
import com.face.ui.tadd.AddActivity
import com.face.ui.tage.AgeStartActivity
import com.face.ui.tcutout.CutoutStartActivity
import com.face.ui.thug.HugActivity
import com.face.ui.tkiss.KissActivity
import com.face.ui.tmoves.MovesActivity
import com.face.ui.tmoves.MovesListActivity
import com.face.ui.toutfit.OutfitActivity
import com.face.ui.trestoration.RestorationStartActivity
import com.face.ui.tstyle.StyleStartActivity
import com.face.ui.txtai.TxtImgActivity
import com.face.util.FileUtil
import com.face.util.GVM
import com.face.util.GoogleToPlay
import com.face.util.SPUtils
import com.face.video.CustomManager
import com.face.video.EmptyVideo
import com.face.video.VideoHeaderManager
import com.face.view.CommentDialog
import com.face.viewmodel.activity.ToolCompletionViewModel
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import java.io.File


class ToolCompletionActivity :
    BaseBindingActivity<ActivityCompletionToolBinding, ToolCompletionViewModel>(
        R.layout.activity_completion_tool, ToolCompletionViewModel::class.java
    ) {
    var strTitle = ""
    private val dataBean by intentExtras("task", ToolTaskBean())
    private val ist by intentExtras("ist", false)//是否用透明背景
    private lateinit var launcherPermission: ActivityResultLauncher<String>
    var toolList: List<DynamicAddBean>? = MoshiHelper.convertJsonToList(SPUtils.listDynamicTools)
    var dynamicAddBean: DynamicAddBean? = null

    var tagVideo = ""

    override fun init(savedInstanceState: Bundle?) {
        dynamicAddBean = toolList?.find { it.taskType == dataBean.type }
        strTitle = when (dataBean.type) {
            AiTaskType.RMBG -> getString(R.string.one_click_background_removal)

            AiTaskType.IMG2CARTOON -> getString(R.string.image_to_cartoon)

            AiTaskType.IMG2VIDEO -> getString(R.string.spicy_moves)

            AiTaskType.OLD_PHONE -> getString(R.string.old_photo_restoration)

            AiTaskType.ID_CARD -> getString(R.string.id_photo_generation)

            AiTaskType.CHANGE_AGE -> getString(R.string.age_change)

            AiTaskType.DYNAMIC -> getString(R.string.pic_live)

            AiTaskType.TEXT2IMG -> getString(R.string.txt_ai)

            AiTaskType.IMG2HUG -> getString(R.string.txt_start_hug1)

            AiTaskType.IMG2KISS -> getString(R.string.one_click_kiss)

            AiTaskType.CHANGE_CLOTHES -> getString(R.string.one_click_outfit_change)

            else -> dynamicAddBean?.getName() ?: ""
        }


        mBinding?.tvTitle?.text = strTitle
        mModel.downloadUrl = dataBean.recordUrl.split(",").firstOrNull().toString()
        mModel.showUrl = if (GVM.INSTANT.isVip.value) {
            dataBean.recordUrl.split(",").firstOrNull().toString()
        } else {
            dataBean.recordUrl.split(",").lastOrNull().toString()
        }

//        strTitle == getString(R.string.txt_ai) ||
        if (dynamicAddBean?.isAgain == false) {
            mBinding?.imgAgain?.visibility = View.GONE
            mBinding?.tvAgain?.visibility = View.GONE
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
                    intent.setData(Uri.parse("package:" + this.packageName))
                    startActivity(intent)
                    toast(getString(R.string.permissions_storage))
                    finish()
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
        if (strTitle == getString(R.string.pic_live) ||
            strTitle == getString(R.string.txt_start_hug1) ||
            strTitle == getString(R.string.one_click_kiss) ||
            strTitle == getString(R.string.spicy_moves) ||
            mModel.showUrl.endsWith(".mp4")||
            dynamicAddBean?.isVideo == true
        ) {
            mBinding?.apply {
                tagVideo = videoView.getVideoKey()
                val imglink=dataBean.recordUrl.split(",").lastOrNull()?:""
                videoView.loadBgImage5(imglink)
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setVideoTitle("")
                    .setRotateViewAuto(false)
                    .setLockLand(false)
                    .setPlayTag("showUrl")
                    .setLooping(true)
                    .setCacheWithPlay(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(false)
                    .setMapHeadData(VideoHeaderManager.headers)
                    .setPlayPosition(0)
                    .setVideoAllCallBack(object : GSYSampleCallBack() {
                        override fun onPlayError(url: String?, vararg objects: Any?) {
                            super.onPlayError(url, *objects)
                            videoView.setUp(url, true, null,VideoHeaderManager.headers,"")
                            videoView.startPlayLogic()
                        }
                    })
                    .build(videoView)
                videoView.setUp(mModel.showUrl, true, null,VideoHeaderManager.headers,"")
                videoView.startPlayLogic()
                imgCompletion.visibility = View.INVISIBLE
            }
        } else {
            Glide.with(this@ToolCompletionActivity).asBitmap().load(mModel.showUrl)
                .placeholder(com.key.R.drawable.img_default_m)
                .override(getScreenWidth(), getScreenHeight())
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap?>?
                    ) {
                        val width = resource.getWidth()
                        val height = resource.getHeight()

                        val widths = (getScreenWidth() - 40.dp)//控件最大宽度
                        val heights = (getScreenHeight()/3*2)//控件最大高度

                        val thresholdValue = widths / heights.toDouble() //阈值
                        val realValue = width / height.toDouble() //实际比例
                        dismissLoading()
                        mBinding?.cardView?.apply {
                            if (thresholdValue > realValue) {
                                layoutParams.width = heights * width / height
                                layoutParams.height = heights
                            } else {
                                layoutParams.width = widths
                                layoutParams.height = widths * height / width
                            }
                        }
                        mBinding?.imgCompletion?.apply {
                            if (ist) background = getDrawable(com.key.R.drawable.img_bj_cut)
                            setImageBitmap(resource)
                        }

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }


    }

    fun onAgain() {
        when (dataBean.type) {
            AiTaskType.TEXT2IMG -> {
                openActivity<TxtImgActivity> {
                    putParcelable("txtContent", dataBean.getinfo())
                }
            }
            AiTaskType.IMG2VIDEO -> {
                openActivity<MovesListActivity>()
            }

            AiTaskType.IMG2HUG -> {
                openActivity<HugActivity>()
            }

            AiTaskType.IMG2KISS -> {
                openActivity<KissActivity>()
            }

            AiTaskType.CHANGE_AGE -> {
                openActivity<AgeStartActivity>()
            }

            AiTaskType.DYNAMIC -> {
                openActivity<LiveStartActivity>()
            }

            AiTaskType.OLD_PHONE -> {
                openActivity<RestorationStartActivity>()
            }

            AiTaskType.IMG2CARTOON -> {
                openActivity<StyleStartActivity>()
            }

            AiTaskType.RMBG -> {
                openActivity<CutoutStartActivity>()
            }

            AiTaskType.ID_CARD -> {
                openActivity<PaperworkStartActivity>()
            }

            AiTaskType.CHANGE_CLOTHES -> {
                openActivity<OutfitActivity>()
            }

            else -> {
                openActivity<AddActivity> {
                    putString("tool_type", dynamicAddBean?.taskType)
                }
            }
        }
        finish()
    }


    fun download() {
        if (!mModel.isDownloadSate.value) {
            if (GVM.INSTANT.userInfo.value.isVip()) {
                SPUtils.downloadNum += 1
                mModel.download()
            } else {
                GVM.INSTANT.payPage.value = when (strTitle) {
                    getString(R.string.txt_ai) -> "Home_completion_txtai"
                    else -> {
                        dynamicAddBean?.eventName ?: "Home_completion_"
                    }
                }
                VipActivity.jump(this@ToolCompletionActivity)
            }
        }
    }

    fun reviewApp() {
        if (SPUtils.commentNum > 0 && SPUtils.commentNum < SPUtils.downloadNum && !SPUtils.notComment) {
            CommentDialog(
                onOpenCode = { onOpenCode() },
                onFeedback = { onFeedback() }).showIgnoreState(this)
        }
    }

    fun onFeedback() {
        openActivity<FeedbackActivity>()
    }

    fun onOpenCode() {
        GoogleToPlay.launchGooglePlay(this)
    }

    fun toPicture() {
        if (mModel.showUrl.endsWith("jpg", true) ||
            mModel.showUrl.endsWith("png", true)
        ) {
            openImg(mModel.showUrl, mBinding?.imgCompletion)
        }
    }


    private fun openImg(urlImg: String, imgView: ImageView?) {
        if (urlImg.isNotEmpty()) {

            OpenImage.with(mActivity).setOpenImageStyle(R.style.Theme_Main)
                .setClickImageView(imgView)//点击ImageView所在的RecyclerView
                .setSrcImageViewScaleType(
                    ImageView.ScaleType.FIT_CENTER, true
                ) //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
                .setImageUrlList(mutableListOf(urlImg), MediaType.IMAGE)//RecyclerView的数据
                .setClickPosition(0)  //点击的ImageView所在数据的位置
                .show()

//            OpenImage.with(this) //点击ImageView所在的RecyclerView（也支持设置setClickViewPager2，setClickViewPager，setClickGridView，setClickListView，setClickImageView）
//                .setClickImageView(imgView) //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
//                .setSrcImageViewScaleType(ImageView.ScaleType.FIT_XY, true) //RecyclerView的数据
//                .setImageUrl(urlImg, type) //点击的ImageView所在数据的位置
//                .setOpenImageActivityCls(PreviewPictureActivity::class.java) //设置自定义的大图外壳页面（可不设置）
//                .setOnSelectMediaListener { _, _ ->//设置切换图片监听（可不设置）
//                }
//                .addPageTransformer(ScaleInTransformer()) //设置切换大图时的效果（可不设置，本库中目前只有这一个，如需其他效果可参照ScaleInTransformer自行定义效果）
//                .show()//开始展示大图
        }
    }


    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val writePermission: Boolean =
            (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this@ToolCompletionActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
        if (writePermission) {
            val downloadFile = File(Constants.savePath, mModel.downName.value)
            FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
            reviewApp()
        } else {
            launcherPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding?.videoView?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding?.videoView?.onVideoResume()
    }

    override fun onDestroy() {
        if (tagVideo.isNotEmpty()) CustomManager.clearAllVideo(tagVideo)
        super.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}


//class VideoFragmentCreateImpl(val wigthVideo: Int, val hightVideo: Int) : VideoFragmentCreate {
//    override fun createVideoFragment(): BaseImageFragment<out View?> {
//        return VideoCompletionFragment(wigthVideo, hightVideo)
//    }
//}