package com.a.dialog

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
import com.a.databinding.DialogAloginBinding
import com.face.util.GVM
import com.a.BR
import com.a.R
import com.face.net.Repository
import com.face.ui.WebActivity
import com.face.util.GoogleLoginUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast


class ALoginDialog : BaseBindingDF<DialogAloginBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {

    val checked = MutableLiveData(false)


    override fun init(savedInstanceState: Bundle?) {
    }

    fun onCheckboxClick() {
        checked.value = !checked.value!!
    }

    fun onGoogleClick() {
        GoogleLoginUtil.signOut(requireActivity())
        if (checked.value == true) {
            GoogleLoginUtil.signIn(this)
        } else {
            longToast("Please read and agree to the Privacy Policy")
        }
    }

    private fun login(id: String, token: String) {
        launchRequestWithLoadingOnIO({ Repository.bindGuestUser(id, token) }) {
            onSuccess = { bean ->
                if (bean != null) {
                    GVM.INSTANT.updateUserInfo(bean, 1)
                    toast(getString(R.string.asuccess))
                    dismissAllowingStateLoss()
                } else {
                    toast(getString(R.string.afail))
                }
            }
            onFailed = { _, errorCode, _ ->
                if (errorCode==4){
                    ABaseHintDialog(
                        "Cannot Link Google Account",
                        "This Google account is already registered and cannot be linked again. You can log in with this account or switch to another account to link.",
                        "Continue"
                    ).showIgnoreState(mActivity)
                }
            }
        }
    }

    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(context, SPUtils.privacyPolicy, "Privacy Policy")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GoogleLoginUtil.handleSignInResult(requestCode, resultCode, data) { id, token ->
            login(id, token)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}