package com.a.activity

import android.graphics.Bitmap
import android.graphics.Color
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
import com.a.R
import com.a.databinding.ActivityAuploadFaceBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.AUploadViewModel
import com.a.BR
import com.face.net.Repository
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.toast


class AUploadFaceActivity : BaseBindingActivity<ActivityAuploadFaceBinding, AUploadViewModel>(
    R.layout.activity_aupload_face, AUploadViewModel::class.java
) {


    val imgUrl by intentExtras("img_url", "")

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("addface")
        onBackPressedDispatcher.addCallback(this) {
            backAct()
        }
        Glide.with(this@AUploadFaceActivity).asBitmap()
            .load(imgUrl)
            .skipMemoryCache(true) // 不使用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {
                override fun onStart() {
                    showLoading("compress")
                    super.onStart()
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
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
                        mModel.updateBitmap =
                            Bitmap.createBitmap(resource, 0, 0, width, height, m2, false);
                    }

                    mBinding?.apply {
//                        val width = mModel.updateBitmap?.getWidth() ?: 1//图片原始宽度
//                        val height = mModel.updateBitmap?.getHeight() ?: 1 //图片原始高度
//                        val imgW = width * 380.dp / height
//                        val maxImgW = getScreenWidth() - 40.dp
//                        if (imgW > maxImgW) {
//                            cardView.layoutParams.width = maxImgW
//                            cardView.layoutParams.height = height * maxImgW / width
//                        } else {
//                            cardView.layoutParams.width = imgW
//                            cardView.layoutParams.height = 380.dp
//                        }
                        uploadImg.setImageBitmap(mModel.updateBitmap)
                        cardView.visibility = View.VISIBLE
                        mModel.uploadPicture()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    mModel.updateBitmap = null
                }
            })



        mModel.numProgress.observe(this) {
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
        mBinding?.apply { //成功状态下控件显示隐藏
            linE.visibility = View.VISIBLE
            viewLine2.clearAnimation()
            cardView2.visibility = View.GONE
            tvRetry.visibility = View.VISIBLE
            progress.visibility = View.GONE
            tvHint.text = "Face without cover, no glasses, no hat, try to face"
            tvHint.setTextColor(Color.parseColor("#B2B2B2"))
            tvComplete.visibility = View.GONE
        }
    }

    /**
     * 退出界面
     * */
    fun backAct() {
        ABaseContentDialog(
            "Cancel Upload",
            "Whether to cancel facial image upload",
            "Cancel",
            "Confirm",
            onCancelData = {
                mModel.cancelRepository()
                finish()
            })
            .showIgnoreState(this)
    }


    /**
     * 上传成功
     * */
    fun uploadSuccess() {
        mBinding?.apply { //成功状态下控件显示隐藏
            mBinding?.uploadImg?.loadImage(mModel.uploadImgPath.value, placeholderResId = com.key.R.drawable.img_default_m)
            linE.visibility = View.GONE
            progress.visibility = View.GONE
            tvHint2.visibility=View.VISIBLE
            tvHint.visibility=View.GONE
            viewLine2.clearAnimation()
            cardView2.visibility = View.GONE
            tvRetry.visibility = View.GONE
            tvComplete.visibility = View.VISIBLE
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

            linE.visibility = View.GONE
            progress.visibility = View.VISIBLE
            cardView2.visibility = View.VISIBLE
            tvComplete.visibility = View.GONE
            tvRetry.visibility = View.GONE
            tvHint2.visibility=View.GONE
            tvHint.visibility=View.VISIBLE
        }
    }

    fun onCompleteClick() {
        if (mModel.uploadImgPath.value == "") {
            toast(getStringX(R.string.afail))
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
                toast(getStringX(R.string.asuccess))
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