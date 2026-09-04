package com.face.ui.accountlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.net.Repository
import com.face.BR
import com.face.databinding.ActivityLoginAccountBinding
import com.face.ui.App
import com.face.ui.BaseBindingActivity
import com.face.ui.MainActivity
import com.face.ui.WebActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.BaseRedDialog
import com.face.view.LoginNoticeDialog
import com.face.viewmodel.activity.LoginAccountViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.getValue


class LoginAccountActivity :
    BaseBindingActivity<ActivityLoginAccountBinding, LoginAccountViewModel>(
        R.layout.activity_login_account,
        LoginAccountViewModel::class.java
    ) {


    companion object {
        fun jump(
            context: Context,
            isLight: Boolean = false
        ) {
            context.openActivity<LoginAccountActivity> {
                putBoolean("isLight", isLight)
            }
        }
    }

    val checked = MutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("login_account_activity")
        mBinding?.tvWelcome1?.text = String.format(
            resources.getString(R.string.logon_agree1),
            resources.getString(com.key.R.string.app_ai_name)
        )

        mModel.passwordVisible.observe(this) {
            if (it) {
                // 显示密码
                mBinding?.editPwd?.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // 隐藏密码
                mBinding?.editPwd?.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            mBinding?.editPwd?.filters = arrayOf(
                InputFilter { source, _, _, _, _, _ ->
                    if (source.toString().matches(Regex("[a-zA-Z0-9]*"))) {
                        source
                    } else {
                        ""
                    }
                }
            )
            // 光标保持最后
            mBinding?.editPwd?.setSelection(
                mBinding?.editPwd?.text?.length ?: 0
            )

        }

    }

    fun onCheckboxClick() {
        checked.value = !checked.value!!
    }

    fun onCreateAccount() {
        CreateAccountActivity.jump(this)
    }


    private fun accountLogin() {
        showLoading()
        launch(Dispatchers.IO) {
            val loginConfig = async {
                Repository.accountLogin(
                    mModel.account.value ?: "",
                    mModel.pwd.value ?: ""
                )
            }
            if (loginConfig.await().mIsSuccess) {
                GVM.INSTANT.updateUserInfo(loginConfig.await().mData!!, 5)
                EventUtil.login("account")
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
                        finish()
                    }
                    onComplete = {
                        dismissLoading()
                    }
                }
            } else {
                EventUtil.login("account", false, loginConfig.await().mError.toString())
                dismissLoading()
                if (loginConfig.await().mCode==-3){
                    LoginNoticeDialog().showIgnoreState(this@LoginAccountActivity)
                }else{
                    toast(loginConfig.await().mMsg)
                }
            }
        }
    }

    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(this, SPUtils.privacyPolicy, "Privacy Policy")
    }

    fun onSpik() {
        if (!mModel.enableLogin.value) {
            toast(R.string.check_pwd)
            return
        }
        if (checked.value == true) {
            accountLogin()
        } else {
            EventUtil.loginp("account")
            BaseRedDialog(
                getString(R.string.dialog_pri_title),
                getString(R.string.dialog_pri_content),
                getString(R.string.dialog_pri_ok),
                getString(R.string.dialog_pri_qixiao),
                unClick = "1",
                onBtnOK = {
                    onCheckboxClick()
                    accountLogin()
                }
            ).showIgnoreState(mActivity)
            longToast(getString(R.string.hint_privacy))
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}