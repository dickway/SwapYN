package com.face.ui.accountlogin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.net.Repository
import com.face.BR
import com.face.databinding.ActivityCreateAccountBinding
import com.face.databinding.ActivityLoginAccountBinding
import com.face.databinding.ActivityUpdateAccountBinding
import com.face.ui.App
import com.face.ui.BaseBindingActivity
import com.face.ui.MainActivity
import com.face.ui.WebActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.BaseRedDialog
import com.face.viewmodel.activity.CreateAccountViewModel
import com.face.viewmodel.activity.LoginAccountViewModel
import com.face.viewmodel.activity.UpdateAccountViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoading
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
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


class UpdateAccountActivity :
    BaseBindingActivity<ActivityUpdateAccountBinding, UpdateAccountViewModel>(
        R.layout.activity_update_account,
        UpdateAccountViewModel::class.java
    ) {


    companion object {
        fun jump(
            context: Context,
            isLight: Boolean = false
        ) {
            context.openActivity<UpdateAccountActivity> {
                putBoolean("isLight", isLight)
            }
        }
    }

    val checked = MutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("login_update_activity")

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

            // 光标保持最后
            mBinding?.editPwd2?.setSelection(
                mBinding?.editPwd2?.text?.length ?: 0
            )

        }
    }

    fun onUpdate() {
        //无法点击
        if (!mModel.enableLogin.value) {
            toast(R.string.check_pwd)
            return
        }
        //密码不匹配
        if (mModel.pwd.value!=mModel.pwd2.value){
            toast(R.string.check_pwd)
            mModel.isErrer.postValue(true)
            return
        }
        launchRequestWithLoadingOnIO({
            Repository.updatePassword(  mModel.account.value ?: "",
                mModel.pwd.value ?: "")
        }) {
            onSuccess = {
                finish()
                toast(R.string.success)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }


    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}