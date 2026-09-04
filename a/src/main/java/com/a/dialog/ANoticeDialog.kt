package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.a.BR
import com.a.databinding.DialogAnoticeBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp

class ANoticeDialog @JvmOverloads constructor(
    private val onBtnOK: (() -> Unit)? = null,
    private val onBtnCancel: (() -> Unit)? = null
) : BaseBindingDF<DialogAnoticeBinding>(
    gravity = Gravity.CENTER,
    horizontalPadding = 36.dp,
    isBottomAnimation = false,
    cancelable = false
) {
    var checked = NotNullMutableLiveData(false)


    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        if (SPUtils.noticeTitle.trim() == "1") mBinding?.tvTitle?.visibility = View.GONE
        if (SPUtils.noticeBtnOk.trim() == "1") mBinding?.btnConfirm?.visibility = View.GONE
        if (SPUtils.noticeBtnDisagree.trim() == "1") mBinding?.btnCancel?.visibility = View.GONE

        mBinding?.apply {
            mBinding?.tvTitle?.text = SPUtils.noticeTitle
            mBinding?.tvContent?.text = SPUtils.noticeContent
            mBinding?.btnConfirm?.text = SPUtils.noticeBtnOk
            mBinding?.btnCancel?.text = SPUtils.noticeBtnDisagree
        }
    }

    fun onCheckboxClick() {
        checked.value = !checked.value
    }


    fun onConfirmClick() {
        SPUtils.noticeShow=checked.value
        onBtnOK?.invoke()
        dismissAllowingStateLoss()
    }

    fun onCancelClick() {
        onBtnCancel?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}