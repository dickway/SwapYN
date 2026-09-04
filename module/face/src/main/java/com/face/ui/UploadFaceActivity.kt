package com.face.ui

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.databinding.ActivityUploadFaceBinding
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.CancelDialog
import com.face.view.UploadFailedDialog
import com.face.viewmodel.activity.UploadViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.toast


class UploadFaceActivity : BaseBindingActivity<ActivityUploadFaceBinding, UploadViewModel>(
    R.layout.activity_upload_face, UploadViewModel::class.java
) {


    val imgUrl by intentExtras("img_url", "")

    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            backAct()
        }

        EventUtil.inPage("addface")

        Glide.with(this@UploadFaceActivity).asBitmap()
            .load(imgUrl)
            .skipMemoryCache(true) // 不使用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //检查是否需要压缩
                    showLoading(getString(R.string.compress),false)
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
                        mModel.updateBitmap =
                            Bitmap.createBitmap(resource, 0, 0, width, height, m2, false);
                    }

                    mBinding?.apply {
                        val width = mModel.updateBitmap?.getWidth() ?: 1//图片原始宽度
                        val height = mModel.updateBitmap?.getHeight() ?: 1 //图片原始高度
                        val imgW = width * 335.dp / height
                        val maxImgW = getScreenWidth() - 40.dp
                        if (imgW > maxImgW) {
                            cardView.layoutParams.width = maxImgW
                            cardView.layoutParams.height = height * maxImgW / width
                        } else {
                            cardView.layoutParams.width = imgW
                            cardView.layoutParams.height = 335.dp
                        }
                        uploadImg.setImageBitmap(mModel.updateBitmap)
                        cardView.visibility = View.VISIBLE
                        mModel.uploadPicture()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    dismissLoading()
                    mModel.updateBitmap = null
                }
            })



        mModel.numProgress.observe(this) {
            mBinding?.tvProgress?.text = "${it}%"
            mBinding?.progress?.progress = it
        }

        mModel.taskBean.observe(this) {
            if (mModel.uploadSate.value == 1) {
                mModel.queryAiTask(it.id)
            }
        }

        mModel.uploadSate.observe(this) {
            when (it) {
                1 -> {
                    uploadLoadingUI()
                }

                2 -> {
                    uploadFailed()
                }

                3 -> {
                    uploadSuccess()
                }
            }
        }
    }


    /**
     * 上传失败
     * */
    fun uploadFailed() {
        UploadFailedDialog(mModel.tvFailure) { toOK() }.showIgnoreState(this)
    }

    /**
     * 退出界面
     * */
    fun backAct() {
        CancelDialog { toOK() }.showIgnoreState(this)
    }


    private fun toOK() {
        mModel.cancelRepository()
        finish()
    }

    /**
     * 上传成功
     * */
    fun uploadSuccess() {
        mBinding?.apply { //成功状态下控件显示隐藏
            conUp.visibility = View.VISIBLE
            linUp.visibility = View.GONE

            viewLine2.clearAnimation()
            viewLine1.visibility = View.GONE
            viewLine2.visibility = View.GONE

        }
    }

    /**
     * 上传状态
     * */
    fun uploadLoadingUI() {
        mBinding?.apply {//上传状态下控件显示隐藏
            val mAnimation = TranslateAnimation(
                TranslateAnimation.ABSOLUTE,
                0f,
                TranslateAnimation.ABSOLUTE,
                0f,
                TranslateAnimation.RELATIVE_TO_PARENT,
                0.5f,
                TranslateAnimation.RELATIVE_TO_PARENT,
                -0.5f
            )
            mAnimation.setDuration(2000)
            mAnimation.setRepeatCount(-1)
            mAnimation.repeatMode = Animation.REVERSE
            mAnimation.interpolator = LinearInterpolator()
            viewLine2.startAnimation(mAnimation)


            conUp.visibility = View.INVISIBLE
            linUp.visibility = View.VISIBLE
            viewLine1.visibility = View.VISIBLE
            viewLine2.visibility = View.VISIBLE

        }
    }

    fun onCompleteClick() {
        if (mModel.uploadImgPath.value == "") {
            toast(getString(R.string.fail))
            return
        }
        launchRequestWithLoadingOnIO({
            Repository.setHeaderType(
                mModel.uploadImgPath.value,
                mModel.uploadList.value[mModel.uploadTypeAdapter.selectIndex]
            )
        }) {
            onSuccess = {
                GVM.INSTANT.needRefresh.postValue(true)
                toast(getString(R.string.success))
                SPUtils.isMaterial = true
                finish();//结束当前activity
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mModel.cancelRepository()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
