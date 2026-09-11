package com.face.ui.tcustom

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.BR
import com.face.R
import com.face.bean.TargetBean
import com.face.databinding.ActivityFrameVideopostBinding
import com.face.key.Constants
import com.face.ui.SwapFaceNewActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.CancelPostDialog
import com.face.viewmodel.activity.FrameVideoPostViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.io.File


class FramePostVideoActivity :
    BaseBindingActivity<ActivityFrameVideopostBinding, FrameVideoPostViewModel>(
        R.layout.activity_frame_videopost,
        FrameVideoPostViewModel::class.java
    ) {
    val isShare by intentExtras("isShare", false)
    val paramType by intentExtras("paramType", "{\"video_type\":\"180\"}")
    val videoUrl by intentExtras("video_url", "")
    val videoHW by intentExtras("video_hw", "")
    private val listData by intentExtras("face_data", mutableListOf<TargetBean>())


    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            onBackHome()
        }
        val json = JSONObject(paramType)
        json.put("sub", "1")
        mModel.postUrl = videoUrl
        mModel.type =json.toString()
        mModel.hw = videoHW
        if (listData.isEmpty() || videoUrl == "") finish()

        val rotateAnimation: Animation =
            AnimationUtils.loadAnimation(mActivity, R.anim.rotate_anim)
        val lin = LinearInterpolator()
        rotateAnimation.interpolator = lin
        mBinding?.progressV?.startAnimation(rotateAnimation)

        listData.toMutableList().forEach {
            mModel.faceUrl = mModel.faceUrl + "," + it.path
        }
        mModel.hw = videoHW

        val file = File(videoUrl)  // 替换为实际的文件路径
        var fileSizeInMB = 0f
        if (file.exists()) {
            val fileSizeInBytes = file.length()  // 获取文件大小，单位是字节
            fileSizeInMB = (fileSizeInBytes.toDouble() / (1024 * 1024)).toFloat() // 转换为MB
        }

        val videoInfo =
            "SIZE:${fileSizeInMB}MB --" +
                    "paramType:$paramType" +
                    "path:$videoUrl"
        EventUtil.videoPost("long", videoInfo)

        val dir = File(Constants.saveVideoPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        mModel.postVideo()
        Glide.with(this).asBitmap().load(videoUrl)
            .frame(0)
            .skipMemoryCache(true)  // 禁用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    mModel.postBitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        mModel.numProgress.observe(this) {
            launch(Dispatchers.Main) {
                mBinding?.picpostTxt1?.text = getStringX(R.string.picpost_uploading) + "${it}%"
            }
        }
        mModel.faceId.observe(this) {
            if (it != "") {
                SPUtils.isMaterial = true
                if (!isShare) {
                    SwapFaceNewActivity.jump(this, it, "video")
                } else {
                    GVM.INSTANT.isShareActivity.value = true
                }
                finish()
            }
        }
    }

    fun onBackHome() {
        CancelPostDialog { taskCancel() }.showIgnoreState(this)
    }

    fun taskCancel() {
        mModel.cancelRepository()
        finish()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}