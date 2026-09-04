package com.a.dialog

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import com.a.R
import com.a.BR
import com.a.databinding.DialogYBinding
import com.face.ui.WebActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getColorX

class BaseYDialog @JvmOverloads constructor(
    val title: String = "",
    val content: String = "",
    val confirmStr: String = "",
    val cancelStr: String = "",
    val unClick: String = "",
    private val onBtnOK: (() -> Unit)? = null,
    private val onBtnCancel: (() -> Unit)? = null
) : BaseBindingDF<DialogYBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            mBinding?.tvTitle?.text = title
            mBinding?.tvContent?.text = content
            mBinding?.btnConfirm?.text = confirmStr
            mBinding?.btnCancel?.text = cancelStr

            if (unClick == "1") {
                try {
                    val fullText = content
                    val clickableText = getString(R.string.dialog_apri_content_click)
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
                        ForegroundColorSpan(getColorX(R.color.colorBgC4FA47)),
                        startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    )

                    // 添加下划线
//                    spannableString.setSpan(
//                        UnderlineSpan(),
//                        startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )

                    // 加粗
                    spannableString.setSpan(
                        StyleSpan(Typeface.BOLD),
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