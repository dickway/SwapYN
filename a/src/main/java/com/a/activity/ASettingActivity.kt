package com.a.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.a.BR
import com.a.R
import com.a.activity.account.AUpdateAccountActivity
import com.a.databinding.ActivityAsettingsBinding
import com.a.dialog.ALogoutDialog
import com.a.view.ALanguagePopup
import com.a.viewmodel.ASettingViewModel
import com.face.bean.UserBean
import com.face.ui.BaseBindingActivity
import com.face.ui.SigninActivity
import com.face.ui.WebActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GoogleLoginUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class ASettingActivity : BaseBindingActivity<ActivityAsettingsBinding, ASettingViewModel>(
    R.layout.activity_asettings,
    ASettingViewModel::class.java
) {
    private var languagePopup: ALanguagePopup? = null

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.linDelete?.singleClick {
            openActivity<ADeleteUserActivity>()
        }
        if (GVM.INSTANT.userInfo.value.loginType != "app") {
            mBinding?.linUpdate?.visibility = View.GONE
        }
        mBinding?.tvLanguage?.text = ALanguagePopup.labelFor(this, SPbaseUtils.spLanguage)
    }

    fun onLanguageClick() {
        val anchor = mBinding?.linc1 ?: return
        languagePopup?.dismiss()
        languagePopup = ALanguagePopup(this, SPbaseUtils.spLanguage) { language ->
            if (language != SPbaseUtils.spLanguage) {
                SPbaseUtils.spLanguage = language
                SPbaseUtils.spCacheLanguage = language
                EventUtil.languageChange(language)
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
            }
        }.also { it.show(anchor) }
    }

    override fun onDestroy() {
        languagePopup?.dismiss()
        super.onDestroy()
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
