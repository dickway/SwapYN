package com.face.ui.tcustom

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.BR
import com.face.R
import com.face.databinding.ActivityVideopostBinding
import com.face.ui.SwapFaceNewActivity
import com.face.util.GVM
import com.face.view.CancelPostDialog
import com.face.viewmodel.activity.VideoPostViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import kotlinx.coroutines.Dispatchers


class PostVideoActivity : BaseBindingActivity<ActivityVideopostBinding, VideoPostViewModel>(
    R.layout.activity_videopost,
    VideoPostViewModel::class.java
) {

    val videoUrl by intentExtras("video_url", "")
    val videoHW by intentExtras("video_hw", "")

    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            onBackHome()
        }
        mModel.postUrl = videoUrl
        mModel.hw = videoHW
        val rotateAnimation: Animation =
            AnimationUtils.loadAnimation(mActivity, R.anim.rotate_anim)
        val lin = LinearInterpolator()
        rotateAnimation.interpolator = lin
        mBinding?.progressV?.startAnimation(rotateAnimation)

        GVM.INSTANT.videoFaceList.value?.toMutableList()?.forEach {
            if (!it.isAdd) {
                mModel.faceUrl = mModel.faceUrl + "," + it.path
            }
        }
        mModel.hw = videoHW
        mModel.postVideo()
        Glide.with(this).asBitmap().load(videoUrl)
            .frame(0 )
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    mModel.postBitmap=resource
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
                SwapFaceNewActivity.jump(this, it, "video")
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