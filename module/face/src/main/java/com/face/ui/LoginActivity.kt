package com.face.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import com.face.util.GVM
import com.face.util.GoogleLoginUtil
import com.face.R
import com.face.net.Repository
import com.face.BR
import com.face.databinding.ActivityLoginBinding
import com.face.ui.accountlogin.LoginAccountActivity
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.face.view.BaseRedDialog
import com.face.view.HomeNoticeDialog
import com.face.view.LoginNoticeDialog
import com.face.viewmodel.InitViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/**
 * @author 再战科技
 * @date 2023/10/11
 * @description 登录界面
 */
class LoginActivity : BaseBindingActivity<ActivityLoginBinding, InitViewModel>(
    R.layout.activity_login,
    InitViewModel::class.java
) {

    val checked = MutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            AppManager.exitApp()
        }
        SPUtils.tokenUser=""
        AppManager.finishActivity(LoginActivity::class.java)
        EventUtil.inPage("login_activity")

        GoogleLoginUtil.signOut(this@LoginActivity)

        mBinding?.imgFace?.apply {
            layoutParams.width = getScreenWidth()
            layoutParams.height = getScreenWidth() * 4 / 3
        }

        mBinding?.tvWelcome1?.text= String.format(
            resources.getString(R.string.logon_agree1),
            resources.getString(com.key.R.string.app_ai_name)
        )

//        Glide.with(this@LoginActivity)
//            .load(SPUtils.startMp4)
//            .placeholder(com.key.R.drawable.img_pho)
//            .skipMemoryCache(true)
//            .into(mBinding?.imgFace!!)
    }

    fun onCheckboxClick() {
        checked.value = !checked.value!!
    }

    fun onLoginAccount() {
        LoginAccountActivity.jump(this)
    }



    fun onGoogleClick() {
        if (checked.value == true) {
            GoogleLoginUtil.signIn(this)
        } else {
            EventUtil.loginp("google")
            BaseRedDialog(
                getString(R.string.dialog_pri_title),
                getString(R.string.dialog_pri_content),
                getString(R.string.dialog_pri_ok),
                getString(R.string.dialog_pri_qixiao),
                unClick = "1",
                onBtnOK = {
                    onCheckboxClick()
                    GoogleLoginUtil.signIn(this)
                }
            ).showIgnoreState(mActivity)
            longToast(getString(R.string.hint_privacy))
        }
    }

    private fun login(type: String = "google", id: String, token: String) {
        showLoading()
        launch(Dispatchers.IO) {
            val loginConfig = async { Repository.googleLogin(type, id, token) }
            if (loginConfig.await().mIsSuccess) {
                GVM.INSTANT.updateUserInfo(loginConfig.await().mData!!, 5)
                EventUtil.login(type)
                //登录后获取渠道
                launchRequestOnIO({ Repository.getDeviceCampaign() }) {
                    onSuccess = { it ->
                        if (GVM.INSTANT.isShowTool.value != it) {//切换账号后不一致就重启app
                            GVM.INSTANT.isEventA.postValue(-1)
                        }

                        GVM.INSTANT.isShowTool.postValue(it ?: 0)
                        SPbaseUtils.loadAB = it ?: 0
                        toast(getString(R.string.success))
                        if (it == 0) {
                            val mIntent = Intent().setClassName(
                                App.INSTANCE.packageName,
                                SPUtils.aMainName
                            )
                            startActivity(mIntent)
                        } else {
                            openActivity<MainActivity>()
                        }
                    }
                    onComplete = {
                        dismissLoading()
                    }
                }
            } else {
                dismissLoading()
                EventUtil.login(type, false, loginConfig.await().mError.toString())
                if (loginConfig.await().mCode==-3){
                    LoginNoticeDialog().showIgnoreState(this@LoginActivity)
                }else{
                    toast(getString(R.string.fail))
                }
            }
        }


//        showLoading()
//        launchRequestOnIO({ Repository.googleLogin(type, id, token) }) {
//            onSuccess = { bean ->
//                if (bean != null) {
//                    GVM.INSTANT.updateUserInfo(bean, 5)
//                    //登录后获取渠道
//                    launchRequestOnIO({ Repository.getDeviceCampaign() }) {
//                        onSuccess = { it ->
//                            GVM.INSTANT.isShowTool.postValue(it ?: 0)
//                            SPbaseUtils.loadAB = it ?: 0
//                            GVM.INSTANT.isEventA.postValue(it ?: 0)
//                            toast(getString(R.string.success))
//                            openActivity<MainActivity>()
//                            finish()
//                        }
//                        onComplete={
//                            dismissLoading()
//                        }
//                    }
//                }
//            }
//            onFailed = { _, _, errorMsg ->
//                toast(errorMsg)
//                dismissLoading()
//            }
//        }
    }

    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(this, SPUtils.privacyPolicy, "Privacy Policy")
    }

    fun onSpik() {
        if (checked.value == true) {
            login("guest", id = SPUtils.deviceId, "")
        } else {
            EventUtil.loginp("guest")
            BaseRedDialog(
                getString(R.string.dialog_pri_title),
                getString(R.string.dialog_pri_content),
                getString(R.string.dialog_pri_ok),
                getString(R.string.dialog_pri_qixiao),
                unClick = "1",
                onBtnOK = {
                    onCheckboxClick()
                    login("guest", id = SPUtils.deviceId, "")
                }
            ).showIgnoreState(mActivity)
            longToast(getString(R.string.hint_privacy))
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