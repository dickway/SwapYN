package com.a.activity.account

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.a.R
import com.a.databinding.ActivityAcreateAccountBinding
import com.a.viewmodel.ACreateAccountViewModel
import com.a.net.ARepository
import com.a.BR
import com.a.activity.AMainActivity
import com.a.dialog.BaseYDialog
import com.face.ui.BaseBindingActivity
import com.face.ui.MainActivity
import com.face.ui.WebActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async


class ACreateAccountActivity :
    BaseBindingActivity<ActivityAcreateAccountBinding, ACreateAccountViewModel>(
        R.layout.activity_acreate_account,
        ACreateAccountViewModel::class.java
    ) {


    companion object {
        fun jump(
            context: Context,
            isLight: Boolean = false
        ) {
            context.openActivity<ACreateAccountActivity> {
                putBoolean("isLight", isLight)
            }
        }
    }

    val checked = MutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("create_aaccount_activity")
//        mModel.enableLogin.observe(this, Observer {
//            mBinding?.login?.alpha = if (it) 1f else 0.3f
//        })
        mModel.passwordVisible.observe(this){
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
        mModel.passwordVisible2.observe(this){
            if (it) {
                // 显示密码
                mBinding?.editPwd2?.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // 隐藏密码
                mBinding?.editPwd2?.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            mBinding?.editPwd2?.filters = arrayOf(
                InputFilter { source, _, _, _, _, _ ->
                    if (source.toString().matches(Regex("[a-zA-Z0-9]*"))) {
                        source
                    } else {
                        ""
                    }
                }
            )
            // 光标保持最后
            mBinding?.editPwd2?.setSelection(
                mBinding?.editPwd2?.text?.length ?: 0
            )

        }
    }

    fun onCheckboxClick() {
        checked.value = !checked.value!!
    }

    private fun createLogin() {
        showLoading()
        launch(Dispatchers.IO) {
            val loginConfig = async {
                ARepository.accountRegister(
                    mModel.account.value ?: "",
                    mModel.pwd.value ?: ""
                )
            }
            if (loginConfig.await().mIsSuccess) {
                GVM.INSTANT.updateUserInfo(loginConfig.await().mData!!, 5)
                EventUtil.login("create")
                //登录后获取渠道
                launchRequestOnIO({ ARepository.getDeviceCampaign() }) {
                    onSuccess = { it ->
                        if (GVM.INSTANT.isShowTool.value != it) {//切换账号后不一致就重启app
                            GVM.INSTANT.isEventA.postValue(-1)
                        }

                        GVM.INSTANT.isShowTool.postValue(it ?: 0)
                        SPbaseUtils.loadAB = it ?: 0
                        toast("Success")
                        if (it == 0) {
                            openActivity<AMainActivity>()
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
                EventUtil.login("create", false, loginConfig.await().mError.toString())
                toast(loginConfig.await().mMsg)
                dismissLoading()
            }
        }
    }

    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(this, SPUtils.privacyPolicy, "Privacy Policy")
    }

    fun onSpik() {
        //无法点击
        if (!mModel.enableLogin.value){
            toast("Invalid username or password")
            return
        }
        //密码不匹配
        if (mModel.pwd.value!=mModel.pwd2.value){
            mModel.isErrer.postValue(true)
            toast("Invalid username or password")
            return
        }
        if (checked.value == true) {
            createLogin()
        } else {
            EventUtil.loginp("create")
            BaseYDialog(
                getString(R.string.dialog_apri_title),
                getString(R.string.dialog_apri_content),
                getString(R.string.dialog_apri_ok),
                getString(R.string.dialog_apri_qixiao),
                unClick = "1",
                onBtnOK = {
                    onCheckboxClick()
                    createLogin()
                }
            ).showIgnoreState(mActivity)
            longToast("Please read and agree to the Privacy Policy")
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}
