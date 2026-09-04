package com.face.view

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import com.face.BR
import com.face.R
import com.face.databinding.DialogRedBinding
import com.face.ui.WebActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.setIfNot

class BaseRedDialog @JvmOverloads constructor(
    val title: String = "",
    val content: String = "",
    val confirmStr: String = "",
    val cancelStr: String = "",
    val unLine: Boolean = true,
    val unClick: String = "",
    val btnColor: String = "",
    private val onBtnOK: (() -> Unit)? = null,
    private val onBtnCancel: (() -> Unit)? = null
) : BaseBindingDF<DialogRedBinding>(
    horizontalPadding = 48.dp,
    isBottomAnimation = false,
) {
    var checkLine = NotNullMutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        if (btnColor.isNotEmpty()){
            mBinding?.btnCancel?.setTextColor(Color.parseColor(btnColor))
        }
        checkLine.setIfNot(unLine)
        mBinding?.apply {
            mBinding?.tvTitle?.text = title
            mBinding?.tvContent?.text = content
            mBinding?.btnConfirm?.text = confirmStr
            mBinding?.btnCancel?.text = cancelStr

            if (unClick == "1") {
                try {
                    val fullText = content
                    val clickableText = getString(R.string.dialog_pri_content_click)
                    // 找到需要添加点击事件的文字的开始和结束位置
                    val startIndex = fullText.indexOf(clickableText)
                    val endIndex = startIndex + clickableText.length
                    val spannableString = SpannableString(fullText)
                    // 设置点击事件
                    spannableString.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            WebActivity.openPrivacyPolicy(
                                mActivity,
                                SPUtils.privacyPolicy,
                                "Privacy Policy"
                            )
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
                    mBinding?.tvContent?.text = spannableString
                    mBinding?.tvContent?.movementMethod =
                        android.text.method.LinkMovementMethod.getInstance()
                }catch (e:Exception){
                    mBinding?.tvContent?.text = content
                }
            }

        }
    }

    fun onConfirmClick() {
        onBtnOK?.invoke()
        dismissAllowingStateLoss()
    }

    fun onCancelClick() {
        onBtnCancel?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}