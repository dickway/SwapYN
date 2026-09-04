package com.face.ui.txtai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.face.net.Repository
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.ad.AdUtil
import com.face.bean.TxTContent
import com.face.bean.UseTypeBean
import com.face.databinding.ActivityTxtimgBinding
import com.face.key.AiTaskType
import com.face.ui.FunZoneHistoryActivity
import com.face.ui.ToolGeneratingActivity
import com.face.util.SPUtils
import com.face.view.AdsTxtimgShowDialog
import com.face.view.AdsVipShowDialog
import com.face.view.BaseRedDialog
import com.face.view.ExperienceDialog
import com.face.viewmodel.activity.TxtImgViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TxtImgActivity : BaseBindingActivity<ActivityTxtimgBinding, TxtImgViewModel>(
    R.layout.activity_txtimg,
    TxtImgViewModel::class.java
) {
    private val txtContent by intentExtras("txtContent", TxTContent())

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.extras?.apply {//通知点击跳转
            val bean = getParcelable("txtContent") ?: TxTContent()
            if (bean.text.isNotEmpty()) {
                mBinding?.edit?.setText(bean.text)
                mModel.txtNum.postValue(bean.text.length)
                mBinding?.switch1?.isChecked = bean.flag == "1"
                onStyle(if (bean.model == "0") 2 else 1)
                mModel.sizeAdapter.selectIndex = if (bean.height == "682") 1 else 0
            }
        }

    }

    override fun init(savedInstanceState: Bundle?) {

        mModel.sizeAdapter.onItemClick = { _, bean, index ->
            mModel.postSize.postValue(bean?.pxSize)
        }
        mModel.taskId.observe(this) {
            if (it.isNotEmpty()) {
                SPUtils.txtimgNew += 1
                openActivity<ToolGeneratingActivity> {
                    putString("taskId", it)
                    putString("taskType", AiTaskType.TEXT2IMG)
                }
            }
        }
        val txtRandom = mModel.txtEdit.split("|").random()
        mBinding?.edit?.setText(txtRandom)
        mModel.txtNum.postValue(txtRandom.length)

        if (txtContent.text.isNotEmpty()) {
            mBinding?.edit?.setText(txtContent.text)
            mModel.txtNum.postValue(txtContent.text.length)
            mBinding?.switch1?.isChecked = txtContent.flag == "1"
            onStyle(if (txtContent.model == "0") 2 else 1)

            mModel.sizeAdapter.selectIndex = if (txtContent.height == "682") 1 else 0
        }

        GVM.INSTANT.hasAD.observe(this) {//广告拿到奖励，取消弹窗
            dismissLoading()
        }
        mBinding?.edit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 文本变化时
                mModel.txtNum.postValue(s?.length ?: 0)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    fun onStyle(num: Int = 1) {
        when (num) {
            1 -> {
                mModel.postStyle.postValue("1")
            }

            2 -> {
                mModel.postStyle.postValue("0")
            }
        }
    }

    fun randomTxt() {
        var txtStr = mBinding?.edit?.text.toString()
        val newArray = mModel.txtEdit.split("|").filterNot { it == txtStr }
        if (newArray.isNotEmpty()) {
            txtStr = newArray.random()
        }
        mBinding?.edit?.setText(txtStr)
    }

    fun postSend() {
        if (mModel.txtNum.value == 0) {
            return
        }

        if (mBinding?.switch1?.isChecked == true) {
            mModel.nsfw = 1
        } else {
            mModel.nsfw = 0
        }
        mModel.postTxt = mBinding?.edit?.text.toString()

        GVM.INSTANT.payPage.value = "Home_tool_txtimg"
        //非vip用户看广告逻辑
        if (!GVM.INSTANT.isVip.value && !GVM.INSTANT.hasAD.value) {
            if (SPUtils.txtimgNew < SPUtils.txtimgDayNum) {//用户能看广告
                AdsTxtimgShowDialog(
                    onToAct = { toShowAD() },
                    onNotAds = { onNotAds() }).showIgnoreState(mActivity)
                return
            } else {//无广告次数
                val conStr = if (SPUtils.nowAdsNum == "0") {
                    "Your free attempts have been used up. Upgrade now to unlock more exciting features!"
                } else {
                    String.format(
                        resources.getString(R.string.dialog_experience_txt1),
                        SPUtils.txtimgDayNum.toString()
                    )
                }
                ExperienceDialog(conStr).showIgnoreState(mActivity)
                return
            }
        }

        //vip用户看广告逻辑
        if (GVM.INSTANT.isVip.value && !GVM.INSTANT.hasAD.value) {
            if (SPUtils.txtimgNew >= SPUtils.txtimgVipdayNum) {//vip用户看广告
                if (SPUtils.nowAdsNum == "0") {
                    BaseRedDialog(
                        getString(R.string.dialog_ads_vip_title),
                        String.format(
                            resources.getString(R.string.dialog_ads_vip_context2),
                            SPUtils.txtimgVipdayNum.toString()
                        ),
                        "${String.format(
                            resources.getString(R.string.consume_point),
                            SPUtils.useAd.toString()
                        )} (${GVM.INSTANT.userInfo.value.tflops})",
                        getString(R.string.cancel),
                        unClick = "1",
                        onBtnOK = {
                            onNotAds()
                        }
                    ).showIgnoreState(mActivity)
                } else {
                    AdsVipShowDialog(
                        String.format(
                            resources.getString(R.string.dialog_ads_vip_context),
                            SPUtils.txtimgVipdayNum.toString()
                        ),
                        onToAct = { toShowAD() },
                        onNotAds = { onNotAds() }).showIgnoreState(mActivity)
                }
                return
            }
        }
        mModel.sendAiTask()
    }

    private fun toShowAD() {
        if (!AdUtil.showRewardedAdForRewarded()) {
            showLoading(false)
            GlobalScope.launch(Dispatchers.Main) {
                delay(6 * 1000) // 延迟6秒
                AdUtil.cancelAds=true
                dismissLoading()
                if (!AdUtil.showAds) toast(getString(R.string.ad_load_fail))
            }
        }
    }

    fun onNotAds() {
        showLoading()
        SPUtils.useType = UseTypeBean(type = "PaperworkAD")
        launchRequestOnIO({
            Repository.subUserTFLOPS(
                SPUtils.useAd,
                type = MoshiHelper.convertObjectToJson(SPUtils.useType)
            )
        }) {
            onSuccess = {
                GVM.INSTANT.refreshUserInfo {
                    dismissLoading()
                    GVM.INSTANT.hasAD.value = true
                    mModel.sendAiTask()
                }
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }

    fun toDelete() {
        mBinding?.edit?.setText("")
    }

    fun toRecord() {
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", AiTaskType.TEXT2IMG)
        }
    }

    override fun onPause() {
        super.onPause()
        GVM.INSTANT.hasAD.postValue(false)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}