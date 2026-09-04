package com.a.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import com.a.databinding.ActivityAdeleteuserBinding
import com.a.viewmodel.ADeleteuserViewModel
import com.a.BR
import com.a.R
import com.a.dialog.ABaseContentDialog
import com.face.bean.UserBean
import com.face.util.GVM
import com.face.util.GoogleLoginUtil
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.longToast


class ADeleteUserActivity : BaseBindingActivity<ActivityAdeleteuserBinding, ADeleteuserViewModel>(
    R.layout.activity_adeleteuser,
    ADeleteuserViewModel::class.java
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

            val fullText = getString(R.string.delete_auser_txt2)
            val clickableText = getString(R.string.delete_auser_txt_click)
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
                ForegroundColorSpan(Color.parseColor("#3478F6")),
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
        ABaseContentDialog(
            "Account Deletion Pending",
            "Your account is about to be deleted. Please confirm that you have canceled any active subscriptions.",
            "Return",
            "Subscription Canceled",
            onCancelData = {
                viewUpdate()
            })
            .showIgnoreState(this)
    }

    fun viewUpdate() {
        mModel.isDisable.postValue(true)
        mBinding?.apply {
            tv1.text = "Account"
            tvAccount.text =
                if (GVM.INSTANT.userInfo.value.email == "") GVM.INSTANT.userInfo.value.guestName()
                else GVM.INSTANT.userInfo.value.email
            mModel.isNext.postValue(true)
        }
        countDownTimer.start()
    }

    fun postDelete2() {
        ABaseContentDialog(
            getStringX(R.string.dialog_adu_title),
            getStringX(R.string.dialog_adu_txt),
            getStringX(R.string.dialog_areturn),
            "Delete",
            onCancelData = {
                mModel.deleteUserInfo()
            })
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
            longToast("Google Play app not found")
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}