package com.a.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.a.R
import com.a.BR
import com.a.databinding.ActivityAcompletionBinding
import com.a.dialog.ABaseContentDialog
import com.a.dialog.ABaseHintDialog
import com.a.dialog.AReportDialog
import com.a.viewmodel.ACompletionViewModel
import com.bumptech.glide.Glide
import com.face.key.Constants
import com.face.net.Repository
import com.face.util.FileUtil
import com.face.util.GVM
import com.face.video.PauseVideo
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.enums.MediaType
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.io.File


class ACompletionActivity :
    BaseBindingActivity<ActivityAcompletionBinding, ACompletionViewModel>(
        R.layout.activity_acompletion,
        ACompletionViewModel::class.java
    ) {
    private val dataId by intentExtras("task_id", "")
    private lateinit var launcherPermission: ActivityResultLauncher<String>

    private var videoView: PauseVideo? = null

    override fun init(savedInstanceState: Bundle?) {
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
                    toast("Failed to obtain photo and video storage permissions")
                    finish()
                }
            }

        mModel.isDownload.observe(this) {//下载图片完成处理图片存储路径
            if (it) {
                val downloadFile = File(Constants.savePath, mModel.downName.value)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FileUtil.copyFileToDownloadDir(downloadFile.path, mModel.pathName.value)
                } else {
                    checkPermission()
                }

            }
        }
        videoView = mBinding?.videoFace
        mModel.taskid.value = dataId
        mModel.getData()
        mModel.taskBean.observe(this) {
            if (it.id != "") {
                mModel.showUrl.value = it.result.dataWater[0]
                mModel.downloadUrl.value = it.result.mData[0]
                mModel.isShowVideo.value = it.media.mediaType == "video"

                var widths = getScreenWidth() - 80.dp
                var heights = widths * it.media.notZeroHeight() / it.media.notZeroWidth()
                if (heights > (getScreenHeight() - 300.dp)) {
                    heights = getScreenHeight() - 300.dp
                    widths =
                        (getScreenHeight() - 300.dp) * it.media.notZeroWidth() / it.media.notZeroHeight()
                }

//                mBinding?.imgCompletion?.apply {
//                    layoutParams.height = heights
//                    layoutParams.width = widths
//                }
//                mBinding?.cardView?.apply {
//                    layoutParams.height = heights
//                    layoutParams.width = widths
//                }

                if (it.media.mediaType == "video") {
                    onLoadViedo(it?.result?.dataWebp ?: "", widths, heights)
                } else {
                    mBinding?.imgCompletion?.loadImage(
                        mModel.showUrl.value,
                        placeholderResId = com.key.R.drawable.img_default_m
                    )
                }


            }
        }

    }

    private fun onLoadViedo(showImgVideo: String, widths: Int = 0, heights: Int = 0) {
        videoView?.tag = "PauseVideo_Completion"
        videoView?.loadBgImage(
            showImgVideo,
            com.key.R.drawable.img_default_m,
            widths = widths,
            heights = heights,
        ) {
            videoView?.startPlayLogic()
        }
        videoView?.setUp(mModel.showUrl.value, true, "")
        videoView?.isLooping = true
        videoView?.startPlayLogic()
    }

    fun onNotice() {
        ABaseHintDialog(
            "Notice",
            "You can view \"Build history\" in \"My\".\"Generate History\", will only keep photos/videos for the last 7 days, after which it will be cleared if you like to remember favorites",
            "Ok"
        ).showIgnoreState(mActivity)
    }

    fun onPointDownload() {
        download()
    }

    fun onDelete() {
        ABaseContentDialog(
            "Remove Generate",
            "Are you sure you want to remove Generate",
            "Delete",
            "Cancel",
            onOkData = {
                deleteTask()
            })
            .showIgnoreState(this)
    }

    fun deleteTask() {
        launchRequestWithLoadingOnIO({
            Repository.deleteUserMedia(
                mModel.taskBean.value?.id ?: ""
            )
        }) {
            onSuccess = {
                toast(getStringX(R.string.asuccess))
                finish()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun download() {
        GVM.INSTANT.payPage.value =
            "Download_material_${mModel.taskBean.value?.taskType}_${mModel.taskBean.value?.media?.mediaType}"
        if (!mModel.isDownloadSate.value) {
            if (GVM.INSTANT.userInfo.value.isVip()) {
                mModel.download()
            } else {
                openActivity<AVipActivity>()
            }
        }
    }

    fun onReport() {
        AReportDialog { black, content ->
           deleteTask()
        }.showIgnoreState(this)
    }

    fun toPicture() {
//        openImg(mModel.showUrl.value, mBinding?.imgCompletion)
    }

    private fun openImg(urlImg: String, imgView: ImageView?, type: MediaType = MediaType.IMAGE) {
        if (urlImg.isNotEmpty()) {
            OpenImage.with(mActivity)
                .setOpenImageStyle(R.style.AppTheme_A)
                .setClickImageView(imgView)//点击ImageView所在的RecyclerView
                .setSrcImageViewScaleType(
                    ImageView.ScaleType.FIT_XY,
                    true
                ) //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
                .setImageUrlList(mutableListOf(urlImg), MediaType.IMAGE)//RecyclerView的数据
                .setClickPosition(0)  //点击的ImageView所在数据的位置
                .show()
        }
    }

    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val writePermission: Boolean = (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(
            this@ACompletionActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        if (writePermission) {
            val downloadFile = File(Constants.savePath, mModel.downName.value)
            FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
        } else {
            launcherPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onPause() {
        super.onPause()
        videoView?.onVideoPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        videoView?.release()
        GSYVideoManager.releaseAllVideos()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}

