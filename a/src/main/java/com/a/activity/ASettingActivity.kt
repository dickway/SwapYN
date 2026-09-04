package com.a.activity

import android.os.Bundle
import android.view.View
import com.a.R
import com.a.databinding.ActivityAsettingsBinding
import com.a.viewmodel.ASettingViewModel
import com.face.util.GVM
import com.a.BR
import com.a.activity.account.AUpdateAccountActivity
import com.a.dialog.ABaseContentDialog
import com.a.dialog.ALogoutDialog
import com.face.bean.UserBean
import com.face.ui.SigninActivity
import com.face.ui.WebActivity
import com.face.util.GoogleLoginUtil
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class ASettingActivity : BaseBindingActivity<ActivityAsettingsBinding, ASettingViewModel>(
    R.layout.activity_asettings,
    ASettingViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        mBinding?.linDelete?.singleClick {
            openActivity<ADeleteUserActivity>()
        }
        if (GVM.INSTANT.userInfo.value.loginType != "app") {
            mBinding?.linUpdate?.visibility = View.GONE
        }
    }

    fun onUpdateClick() {
        openActivity<AUpdateAccountActivity>()
    }

    fun onLogout() {
        ALogoutDialog(::logout).showIgnoreState(this)
    }

    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(mActivity, SPUtils.privacyPolicy, "Privacy Policy")
    }

    fun onPoint() {
        openActivity<SigninActivity>()
    }

    fun onFaceAll() {
        openActivity<AMyFaceAllActivity>()
    }

    private fun logout() {
        GoogleLoginUtil.signOut(this)
        GVM.INSTANT.updateUserInfo(UserBean(), 4)
        openActivity<ALoginActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}