package com.a.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.a.R
import com.a.BR
import com.a.databinding.ActivityAtoolcompletionBinding
import com.a.dialog.ABaseContentDialog
import com.a.dialog.AReportDialog
import com.a.viewmodel.AToolCompletionViewModel
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.bean.ToolTaskBean
import com.face.key.Constants
import com.a.net.ARepository
import com.face.util.FileUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.enums.MediaType
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.io.File


class AToolCompletionActivity :
    BaseBindingActivity<ActivityAtoolcompletionBinding, AToolCompletionViewModel>(
        R.layout.activity_atoolcompletion,
        AToolCompletionViewModel::class.java
    ) {

    private val strTitle by intentExtras("title", "")
    private val dataBean by intentExtras("task", ToolTaskBean())
    private val ist by intentExtras("ist", false)//是否用透明背景
    private lateinit var launcherPermission: ActivityResultLauncher<String>

    override fun init(savedInstanceState: Bundle?) {
        mModel.downloadUrl = dataBean.recordUrl.split(",").firstOrNull().toString()
        mModel.showUrl = dataBean.recordUrl.split(",").lastOrNull().toString()
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
                    toast(getString(com.face.R.string.permissions_storage))
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

        Glide.with(this@AToolCompletionActivity).asBitmap().load(mModel.showUrl)
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    val width = resource.getWidth()
                    val height = resource.getHeight()

                    var imgW = getScreenWidth() - 16.dp
                    var imgH = imgW * height / width
                    if (imgH > (getScreenHeight() - 160.dp)) {
                        imgW = (getScreenHeight() - 160.dp) * width / height
                        imgH = getScreenHeight() - 160.dp
                    }
                    dismissLoading()
                    mBinding?.imgCompletion?.apply {
                        layoutParams.height = imgH
                        layoutParams.width = imgW
                        if (ist) {
                            background = getDrawable(com.key.R.drawable.img_bj_cut)
                        }
                        setImageBitmap(resource)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        if (dataBean.id.isEmpty()) {
            mBinding?.btnDelete?.visibility = View.GONE
        }

    }

    fun onReport() {
        AReportDialog { black, content ->
            deleteTask()
        }.showIgnoreState(this)
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
        launchRequestWithLoadingOnIO({ ARepository.deleteUserRecord(dataBean.id) }) {
            onSuccess = {
                toast(getStringX(com.face.R.string.success))
                finish()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun download() {
        if (!mModel.isDownloadSate.value) {
            if (GVM.INSTANT.userInfo.value.isVip()) {
                SPUtils.downloadNum += 1
                mModel.download()
            } else {
                GVM.INSTANT.payPage.value = "ATool_Download"
                openActivity<AVipActivity>()
            }
        }
    }

    fun toPicture() {
        openImg(mModel.showUrl, mBinding?.imgCompletion)
    }

    private fun openImg(urlImg: String, imgView: ImageView?, type: MediaType = MediaType.IMAGE) {
        if (urlImg.isNotEmpty()) {
            OpenImage.with(mActivity)
                .setOpenImageStyle(R.style.AppTheme_A)
                .setClickImageView(imgView)//点击ImageView所在的RecyclerView
                .setSrcImageViewScaleType(
                    ImageView.ScaleType.FIT_CENTER,
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
            this@AToolCompletionActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        if (writePermission) {
            val downloadFile = File(Constants.savePath, mModel.downName.value)
            FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
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

