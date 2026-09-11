package com.a.activity

import android.os.Bundle
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.view.doOnLayout
import com.a.R
import com.a.databinding.ActivityAvipBinding
import com.a.viewmodel.AVipViewModel
import com.android.billingclient.api.BillingClient
import com.a.BR
import com.a.dialog.ALoginDialog
import com.a.dialog.BaseYDialog
import com.face.ui.WebActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.util.SPUtils
import com.face.view.PurchaseErrorDialog
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast

class AVipActivity : BaseBindingActivity<ActivityAvipBinding, AVipViewModel>(
    R.layout.activity_avip,
    AVipViewModel::class.java
) {
    private var renewalTerms = ""
    private var renewalTermsExpanded = false

    override fun init(savedInstanceState: Bundle?) {
        GVM.INSTANT.payPage.value = "Avip"
        EventUtil.inPage("vip")
        mModel.vipSchemeAdapter.onItemClick = onSchemeClick@{ _, bean, position ->
            if (bean == null || position < 0) return@onSchemeClick
            mBinding?.recyclerView?.smoothScrollToPosition(position)
            mModel.vipSchemeAdapter.selectIndex = position
            mModel.selectedScheme.postValue(bean)
        }

        mModel.selectedScheme.observe(this) {
            val selectedIndex = mModel.schemes.value.indexOfFirst { scheme ->
                scheme.id == it.id && scheme.productId == it.productId
            }
            if (selectedIndex >= 0) {
                mModel.vipSchemeAdapter.selectIndex = selectedIndex
            }
            var riqi = ""
            when (it.day) {
                1 -> {
                    riqi = it.getBeanPrice() + "/Day"
                }

                7 -> {
                    riqi = it.getBeanPrice() + "/Week"
                }

                28, 29, 30, 31, 32 -> {
                    riqi = it.getBeanPrice() + "/Month"
                }

                86, 87, 88, 89, 90, 91, 92, 93, 94 -> {
                    riqi = it.getBeanPrice() + "/Quarter"
                }

                364, 365, 366, 367, 368 -> {
                    riqi = it.getBeanPrice() + "/Year"
                }
            }
            mBinding?.atuoText2?.post {//某种情况下会在线程种触发
                mBinding?.atuoText2?.visibility = if (it.day == 0) {
                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
                renewalTermsExpanded = false
                renewalTerms = if (it.trialPriceTitle != "") {//如果是试用需要加上前缀
                    String.format(
                        resources.getString(R.string.avip_txt8),
                        parseTrialPeriod(it.trialPriceTitle),
                        it.showCode + " " + riqi,
                        parseTrialPeriod(it.trialPriceTitle)
                    )
                } else {
                    String.format(
                        resources.getString(R.string.avip_txt7), it.showCode + " " + riqi
                    )
                }
                renderRenewalTerms()
            }
        }
        mModel.refresh()
    }

    fun onRenewalDetailsClick() {
        renewalTermsExpanded = !renewalTermsExpanded
        renderRenewalTerms()
    }

    private fun renderRenewalTerms() {
        val textView = mBinding?.atuoText2 ?: return
        textView.doOnLayout {
            val availableWidth = textView.width - textView.paddingLeft - textView.paddingRight
            if (availableWidth <= 0) return@doOnLayout

            fun textLayout(text: CharSequence) = StaticLayout.Builder
                .obtain(text, 0, text.length, textView.paint, availableWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(false)
                .setBreakStrategy(textView.breakStrategy)
                .setHyphenationFrequency(textView.hyphenationFrequency)
                .build()

            fun withLink(content: String, label: String) = SpannableStringBuilder(content).apply {
                val linkStart = length
                append(label)
                setSpan(ForegroundColorSpan(getColor(R.color.a_vip_link)), linkStart, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(StyleSpan(Typeface.BOLD), linkStart, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            val fullLayout = textLayout(renewalTerms)
            textView.text = when {
                fullLayout.lineCount <= 2 -> renewalTerms
                renewalTermsExpanded -> withLink("$renewalTerms ", getString(R.string.a_vip_less))
                else -> {
                    // Reserve space for the action inside the second line, including its bold width.
                    var end = fullLayout.getLineEnd(1)
                    fun collapsed() = withLink(renewalTerms.take(end).trimEnd() + "…", getString(com.face.R.string.see_all))
                    var preview = collapsed()
                    while (end > 0 && textLayout(preview).lineCount > 2) {
                        end = renewalTerms.offsetByCodePoints(end, -1)
                        preview = collapsed()
                    }
                    preview
                }
            }
        }
    }


    fun onLaunchPayClick() {
        if (GVM.INSTANT.userInfo.value.loginType == "guest") {
            BaseYDialog(
                "Link or Log in",
                "Please link or log in to your Google account before purchasing a membership to avoid losing VIP access.",
                "Link Now",
                "Skip, buy as guest",
                unClick = "1",
                onBtnOK = {
                    ALoginDialog().showIgnoreState(mActivity)
                },
                onBtnCancel = {
                    onPlay()
                }
            ).showIgnoreState(mActivity)
            return
        }
        onPlay()
    }

    fun parseTrialPeriod(trialPeriod: String): String {
        return when {
            trialPeriod.endsWith("D") -> "${
                trialPeriod.removePrefix("P").removeSuffix("D")
            }"

            else -> ""
        }
    }

    private fun onPlay() {
        if (GVM.INSTANT.checkingLogin(this)) {
            val selectedBean = mModel.selectedScheme.value ?: return
            GooglePayUtil.launchPay(selectedBean) { code, s ->
                mActivity?.dismissLoading()
                when (code) {
                    BillingClient.BillingResponseCode.OK -> {
                        toast(s)
                        GVM.INSTANT.refreshUserInfo {
                            if (it) {
                                val jiage = if (selectedBean.trialPriceTitle != "") {//如果是试用需要加上前缀
                                    selectedBean.showPrice.ifEmpty {
                                        selectedBean.formattedPrice
                                    }
                                } else {
                                    selectedBean.getBeanPrice()
                                }
                                openActivity<AVipSuccessActivity> {
                                    putString("jiage", selectedBean.showCode + jiage)
                                }
                                finish()
                            } else {
                                toast("Auto refresh failed")
                            }
                        }
                    }

                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                        PurchaseErrorDialog().showIgnoreState(this)
                    }

                    else -> {
                        toast(s)
                    }
                }
            }
        }
    }

    fun onAutoSubClick() {
        WebActivity.openPrivacyPolicy(this, SPUtils.autoSub, "Service Rules")
    }

    override fun onResume() {
        super.onResume()
        GooglePayUtil.init()
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
