package com.a.activity.account

import android.os.Bundle
import android.text.InputType
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.a.R
import com.a.databinding.ActivityAupdateAccountBinding
import com.a.viewmodel.AUpdateAccountViewModel
import com.a.net.ARepository
import com.a.BR
import com.face.ui.BaseBindingActivity
import com.face.util.EventUtil
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.toast


class AUpdateAccountActivity :
    BaseBindingActivity<ActivityAupdateAccountBinding, AUpdateAccountViewModel>(
        R.layout.activity_aupdate_account,
        AUpdateAccountViewModel::class.java
    ) {

    val checked = MutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("login_aupdate_activity")


//        mModel.enableLogin.observe(this, Observer {
//            mBinding?.login?.alpha = if (it) 1f else 0.3f
//        })

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

            // 光标保持最后
            mBinding?.editPwd?.setSelection(
                mBinding?.editPwd?.text?.length ?: 0
            )
        }
        mModel.passwordVisible2.observe(this) {
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
            toast("Invalid username or password")
            return
        }
        //密码不匹配
        if (mModel.pwd.value != mModel.pwd2.value) {
            toast("Invalid username or password")
            mModel.isErrer.postValue(true)
            return
        }
        launchRequestWithLoadingOnIO({
            ARepository.updatePassword(
                mModel.account.value ?: "",
                mModel.pwd.value ?: ""
            )
        }) {
            onSuccess = {
                finish()
                toast("Success")
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
