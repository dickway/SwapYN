package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.BR
import com.face.databinding.DialogExitSettingBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData

/**
 * @author 再战科技
 * @date 2022/4/19
 * @description
 */
class ExitSettingDialog @JvmOverloads constructor(
    val content: String = "",
    val btnOk: String = "",
    private val onCloseApp: (() -> Unit)? = null
) : BaseBindingDF<DialogExitSettingBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true,
    cancelable = false
) {
    var checked = NotNullMutableLiveData(SPUtils.openHomeShow)

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.tvContent?.text = content
        mBinding?.dialogBtn?.text = btnOk
    }

    fun onCheckboxClick(num:Int) {
        checked.value = num
    }

    fun onConfirmClick() {
        SPUtils.openHomeShow = checked.value
        SPUtils.exitApp = false
        dismissAllowingStateLoss()
        onCloseApp?.invoke()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}