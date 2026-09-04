package com.face.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.databinding.DialogLoginBinding
import com.face.ui.WebActivity
import com.face.util.GoogleLoginUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.toast


class LoginDialog @JvmOverloads constructor(
) : BaseBindingDF<DialogLoginBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {

    val checked = MutableLiveData(false)


    override fun init(savedInstanceState: Bundle?) {
        mBinding?.tvWelcome1?.text= String.format(
            resources.getString(R.string.logon_agree1),
            resources.getString(com.key.R.string.app_ai_name)
        )
        mBinding?.txtCon?.text= String.format(
            resources.getString(R.string.log_in),
            resources.getString(com.key.R.string.app_ai_name)
        )

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
                    toast(getString(R.string.success))
                    dismissAllowingStateLoss()
                } else {
                    toast(getString(R.string.fail))
                }
            }
            onFailed = { _, errorCode, errorMsg ->
                if (errorCode==4){
                    BaseHintDialog(
                        getString(R.string.dialog_hint_title),
                        getString(R.string.dialog_hint_content),
                        getString(R.string.dialog_hint_ok)
                    ).showIgnoreState(mActivity)
                }
            }
        }

        //之前无登录弹窗功能
//        showLoading()
//        launch(Dispatchers.IO) {
//            val loginConfig = async { Repository.googleLogin(id = id, token = token) }
//            val isDevice = async(start = CoroutineStart.LAZY) {//获取渠道在登录之后
//                Repository.getDeviceCampaign()
//            }
//            if (loginConfig.await().mIsSuccess) {
//                //it == 0 自然量
//                GVM.INSTANT.isShowTool.postValue(isDevice.await().mData ?: 0)
//                GVM.INSTANT.updateUserInfo(loginConfig.await().mData!!, 5)
//                toast(getString(R.string.success))
//                GVM.INSTANT.notLoginRefresh.postValue(true)
//                dismissAllowingStateLoss()
//            } else {
//                toast(getString(R.string.fail))
//            }
//            dismissLoading()
//        }
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