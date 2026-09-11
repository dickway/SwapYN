package com.a.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import com.a.BR
import com.a.R
import com.a.activity.account.ALoginAccountActivity
import com.a.databinding.ActivityAloginBinding
import com.a.dialog.BaseYDialog
import com.blankj.utilcode.util.LogUtils
import com.face.util.GVM
import com.face.util.GoogleLoginUtil
import com.a.net.ARepository
import com.face.ui.MainActivity
import com.face.ui.WebActivity
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.face.view.BaseRedDialog
import com.face.viewmodel.InitViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ALoginActivity : BaseBindingActivity<ActivityAloginBinding, InitViewModel>(
    R.layout.activity_alogin,
    InitViewModel::class.java
) {

    val checked = MutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            AppManager.exitApp()
        }
        EventUtil.inPage("alogin_activity")

        GoogleLoginUtil.signOut(this@ALoginActivity)
    }

    fun onLoginAccount() {
        openActivity<ALoginAccountActivity>()
    }

    fun onCheckboxClick() {
        checked.value = !checked.value!!
    }

    fun onGoogleClick() {
        if (checked.value == true) {
            GoogleLoginUtil.signIn(this)
        } else {
            EventUtil.loginp("google")
            BaseYDialog(
                "Privacy Protection",
                getString(R.string.dialog_apri_content),
                getString(R.string.dialog_apri_ok),
                getString(R.string.dialog_apri_qixiao),
                unClick = "1",
                onBtnOK = {
                    onCheckboxClick()
                    GoogleLoginUtil.signIn(this)
                }
            ).showIgnoreState(mActivity)
            longToast("Please read and agree to the Privacy Policy")
        }
    }

    private fun login(type: String = "google", id: String, token: String) {
        showLoading()
        launch(Dispatchers.IO) {
            val loginConfig = async { ARepository.googleLogin(type, id, token) }
            if (loginConfig.await().mIsSuccess) {
                GVM.INSTANT.updateUserInfo(loginConfig.await().mData!!, 5)
                EventUtil.login(type)
                //登录后获取渠道
                launchRequestOnIO({ ARepository.getDeviceCampaign() }) {
                    onSuccess = { it ->
                        SPbaseUtils.loadAB = it ?: 0
                        toast(getString(com.face.R.string.success))
                        if (it == 0) {
                            openActivity<AMainActivity>()
                        } else {
                            GVM.INSTANT.isShowTool.postValue(it ?: 0)
                            openActivity<MainActivity>()
                        }
                        finish()
                    }
                    onComplete = {
                        dismissLoading()
                    }
                }
            } else {
                EventUtil.login(type, false, loginConfig.await().mError.toString())
                toast(getString(com.face.R.string.fail))
            }
        }
    }


    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(this, SPUtils.privacyPolicy, "Privacy Policy")
    }

    fun onSpik() {
        if (checked.value == true) {
            login("guest", id = SPUtils.deviceId, "")
        } else {
            EventUtil.loginp("guest")
            BaseYDialog(
                getString(R.string.dialog_apri_title),
                getString(R.string.dialog_apri_content),
                getString(R.string.dialog_apri_ok),
                getString(R.string.dialog_apri_qixiao),
                unClick = "1",
                onBtnOK = {
                    onCheckboxClick()
                    login("guest", id = SPUtils.deviceId, "")
                }
            ).showIgnoreState(mActivity)
            longToast("Please read and agree to the Privacy Policy")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GoogleLoginUtil.handleSignInResult(requestCode, resultCode, data) { id, token ->
            login(id = id, token = token)
        }
    }


    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}
