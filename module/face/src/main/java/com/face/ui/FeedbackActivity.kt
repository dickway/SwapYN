package com.face.ui

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import coil.load
import com.face.util.GVM
import com.face.viewmodel.activity.FeedbackViewModel
import com.face.BR
import com.face.R
import com.face.databinding.ActivityFeedbackBinding
import com.face.util.EventUtil
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.base.ViewEffect
import com.zzkj.structure.ui.dialog.StatusDialog
import com.zzkj.structure.util.ImgLoader
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import kotlinx.coroutines.delay

class FeedbackActivity : BaseBindingActivity<ActivityFeedbackBinding, FeedbackViewModel>(
    R.layout.activity_feedback,
    FeedbackViewModel::class.java
) {
    private lateinit var pickPictureLauncher: ActivityResultLauncher<Array<String>>
    val isList by intentExtras("isList", false)
    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("feedback")
        pickPictureLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                mModel.handleUri(it)
            }
        }
        mModel.fileUri.observe(this) {
            mBinding?.apply {
                imgFile.load(it, ImgLoader.imageLoader)
                imgDelete.isVisible = it != null
                imgPlay.isVisible = (it != null && !mModel.isImage)
                if (it == null) {
                    imgFile.setBackgroundResource(com.key.R.drawable.img_faceback_icon_add)
                } else {
                    imgFile.background = null
                }
            }
        }
        mModel.feedBackFinish.observe(this) {
            if (it) {
                onMsgClick()
                finish()
            }
        }
        if (isList)
            mBinding?.conMsg?.visibility = View.GONE
    }

    override fun handleCommonEffect(effect: ViewEffect?) {
        if (effect is ViewEffect.SimpleCommonEffect) {
            when (effect.type) {
                FeedbackViewModel.VIEW_EFFECT_FEEDBACK_SUCCESS -> {
                    onMsgClick()
                    finish()
                }

                FeedbackViewModel.VIEW_EFFECT_UPDATE_FILE_SUCCESS -> {
                    StatusDialog(this).apply {
                        setImgResId(com.key.R.drawable.img_feedback_success)
                        setText(R.string.success)
                        show()
                        launch {
                            delay(1500)
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    fun onConfirmClick() {
        mModel.saveFeedback()
    }

    fun onFileClick() {
        val uri = mModel.fileUri.value
        if (uri == null) {
            pickPictureLauncher.launch(arrayOf("image/*", "video/*"))
        }
//        else if (mModel.isImage) {
//            PreviewPictureActivity.jump(this, uri.toString())
//        } else {
//            ARouter.getInstance()
//                .build(Router.ROUTE_LOCAL_PLAYER)
//                .withString("uri", uri.toString())
//                .navigation()
//        }
    }

    fun onMsgClick() {
        openActivity<FeedbackHistoryActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}