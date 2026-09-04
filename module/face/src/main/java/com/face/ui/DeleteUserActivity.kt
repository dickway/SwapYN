package com.face.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.bean.UserBean
import com.face.databinding.ActivityDeleteuserBinding
import com.face.util.GoogleLoginUtil
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.DeleteuserViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.longToast


class DeleteUserActivity : BaseBindingActivity<ActivityDeleteuserBinding, DeleteuserViewModel>(
    R.layout.activity_deleteuser,
    DeleteuserViewModel::class.java
) {

    private var countDownTimer = object : CountDownTimer(10000, 1000) {
        override fun onFinish() {
            mModel.isDisable.postValue(false)
        }

        override fun onTick(millisUntilFinished: Long) {
            mBinding?.tvTime?.text = "${millisUntilFinished / 1000 + 1}s"
        }
    }

    override fun init(savedInstanceState: Bundle?) {

        countDownTimer.start()
        mBinding?.apply {

            val fullText = getString(R.string.delete_user_txt2)
            val clickableText = getString(R.string.delete_user_txt_click)
            // 找到需要添加点击事件的文字的开始和结束位置
            val startIndex = fullText.indexOf(clickableText)
            val endIndex = startIndex + clickableText.length

            val spannableString = SpannableString(fullText)

            // 设置点击事件
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    openPlaySubscriptions()
                }
            }, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

            // 设置文字颜色
            spannableString.setSpan(
                ForegroundColorSpan(getColorX(R.color.colorFont6)),
                startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            // 添加下划线
            spannableString.setSpan(
                UnderlineSpan(),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // 设置TextView的文本为可点击
            tv3.text = spannableString
            tv3.movementMethod = android.text.method.LinkMovementMethod.getInstance()
        }
        mModel.isDelete.observe(this) {
            if (it) {
                GoogleLoginUtil.signOut(this)
                GVM.INSTANT.updateUserInfo(UserBean(), 4)
            }
        }
    }

    fun postDelete1() {
        BaseContentDialog(
            getString(R.string.delete_dialog_title),
            getString(R.string.delete_dialog_txt),
            getString(R.string.dialog_return),
            getString(R.string.delete_dialog_canceled),
            true,
            onRightData = { viewUpdate() })
            .showIgnoreState(this)
    }

    fun viewUpdate() {
        mModel.isDisable.postValue(true)
        mBinding?.apply {
            tv1.text = getString(R.string.deleteuser_account)
            tvAccount.text =
                if (GVM.INSTANT.userInfo.value.email == "") GVM.INSTANT.userInfo.value.guestName()
                else GVM.INSTANT.userInfo.value.email
            mModel.isNext.postValue(true)
        }
        countDownTimer.start()
    }

    fun postDelete2() {
        BaseContentDialog(
            getString(R.string.dialog_du_title),
            getString(R.string.dialog_du_txt),
            getString(R.string.dialog_return),
            getString(R.string.dialog_du_delete),
            true,
            onRightData = { mModel.deleteUserInfo() })
            .showIgnoreState(this)
    }


    fun openPlaySubscriptions() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
            setPackage("com.android.vending")  // 确保 Intent 打开的是 Google Play 应用
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            longToast(getString(R.string.google_info))
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}