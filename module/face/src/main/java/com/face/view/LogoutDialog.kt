package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.bean.UserBean
import com.face.databinding.DialogLogoutBinding
import com.face.ui.LoginActivity
import com.face.util.GoogleLoginUtil
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.openActivity


class LogoutDialog @JvmOverloads constructor(
//    private val onCloseApp: (() -> Unit)? = null
) : BaseBindingDF<DialogLogoutBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onConfirmClick() {
        onLogout()
        dismissAllowingStateLoss()
    }
    fun onLogout() {
        GoogleLoginUtil.signOut(requireActivity())
        GVM.INSTANT.updateUserInfo(UserBean(), 4)
    }
    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}