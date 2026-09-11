package com.a.activity

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.a.BR
import com.a.R
import com.a.databinding.ActivityAuploadFaceBinding
import com.a.dialog.ABaseContentDialog
import com.a.dialog.AUploadFailedDialog
import com.a.viewmodel.AUploadViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.a.net.ARepository
import com.face.ui.BaseBindingActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.toast

class AUploadFaceActivity : BaseBindingActivity<ActivityAuploadFaceBinding, AUploadViewModel>(
    R.layout.activity_aupload_face, AUploadViewModel::class.java
) {
    val imgUrl by intentExtras("img_url", "")
    private var uploadTarget: CustomTarget<Bitmap>? = null
    private var uploadRequests: RequestManager? = null
    private var failureDialogShown = false

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("addface")
        onBackPressedDispatcher.addCallback(this) { backAct() }

        mModel.numProgress.observe(this) {
            mBinding?.progress?.progress = it.coerceIn(0, 100)
        }
        mModel.taskBean.observe(this) {
            if (mModel.uploadSate.value == 1) {
                mModel.queryAiTask(it.id)
            }
        }
        mModel.uploadSate.observe(this) {
            when (it) {
                0, 1 -> uploadLoadingUI()
                2 -> uploadFailed()
                3 -> uploadSuccess()
            }
        }

        uploadTarget = object : CustomTarget<Bitmap>() {
            override fun onStart() {
                showLoading(getStringX(com.face.R.string.compress))
                super.onStart()
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                if (isFinishing || isDestroyed) return
                mModel.updateBitmap = resource
                val width = resource.width
                val height = resource.height
                var inSampleSize = 1
                val max = 10 * 1024 * 1024
                while (width * height * 4 / inSampleSize > max) {
                    inSampleSize *= 2
                }
                if (inSampleSize > 1) {
                    val matrix = Matrix().apply {
                        setScale(1 / inSampleSize.toFloat(), 1 / inSampleSize.toFloat())
                    }
                    mModel.updateBitmap = Bitmap.createBitmap(resource, 0, 0, width, height, matrix, false)
                }
                mBinding?.uploadImg?.setImageBitmap(mModel.updateBitmap)
                mModel.uploadPicture()
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                dismissLoading()
                if (!isFinishing && !isDestroyed) mModel.uploadSate.value = 2
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                mModel.updateBitmap = null
            }
        }
        val requests = Glide.with(this)
        uploadRequests = requests
        requests.asBitmap()
            .load(imgUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(uploadTarget!!)
    }

    fun uploadFailed() {
        dismissLoading()
        configurePreview(success = false)
        mBinding?.apply {
            viewLine2.clearAnimation()
            cardView2.visibility = View.VISIBLE
            linUp.visibility = View.VISIBLE
            progress.visibility = View.GONE
            tvComplete.visibility = View.GONE
        }
        showFailureDialog()
    }

    private fun showFailureDialog() {
        if (isFinishing || isDestroyed || supportFragmentManager.isStateSaved || failureDialogShown) return
        if (supportFragmentManager.findFragmentByTag(FAILURE_DIALOG_TAG) == null) {
            AUploadFailedDialog().show(supportFragmentManager.beginTransaction(), FAILURE_DIALOG_TAG)
        }
        failureDialogShown = true
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (mModel.uploadSate.value == 2) showFailureDialog()
    }

    fun backAct() {
        if (mModel.uploadSate.value == 2 || mModel.uploadSate.value == 3) {
            finish()
            return
        }
        ABaseContentDialog(
            getString(R.string.a_upload_cancel_title),
            getString(com.face.R.string.cancel_content),
            getString(com.face.R.string.cancel),
            getString(com.face.R.string.confirm),
            onCancelData = {
                mModel.cancelRepository()
                finish()
            }
        ).showIgnoreState(this)
    }

    fun uploadSuccess() {
        dismissLoading()
        configurePreview(success = true)
        mBinding?.apply {
            uploadImg.loadImage(mModel.uploadImgPath.value, placeholderResId = com.key.R.drawable.img_default_m)
            viewLine2.clearAnimation()
            cardView2.visibility = View.GONE
            linUp.visibility = View.GONE
            tvComplete.visibility = View.VISIBLE
        }
    }

    fun uploadLoadingUI() {
        configurePreview(success = false)
        mBinding?.apply {
            viewLine2.clearAnimation()
            viewLine2.startAnimation(
                TranslateAnimation(
                    Animation.ABSOLUTE, 0f,
                    Animation.ABSOLUTE, 0f,
                    Animation.RELATIVE_TO_PARENT, 0.45f,
                    Animation.RELATIVE_TO_PARENT, -0.45f
                ).apply {
                    duration = 2000
                    repeatCount = Animation.INFINITE
                    repeatMode = Animation.REVERSE
                    interpolator = LinearInterpolator()
                }
            )
            cardView2.visibility = View.VISIBLE
            linUp.visibility = View.VISIBLE
            progress.visibility = View.VISIBLE
            tvComplete.visibility = View.GONE
        }
    }

    private fun configurePreview(success: Boolean) {
        mBinding?.apply {
            val margin = if (success) 16.dp else 0
            cardView.layoutParams = (cardView.layoutParams as ConstraintLayout.LayoutParams).apply {
                marginStart = margin
                marginEnd = margin
                bottomMargin = margin
            }
            uploadImg.shapeAppearanceModel = uploadImg.shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(if (success) 48.dp.toFloat() else 0f)
                .build()
            viewSuccessBorder.visibility = if (success) View.VISIBLE else View.GONE
        }
    }

    fun onCompleteClick() {
        if (mModel.uploadImgPath.value == "") {
            toast(getStringX(R.string.afail))
            return
        }
        launchRequestWithLoadingOnIO({
            ARepository.setHeaderType(
                mModel.uploadImgPath.value,
                mModel.uploadList.value[mModel.uploadTypeAdapter.selectIndex]
            )
        }) {
            onSuccess = {
                GVM.INSTANT.needRefresh.postValue(true)
                toast(getStringX(R.string.asuccess))
                SPUtils.isMaterial = true
                finish()
            }
            onFailed = { _, _, errorMsg -> toast(errorMsg) }
        }
    }

    override fun onDestroy() {
        mBinding?.viewLine2?.clearAnimation()
        uploadTarget?.let { uploadRequests?.clear(it) }
        uploadTarget = null
        uploadRequests = null
        mModel.cancelRepository()
        super.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }

    companion object {
        private const val FAILURE_DIALOG_TAG = "upload-face-failure"
    }
}
