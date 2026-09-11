package com.face.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.android.billingclient.api.BillingClient
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.bean.VipSchemeBean
import com.face.databinding.ActivityVipBinding
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.util.SPUtils
import com.face.view.BaseRedDialog
import com.face.view.DeteleDialog
import com.face.view.DiscountDialog
import com.face.view.LoginDialog
import com.face.view.PurchaseErrorDialog
import com.face.view.VipLifeTimeDialog
import com.face.viewmodel.activity.VipViewModel
import com.youth.banner.listener.OnPageChangeListener
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.toast
import kotlinx.coroutines.delay

class VipActivity : BaseBindingActivity<ActivityVipBinding, VipViewModel>(
    R.layout.activity_vip,
    VipViewModel::class.java
) {
    val isVipDiscount = (SPUtils.vipDiscount.split("|").getOrNull(0) == "1")
    val time = SPUtils.vipDiscount.split("|").getOrNull(3).toIntOrZero()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBack()
        }
    }

    companion object {
        fun jump(
            context: Context) {
            if (GVM.INSTANT.isShowTool.value == 0) {
                val mIntent = Intent().setClassName(
                    App.INSTANCE.packageName,
                    SPUtils.aVipName
                )
                context.startActivity(mIntent)
            } else {
                context.openActivity<VipActivity>()
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        val timer = object : CountDownTimer(time * 1000L, 1000) { // 3分钟，间隔10ms
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                mModel.isTime = true
            }
        }
        timer.start()  // 启动倒计时
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        mModel.discountBean()
        EventUtil.inPage("vip")

        mModel.vipSchemeAdapter.onLifetimeClick = {
            if (it?.day == 0) {
                VipLifeTimeDialog(it.remark).showIgnoreState(this)
            }
        }

        mBinding?.apply {
            recyclerView.itemAnimator = null
        }
        mModel.vipSchemeAdapter.onItemClick = { _, bean, position ->
            mBinding?.recyclerView?.smoothScrollToPosition(position)
            mModel.vipSchemeAdapter.selectIndex = position
            mModel.selectedScheme.postValue(bean)
        }

        mModel.selectedScheme.observe(this) {
            var riqi = ""
            when (it.day) {
                1 -> {
                    riqi = it.getBeanPrice() + resources.getString(R.string.vip_day)
                }

                7 -> {
                    riqi = it.getBeanPrice() + resources.getString(R.string.vip_week)
                }

                28, 29, 30, 31, 32 -> {
                    riqi = it.getBeanPrice() + resources.getString(R.string.vip_month)
                }

                86, 87, 88, 89, 90, 91, 92, 93, 94 -> {
                    riqi = it.getBeanPrice() + resources.getString(R.string.vip_quarter)
                }

                364, 365, 366, 367, 368 -> {
                    riqi = it.getBeanPrice() + resources.getString(R.string.vip_year)
                }
            }

            if (GVM.INSTANT.isShowTool.value != 0) {//b显示的文案
                mBinding?.atuoText2?.visibility = if (it.day == 0) {
                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
                mBinding?.atuoView?.post {
                    mBinding?.atuoView?.text = getString(R.string.vip_txt_atuo1)
                }
                mBinding?.atuoText2?.post {
                    mBinding?.atuoText2?.text = if (it.trialPriceTitle != "") {//如果是试用需要加上前缀
                        String.format(
                            resources.getString(R.string.vip_txt6), it.showCode + " " + riqi
                        )
                    } else {
                        String.format(
                            resources.getString(R.string.vip_txt9), it.showCode + " " + riqi
                        )
                    }
                }
            } else {
                val txtAtuo = if (it.day == 0) {
                    ""
                } else if (it.trialPriceTitle != "") {//如果是试用需要加上前缀
                    String.format(
                        resources.getString(R.string.vip_txt8),
                        parseTrialPeriod(it.trialPriceTitle),
                        it.showCode + " " + riqi,
                        parseTrialPeriod(it.trialPriceTitle)
                    )
                } else {
                    String.format(
                        resources.getString(R.string.vip_txt7), it.showCode + " " + riqi
                    )
                }

                mBinding?.atuoView?.post {
                    mBinding?.atuoView?.text = getString(R.string.vip_txt_atuo)
                }
            }
        }
        mModel.refresh()
    }


    fun onLaunchPayClick() {
        if (GVM.INSTANT.userInfo.value.loginType == "guest") {
            BaseRedDialog(
                getString(R.string.dialog_red_title),
                getString(R.string.dialog_red_content),
                getString(R.string.dialog_red_ok),
                getString(R.string.dialog_red_qx),
                onBtnOK = {
                    LoginDialog().showIgnoreState(mActivity)
                },
                onBtnCancel = {
                    onPlay(mModel.selectedScheme.value)
                }
            ).showIgnoreState(mActivity)
            return
        }
        onPlay(mModel.selectedScheme.value)
    }

    /**
     * 商品信息P7D
     * */
    fun parseDay(trialPeriod: String): String {
        return when {
            trialPeriod.endsWith("D") -> "/Days"

            trialPeriod.endsWith("W") -> "/Week"

            trialPeriod.endsWith("M") -> "/Month"

            else -> ""
        }
    }


    fun parseTrialPeriod(trialPeriod: String): String {
        return when {
            trialPeriod.endsWith("D") -> "${
                trialPeriod.removePrefix("P").removeSuffix("D")
            }"

            else -> ""
        }
    }

    private fun onPlay(selectedBean: VipSchemeBean?) {
        if (GVM.INSTANT.checkingLogin(this)) {
            selectedBean ?: return
            GooglePayUtil.launchPay(selectedBean) { code, s ->
                mActivity?.dismissLoading()
                when (code) {
                    BillingClient.BillingResponseCode.OK -> {
                        toast(s)
                        GVM.INSTANT.refreshUserInfo {
                            if (it) {
                                openActivity<VipSuccessActivity>()
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
                        mModel.isClickWeek = selectedBean.day == 7
                        toast(s)
                    }
                }
            }
        }
    }

    fun onBack() {
        val isShow = mModel.isTime || mModel.isClickWeek
        //isVipDiscount:后台开关；isOkClose:关闭退出； 是否满足折扣条件
        if (!GVM.INSTANT.isVip.value && isVipDiscount && !mModel.isOkClose && SPUtils.discountSchemes.trialPriceTitle != "" && isShow) {
            mModel.isOkClose = true
            DiscountDialog(
                SPUtils.discountSchemes.showUnit,
                SPUtils.discountSchemes.showPrice,
                SPUtils.discountSchemes.oldGooglePrice ?: "-",
                parseDay(SPUtils.discountSchemes.trialPriceTitle),
                onConfirm = { onDiscountPayClick() },
                onDismiss = { onDismissDClick() }
            ).showIgnoreState(mActivity)
            return
        }
        finish()
    }

    fun onDismissDClick() {
        BaseRedDialog(
            getString(R.string.title_offer),
            getString(R.string.title_offer_content),
            getString(R.string.title_offer_ok),
            getString(R.string.title_offer_c),
            unLine = false,
            btnColor = "#3478F6",
            onBtnOK = {
                DiscountDialog(
                    SPUtils.discountSchemes.showUnit,
                    SPUtils.discountSchemes.showPrice,
                    SPUtils.discountSchemes.oldGooglePrice ?: "-",
                    parseDay(SPUtils.discountSchemes.trialPriceTitle),
                    onConfirm = { onDiscountPayClick() },
                    onDismiss = { onDismissDClick() }
                ).showIgnoreState(mActivity)
            }
        ).showIgnoreState(mActivity)
    }


    fun onDiscountPayClick() {
        if (GVM.INSTANT.userInfo.value.loginType == "guest") {
            BaseRedDialog(
                getString(R.string.dialog_red_title),
                getString(R.string.dialog_red_content),
                getString(R.string.dialog_red_ok),
                getString(R.string.dialog_red_qx),
                onBtnOK = {
                    LoginDialog().showIgnoreState(mActivity)
                },
                onBtnCancel = {
                    onPlay(SPUtils.discountSchemes)
                }
            ).showIgnoreState(mActivity)
            return
        }
        onPlay(SPUtils.discountSchemes)
    }

    fun onAutoSubClick() {
        WebActivity.openPrivacyPolicy(this, SPUtils.autoSub, "Service Rules")
    }

    override fun onResume() {
        super.onResume()
        GooglePayUtil.init()
        GVM.INSTANT.refreshUserInfo()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}