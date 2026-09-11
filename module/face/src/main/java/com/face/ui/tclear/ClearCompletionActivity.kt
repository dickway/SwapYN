package com.face.ui.tclear

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.BR
import com.face.R
import com.face.key.Constants
import com.face.util.FileUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.flyjingfish.openimagelib.OpenImage
import com.flyjingfish.openimagelib.enums.MediaType
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityCompletionClearBinding
import com.face.key.AiTaskType
import com.face.ui.FeedbackActivity
import com.face.ui.VipActivity
import com.face.ui.live.LiveStartActivity
import com.face.ui.paperwork.PaperworkStartActivity
import com.face.ui.tadd.AddActivity
import com.face.ui.tage.AgeStartActivity
import com.face.ui.tcutout.CutoutStartActivity
import com.face.ui.thug.HugActivity
import com.face.ui.tkiss.KissActivity
import com.face.ui.toutfit.OutfitActivity
import com.face.ui.trestoration.RestorationStartActivity
import com.face.ui.tstyle.StyleStartActivity
import com.face.ui.txtai.TxtImgActivity
import com.face.util.GoogleToPlay
import com.face.view.CommentDialog
import com.face.viewmodel.activity.ClearCompletionViewModel
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import java.io.File


class ClearCompletionActivity :
    BaseBindingActivity<ActivityCompletionClearBinding, ClearCompletionViewModel>(
        R.layout.activity_completion_clear,
        ClearCompletionViewModel::class.java
    ) {
    private val isPost by intentExtras("ispost", false)//是否是上传界面过来
    private val dataBean by intentExtras("task", ToolTaskBean())
    private lateinit var launcherPermission: ActivityResultLauncher<String>

    override fun init(savedInstanceState: Bundle?) {

        if (isPost) SPUtils.isMaterial = true


        mModel.downloadUrl = dataBean.recordUrl.split(",").firstOrNull().toString()
        mModel.showUrl =  if(GVM.INSTANT.isVip.value){
            dataBean.recordUrl.split(",").firstOrNull().toString()
        }else{
            dataBean.recordUrl.split(",").lastOrNull().toString()
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


        Glide.with(this@ClearCompletionActivity).asBitmap().load(mModel.showUrl)
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
                    mBinding?.imgCompletion?.apply {
                        if (thresholdValue > realValue) {
                            layoutParams.width = heights * width / height
                            layoutParams.height = heights
                        } else {
                            layoutParams.width = widths
                            layoutParams.height = widths * height / width
                        }
                    }
                    mBinding?.imgCompletion?.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

    }

    fun download() {
        if (!mModel.isDownloadSate.value) {
            SPUtils.downloadNum += 1
            mModel.download()
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

    fun onFeedback() {
        openActivity<FeedbackActivity>()
    }

    fun onOpenCode() {
        GoogleToPlay.launchGooglePlay(this)
    }

    fun onAgain() {
        openActivity<ClearStartActivity>()
        finish()
    }

    fun toPicture() {
        openImg(mModel.showUrl, mBinding?.imgCompletion)
    }

//    fun toSwap() {
//        if (mModel.taskBean.value?.media?.mediaId != "") {
//            mModel.taskBean.value?.media?.mediaId?.let { SwapFaceNewActivity.jump(this, it) }
//        } else {
//            toPictureOld()
//        }
//    }

//    fun toPictureOld() {
//        mModel.taskBean.value?.media?.let {
//            if (it.mediaType != ("video")) {
//                openImg(it.imageUrl, mBinding?.imgMaterial)
//            } else {
//                openImg(it.videoUrl, mBinding?.imgMaterial2, MediaType.VIDEO)
//            }
//        }
//    }

    private fun openImg(urlImg: String, imgView: ImageView?) {
        if (urlImg.isNotEmpty()) {
            OpenImage.with(mActivity)
                .setOpenImageStyle(R.style.Theme_Main)
                .setClickImageView(imgView)//点击ImageView所在的RecyclerView
                .setSrcImageViewScaleType(ImageView.ScaleType.FIT_XY, true) //点击的ImageView的ScaleType类型（如果设置不对，打开的动画效果将是错误的）
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
        val writePermission: Boolean = (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(
            this@ClearCompletionActivity,
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

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}


//class VideoFragmentCreateImpl(val wigthVideo: Int, val hightVideo: Int) : VideoFragmentCreate {
//    override fun createVideoFragment(): BaseImageFragment<out View?> {
//        return VideoCompletionFragment(wigthVideo, hightVideo)
//    }
//}