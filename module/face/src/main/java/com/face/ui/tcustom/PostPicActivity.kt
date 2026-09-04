package com.face.ui.tcustom

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.databinding.ActivityPicpostBinding
import com.face.ui.SwapFaceNewActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.CancelPostDialog
import com.face.viewmodel.activity.PicPostViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast


class PostPicActivity : BaseBindingActivity<ActivityPicpostBinding, PicPostViewModel>(
    R.layout.activity_picpost,
    PicPostViewModel::class.java
) {

    val picUrl by intentExtras("pic_url", "")

    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            onBackHome()
        }
        mModel.updateUrl = picUrl
        mBinding?.apply {
            Glide.with(this@PostPicActivity).asBitmap()
                .load(picUrl)
                .skipMemoryCache(true)  // 禁用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
                .placeholder(com.key.R.drawable.img_default_m)
                .override(getScreenWidth(), getScreenHeight())
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        showLoading(getString(R.string.compress))
                        //检查是否需要压缩
                        mModel.updateBitmap = resource
                        val width = resource.getWidth()
                        val height = resource.getHeight()
                        var inSampleSize = 1
                        val max = 10 * 1024 * 1024
                        while (width * height * 4 / inSampleSize > max) {
                            inSampleSize *= 2
                        }
                        if (inSampleSize > 1) {
                            val m2 = Matrix()
                            m2.setScale(1 / inSampleSize.toFloat(), 1 / inSampleSize.toFloat())
                            mModel.updateBitmap  =
                                Bitmap.createBitmap(resource, 0, 0, width, height, m2, false);
                        }

                        val widthX = mModel.updateBitmap?.getWidth() //图片原始宽度
                        val heightY = mModel.updateBitmap?.getHeight() //图片原始高度
                        mModel.hw = "${heightY},${widthX}"
                        imgPost.setImageBitmap(mModel.updateBitmap)
                        mModel.uploadPicture()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        mModel.updateBitmap = null
                    }
                })

            val rotateAnimation: Animation =
                AnimationUtils.loadAnimation(mActivity, R.anim.rotate_anim)
            val lin = LinearInterpolator()
            rotateAnimation.interpolator = lin
            progressV.startAnimation(rotateAnimation)
        }

        mModel.numProgress.observe(this) {
            mBinding?.picpostTxt1?.text = getString(R.string.picpost_uploading) + "${it}%"
        }
        mModel.checkingProgress.observe(this) {
            mBinding?.checkingNum?.text = "${it}%"
        }
        mModel.taskBean.observe(this) {
            if (mModel.uploadSate.value == 2) mModel.queryAiTask(it.id)
        }
        mModel.loadFailed.observe(this) {
            if (it) {
                longToast(R.string.network_txt)
                mModel.cancelRepository()
                finish()
            }
        }


        mModel.uploadSate.observe(this) {
            if (it == 4) {
                finish()
            } else if (it == 3) {
                mModel.saveUserAiMedia()
            } else if (it == 2) {
                mBinding?.tvTitle?.text = getString(R.string.picpost_title1)
            }
        }
        mModel.faceId.observe(this) {
            if (it != "") {
                SwapFaceNewActivity.jump(this, it, "image")
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.isMaterial) {
            finish()
        }
    }

    fun onBackHome() {
        CancelPostDialog { taskCancel() }.showIgnoreState(this)
    }


    fun taskCancel() {
        val taskId = mModel.taskBean.value?.id ?: ""
        if (taskId != "") {
            launchRequestWithLoadingOnIO({ Repository.cancelTask(taskId) }) {
                onSuccess = {
                    finish()
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        } else {
            mModel.cancelRepository()
            finish()
        }

    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}