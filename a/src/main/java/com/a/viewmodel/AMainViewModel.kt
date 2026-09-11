package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.face.bean.TagConfigBean
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.bean.TaskBean
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.face.BuildConfig
import com.face.bean.ConfigBean
import com.face.bean.ConfigBean.Companion.toMap
import com.face.bean.GooglePriceBean
import com.face.bean.VipSchemeBean
import com.a.net.ARepository
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class AMainViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val IsSuccess = MutableLiveData(false)
    val selectIndex = MutableLiveData(0)


    val taskBean = MutableLiveData<TaskBean?>()//推送过来查询任务
    fun retryRefresh(isBtn: Boolean = false) {
        launch(Dispatchers.IO) {
            if (isBtn) showLoading()
            val configSubscribe = async { ARepository.getUserSetting("config_subscribe") }
            val sysConfig = async { ARepository.getSysConfig("common_config") }
            val typeImg = async { ARepository.getTags(1) }
            val typeVideo = async { ARepository.getTags(2) }
            val type = async { ARepository.getTags(3) }
            val isDevice = async { ARepository.getDeviceCampaign() }

            if (sysConfig.await().mIsSuccess &&
                type.await().mIsSuccess &&
                typeVideo.await().mIsSuccess &&
                typeImg.await().mIsSuccess &&
                isDevice.await().mIsSuccess
            ) {
                if (isBtn) dismissLoading()
                loadFailed.postIfNot(false)
                getDevice(isDevice.await().mData)
                getSysConfig(sysConfig.await().mData)
                getSubscribe(configSubscribe.await().mData)
                getType(type.await().mData)
                getTypeVideo(typeVideo.await().mData)
                getTypeImg(typeImg.await().mData)
                IsSuccess.postIfNot(true)
            } else {
                if (isBtn) dismissLoading()
                loadFailed.postIfNot(true)
            }
        }
    }

    fun refreshFeedback() {//通知
        launchRequestOnIO({ ARepository.getFeedback() }) {
            onSuccess = { beans ->
                var hasNewMsg = false
                beans?.forEach {
                    if (it.state == 1) {
                        hasNewMsg = true
                        return@forEach
                    }
                }
                GVM.INSTANT.hasNewFeedbackMsg.value = hasNewMsg
            }
        }
    }

    fun getSubscribe(listConfig: List<ConfigBean>?) {
        listConfig?.toMap()?.let { map ->
            map["language"]?.let {
                SPUtils.lastLanguage = it
            }
            map["time"]?.let {
                SPUtils.lastTimeZone = it
            }
            map["version"]?.let {
                SPUtils.lastVersion = it
            }
        }
    }


    fun getSysConfig(sysList: List<TagConfigBean>?) {

        sysList?.toMap()?.let {
            val value = it["head_tag"] ?: ""
            SPUtils.tabTitles = value.split(",")
            SPUtils.txtDisclaimer = it["dsclaimer_text"] ?: ""
            SPUtils.startMp4 =
                it["start_mp4"] ?: ""
            SPUtils.privacyPolicy = it["privacy_policy"]
                ?: "https://sites.google.com/view/privacy-policy-of-swapai"
            SPUtils.autoSub =
                it["auto_sub"] ?: "https://sites.google.com/view/privacy-policy-of-swapai"
            SPUtils.showTool = it["show_tool"] ?: "0"
            SPUtils.vipUsageCount = it["vip_usage_count"] ?: "-1"
            SPUtils.nowAdsNum = it["now_ads_num"] ?: "-1"
            SPUtils.freeNum = it["free_num"] ?: "1"

            SPUtils.noticeTitle = it["a_notify_title"] ?: ""
            SPUtils.noticeContent = it["a_notify_content"] ?: ""
            SPUtils.noticeBtnOk = it["a_notify_ok"] ?: ""
            SPUtils.noticeBtnDisagree = it["a_notify_disagree"] ?: ""

            SPUtils.openShare = it["open_share"] == "1"
            SPUtils.enableAd = it["enable_banner_ad"] != "0"
            SPbaseUtils.screenshot = it["screenshot"] ?: "0"
            SPUtils.commentNum = (it["comment_num"] ?: "-1").toInt()
            SPUtils.readEnglish = (it["read_text_english"] ?: "").split("|")
            SPUtils.readChinese = (it["read_text_chinese"] ?: "").split("|")
            SPUtils.readSpain = (it["read_text_spain"] ?: "").split("|")
            SPUtils.commonAuditTags = (it["common_audit_tags"] ?: "").split(",")
            SPUtils.poperworkNum = (it["paperwork_ads"] ?: "1").toInt()
            SPUtils.interstitialNum = (it["interstitial_num"] ?: "2").toInt()
            SPUtils.openNum = (it["open_num"] ?: "2").toInt()
            SPUtils.telegramGroup = it["telegram_group"] ?: ""
            SPUtils.hashPoints = it["hash_points"] ?: ""
            SPUtils.usePoints = it["point_use"] ?: ""
            SPUtils.videoScale = it["video_scale"]?.toFloat() ?: 0.35f
            SPUtils.voideSize = it["voide_size"]?.toInt()?:200
            SPUtils.videoAipoints = it["video_aipoints"] ?: "{}"
            SPbaseUtils.versionRestriction =
                it["version_restriction"]?.contains(BuildConfig.VERSION_CODE.toString()) != true

            GVM.INSTANT.isOpenShare.postValue(SPUtils.openShare)
            GVM.INSTANT.enableAdAndNotVip.postIfNot(GVM.INSTANT.isVip.value && SPUtils.enableAd)
        }
    }

    private fun getType(typeList: List<String>?) {
        if (typeList != null) {
            SPUtils.typeTags = typeList
        }
    }

    private fun getTypeVideo(typeList: List<String>?) {
        if (typeList != null) {
            SPUtils.typeVideoTags = typeList.toMutableList().apply {
                add(0, "All")
            }
        }
    }

    private fun getTypeImg(typeList: List<String>?) {
        if (typeList != null) {
            SPUtils.typeImgTags = typeList.toMutableList().apply {
                add(0, "All")
            }
        }
    }

    fun sendAiTask(taskId: String?) {
        if (taskId != null && taskId != "")
            launchRequestWithLoadingOnIO({ ARepository.queryAiTask(taskId) }) {
                onSuccess = { bean ->
                    taskBean.value = bean
                }
                onFailed = { _, _, errorMsg ->
                    loadFailed.setIfNot(true)
                    toast(errorMsg)
                }
            }
    }


    private fun getDevice(isdev: Int?) {
        //it == 0 自然量
        GVM.INSTANT.aIsAB.postValue(isdev)
        GVM.INSTANT.isShowTool.postValue(isdev)
    }

    fun getDeviceRefresh() {//刷新工具界面
        launchRequestOnIO({ ARepository.getDeviceCampaign() }) {
            onSuccess = { bean ->
                GVM.INSTANT.isShowTool.postValue(bean)
                GVM.INSTANT.aIsAB.postValue(bean)
            }
        }
    }

    //缓存会员价格
    fun vipSchemes() {
        launchRequestOnIO({ ARepository.getVipScheme() }) {
            onSuccess = {
                if (!it.isNullOrEmpty()) {
                    val listGoogle = it.toMutableList()
                    if (SPUtils.vipLifetime.isNotEmpty()&& GVM.INSTANT.isShowTool.value != 0) {
                        val vipBean =
                            MoshiHelper.adapter(VipSchemeBean::class.java)
                                .fromJson(SPUtils.vipLifetime)
                                ?: VipSchemeBean()
                        listGoogle.add(vipBean)
                    }
                    GooglePriceBean(listGoogle, 1)
                }
            }
        }
    }


    private fun GooglePriceBean(list: List<VipSchemeBean>, type: Int) {
        launch(Dispatchers.IO) {
            val subProductIds =
                list.filter { !it.getProductId() }.map { it.productId }.toTypedArray()
            val coinProductIds =
                list.filter { it.getProductId() }.map { it.productId }.toTypedArray()
            if (subProductIds.isNotEmpty()) {//通过google查询订阅
                val vipAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.SUBS,
                        *subProductIds
                    )
                }
                val (_, vipDetails) = vipAsync.await()
                GooglePriceBean.toBeans(vipDetails)?.let {
                    updateGooglePrice(it, list, type)
                }
            }
            if (coinProductIds.isNotEmpty()) {//通过google查询商品
                val coinAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.INAPP,
                        *coinProductIds
                    )
                }
                val (_, coinDetails) = coinAsync.await()
                GooglePriceBean.toBeans(coinDetails)?.let {
                    updateGooglePrice(it, list, type)
                }
            }
        }
    }

    private fun updateGooglePrice(
        googleList: List<GooglePriceBean>,
        list: List<VipSchemeBean>,
        type: Int
    ) {
        googleList.forEach { bean ->//根据google返回信息更新数据
            list.find { bean.productId == it.productId }?.let {
                it.showPrice = bean.price
                it.showUnit = bean.unit
                it.showCode = bean.currencyCode
                it.oldGooglePrice = bean.oldGooglePrice
                it.trialPriceTitle = bean.trialTitle
            }
        }
        if (type == 1)
            SPUtils.vipSchemes = list
        else
            SPUtils.buySchemes = list
    }


    fun saveSetting() {
        val page =  "PageA"
        val version = "Version${BuildConfig.VERSION_CODE}"
        val timeZone = try {
            TimeUtil.getTimeZone()
        } catch (t: Throwable) {
            EventUtil.recordExceptionToFirebase(t)
            ""
        }

        launchRequestOnIO({
            ARepository.saveUserSetting(
                ConfigBean(
                    type = "config_subscribe",
                    key = "page",
                    value = page
                ),
                ConfigBean(
                    type = "config_subscribe",
                    key = "time",
                    value = timeZone
                ),
                ConfigBean(
                    type = "config_subscribe",
                    key = "version",
                    value = version
                )
            )
        }) {
        }
    }
}
