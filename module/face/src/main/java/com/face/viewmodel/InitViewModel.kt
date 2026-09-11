package com.face.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.face.bean.TagConfigBean
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.bean.TaskBean
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.face.BuildConfig
import com.face.bean.CheckInBean
import com.face.bean.ConfigBean
import com.face.bean.ConfigBean.Companion.toMap
import com.face.bean.GooglePriceBean
import com.face.bean.PointBean
import com.face.bean.VipSchemeBean
import com.face.util.FirebaseMessageUtil
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.view.HomeNoticeDialog
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
class InitViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val IsSuccess = MutableLiveData(false)
    val isPermissionEnd = MutableLiveData<Boolean>()

    var isShowDownload = NotNullMutableLiveData(
        SPUtils.versionDownloadShow.contains(
            "${
                getStringX(
                    com.key.R.string.app_ai_name
                )
            }${BuildConfig.VERSION_CODE}"
        )
    )

    val taskBean = MutableLiveData<TaskBean?>()//推送过来查询任务


    fun retryMian() {
        launch(Dispatchers.IO) {
            val configSubscribe = async { Repository.getUserSetting("config_subscribe") }
            val sysConfig = async { Repository.getSysConfig("common_config") }
            val userSetting = async { Repository.getUserSetting("check_in") }
            val typeImg = async { Repository.getTags(1) }
            val typeVideo = async { Repository.getTags(2) }
            val type = async { Repository.getTags(3) }

            if (sysConfig.await().mIsSuccess &&
                type.await().mIsSuccess &&
                typeVideo.await().mIsSuccess &&
                typeImg.await().mIsSuccess
            ) {
                getSysConfig(sysConfig.await().mData)
                getSubscribe(configSubscribe.await().mData)
                getUserSetting(userSetting.await().mData)
                getType(type.await().mData)
                getTypeVideo(typeVideo.await().mData)
                getTypeImg(typeImg.await().mData)
                getUserSignInLog()
                loadFailed.postIfNot(false)
            } else {
                loadFailed.postIfNot(true)
            }
        }
    }


    fun retryRefresh(isBtn: Boolean = false) {
        launch(Dispatchers.IO) {
            if (isBtn) showLoading()
            val sysConfig = async { Repository.getSysConfig("common_config") }
            val shareConfig = async { Repository.getSysConfig("share_config") }
            val noticeConfig = async { Repository.getSysConfig("homepage_notice") }
            val typeImg = async { Repository.getTags(1) }
            val typeVideo = async { Repository.getTags(2) }
            val type = async { Repository.getTags(3) }
            val isDevice = async { Repository.getDeviceCampaign() }

            val sysConfigResp = sysConfig.await()
            val shareConfigResp = shareConfig.await()
            val typeImgResp = typeImg.await()
            val typeVideoResp = typeVideo.await()
            val typeResp = type.await()
            val isDeviceResp = isDevice.await()
            val noticeResp = noticeConfig.await()
            if (sysConfigResp.mIsSuccess &&
                shareConfigResp.mIsSuccess &&
                typeImgResp.mIsSuccess &&
                typeVideoResp.mIsSuccess &&
                typeResp.mIsSuccess &&
                isDeviceResp.mIsSuccess &&
                noticeResp.mIsSuccess
            ) {
                dismissLoading()
                loadFailed.postIfNot(false)
                getDevice(isDeviceResp.mData)
                getSysConfig(sysConfigResp.mData)
                getShareConfig(shareConfigResp.mData)
                getType(typeResp.mData)
                getTypeVideo(typeVideoResp.mData)
                getTypeImg(typeImgResp.mData)
                getNotice(noticeResp.mData)
                IsSuccess.postIfNot(true)
            } else {
                dismissLoading()
                loadFailed.postIfNot(true)
                toast("Connection fail")
                val failedRequests = mutableListOf<String>()
                if (!sysConfigResp.mIsSuccess) failedRequests.add("sys:${sysConfigResp.mError?.cause}")
                if (!shareConfigResp.mIsSuccess) failedRequests.add("share:${shareConfigResp.mError?.cause}")
                if (!typeResp.mIsSuccess) failedRequests.add("type:${typeResp.mError?.cause}")
                if (!typeVideoResp.mIsSuccess) failedRequests.add("video:${typeVideoResp.mError?.cause}")
                if (!typeImgResp.mIsSuccess) failedRequests.add("img:${typeImgResp.mError?.cause}")
                if (!isDeviceResp.mIsSuccess) failedRequests.add("device:${isDeviceResp.mError?.cause}")
//                LogUtils.e(">>>>>>>>>>${failedRequests.joinToString("\n")}")
                EventUtil.initFailed(failedRequests.joinToString("\n"))
            }
        }
    }

    /**
     * 获取签到数据
     */
    fun getUserSignInLog() {
        launchRequestOnIO({
            Repository.getUserSignInLog(
                TimeUtil.getFormatDate(
                    System.currentTimeMillis(),
                    "yyyyMM"
                )?.toInt() ?: 0
            )
        }) {
            onSuccess = { it ->
                val date = TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyyMMdd")
                SPUtils.checkInLogs = CheckInBean.merge(SPUtils.checkInLogs, it)
                GVM.INSTANT.isTodayChecked.postIfNot(false)//默认为没签到
                if (it?.any { it.fromType == 1 && it.day == date } == true) {
                    SPUtils.checkInTime = date.toString()
                    GVM.INSTANT.isTodayChecked.postIfNot(SPUtils.checkInTime == date)
                }
            }
        }
    }

    fun refreshFeedback() {//通知
        launchRequestOnIO({ Repository.getFeedback() }) {
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

    fun getShareConfig(sysList: List<TagConfigBean>?) {
        sysList?.toMap()?.let {
            SPUtils.addDay = it["add_day"] ?: "0"
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
        FirebaseMessageUtil.subscribeApp()
    }

    fun getUserSetting(listConfig: List<ConfigBean>?) {
        listConfig?.toMap()?.let { map ->
            SPUtils.enableCheckInNotification = (map["enable_check"] ?: "0").toInt()
            val oldTopic = SPUtils.checkInNotificationTopic
            val topicNew = map["notification_topic"] ?: oldTopic
            if (oldTopic != "") {
                FirebaseMessageUtil.unsubscribeTopic(oldTopic) {
                    FirebaseMessageUtil.subscribeTopic(topicNew) { success ->
                        if (success) {
                            SPUtils.checkInNotificationTime = map["reminder_time"].toIntOrZero()
                            SPUtils.checkInNotificationTopic = topicNew
                        }
                    }
                }
            } else {
                FirebaseMessageUtil.subscribeTopic(topicNew) {
                    if (it) {
                        SPUtils.checkInNotificationTime = map["reminder_time"].toIntOrZero()
                        SPUtils.checkInNotificationTopic = topicNew
                    }
                }
            }

        }
    }


    fun getSysConfig(sysList: List<TagConfigBean>?) {

        sysList?.toMap()?.let {
            val value = it["head_tag"] ?: ""
            SPUtils.tabTitles = value.split(",")
            SPUtils.txtDisclaimer = it["dsclaimer_text"] ?: ""
            SPUtils.materialId = it["material_id"] ?: ""
            SPUtils.startMp4 =
                it["start_mp4"] ?: ""
            SPUtils.privacyPolicy = it["privacy_policy"]
                ?: ""//https://sites.google.com/view/privacy-policy-of-swapai
            SPUtils.autoSub =
                it["auto_sub"] ?: ""//https://sites.google.com/view/privacy-policy-of-swapai
//            SPUtils.showTool = it["show_tool"] ?: "0"
            SPUtils.toolUse = (it["tool_use"] ?: "").contains(BuildConfig.VERSION_CODE.toString())

            SPUtils.exploreListTools1 = it["explore_txt_tools"] ?: "{}"
            SPUtils.exploreListTools2 = it["explore_img_tools"] ?: "{}"

            SPUtils.noticeTitle = it["a_notify_title"] ?: ""
            SPUtils.noticeContent = it["a_notify_content"] ?: ""
            SPUtils.noticeBtnOk = it["a_notify_ok"] ?: ""
            SPUtils.noticeBtnDisagree = it["a_notify_disagree"] ?: ""

            SPUtils.buySlots = it["pay_for_media"] ?: ""
            SPUtils.rateInfo = it["rate_info"] ?: "{}"

            SPUtils.videoHeader = it["video_header"] ?: ""

            SPUtils.useAd = it["use_ad_aipoints"].toIntOrZero()

            SPUtils.showPage = it["show_page"] ?: ""

            SPUtils.funzoneList = it["funzone_list"] ?: "{}"

            SPUtils.listDynamicTools = it["list_dynamic_tools"] ?: "{}"

            SPUtils.customShowUse =
                (it["custom_show"] ?: "").contains(BuildConfig.VERSION_CODE.toString())

            SPUtils.vipLifetime = it["vip_lifetime"] ?: ""
            SPUtils.versionDownloadShow = it["version_download_show"] ?: ""
            SPUtils.hdPoints = it["hd_points"].toIntOrZero()


            SPUtils.hugPinot = it["expend_pinot"]?.split("|")?.getOrNull(0) ?: "30"
            SPUtils.kissPinot = it["expend_pinot"]?.split("|")?.getOrNull(1) ?: "30"
            SPUtils.outfitPinot = it["expend_pinot"]?.split("|")?.getOrNull(2) ?: "10"
            SPUtils.movesPinot = it["expend_pinot"]?.split("|")?.getOrNull(6) ?: "20"
            SPUtils.playAiPinot = it["expend_pinot"]?.split("|")?.getOrNull(7) ?: "196"

            SPUtils.vipUsageCount = it["vip_usage_count"] ?: "-1"
            SPUtils.nowAdsNum = it["now_ads_num"] ?: "-1"
            SPUtils.freeNum = it["free_num"] ?: "1"
            SPUtils.readNumSize = it["read_num_size"] ?: "300"

            SPUtils.introductionVideo = it["introduction_video"] ?: ""

            SPUtils.openHomeShow = SPUtils.openHomeShow.takeIf { it != -1 }
                ?: it["open_home_show"]?.toIntOrZero() ?: -1

            SPUtils.openShare = it["open_share"] == "1"
            SPUtils.enableAd = it["enable_banner_ad"] != "0"
            SPUtils.enableVipBannerAd = it["enable_banner_vip_ad"] != "0"
            SPbaseUtils.screenshot = it["screenshot"] ?: "0"
            SPUtils.commentNum = (it["comment_num"] ?: "-1").toInt()
            SPUtils.readEnglish = (it["read_text_english"] ?: "").split("|")
            SPUtils.readChinese = (it["read_text_chinese"] ?: "").split("|")
            SPUtils.readSpain = (it["read_text_spain"] ?: "").split("|")
            SPUtils.commonAuditTags = (it["common_audit_tags"] ?: "").split(",")
            SPUtils.poperworkNum = (it["paperwork_ads"] ?: "1").toInt()
            SPUtils.isPlayAI = (it["show_play"] ?: "1") == "1"

            SPUtils.ageNum = (it["age_ads"] ?: "1").toInt()
            SPUtils.liveNum = (it["live_ads"] ?: "1").toInt()
            SPUtils.interstitialNum = (it["interstitial_num"] ?: "2").toInt()
            SPUtils.interstitialNumA = (it["interstitial_num_a"] ?: "100").toInt()

            SPUtils.openNum = (it["open_num"] ?: "2").toInt()
            SPUtils.telegramGroup = it["telegram_group"] ?: ""
            SPUtils.hashPoints = it["hash_points"] ?: ""
            SPUtils.usePoints = it["point_use"] ?: ""
            SPUtils.videoScale = it["video_scale"]?.toFloat() ?: 0.35f
            SPUtils.voideSize = it["voide_size"].toIntOrZero()
            SPUtils.faceRect = it["face_rect"] ?: "{}"
            SPUtils.videoAipoints = it["video_aipoints"] ?: "{}"
            SPbaseUtils.versionRestriction =
                it["version_restriction"]?.contains(BuildConfig.VERSION_CODE.toString()) != true

            SPUtils.txtTelegram = it["vip_telegram"] ?: ""
            SPUtils.txtWhatapp = it["vip_whatapp"] ?: ""
            SPUtils.txtDiscord = it["vip_discord"] ?: ""

            SPUtils.vipDiscount = it["vip_discount"] ?: ""

//            SPUtils.cacheTimeDuration = it["cache_time"].toIntOrZero()


            SPUtils.txtimgZH = it["zh_text2img"] ?: ""
            SPUtils.txtimgZHTW = it["zh_tw_text2img"] ?: ""
            SPUtils.txtimgEN = it["en_text2img"] ?: ""
            SPUtils.txtimgES = it["es_text2img"] ?: ""
            SPUtils.txtimgDE = it["de_text2img"] ?: ""
            SPUtils.txtimgFR = it["fr_text2img"] ?: ""
            SPUtils.txtimgSV = it["sv_text2img"] ?: ""
            SPUtils.txtimgAR = it["ar_text2img"] ?: ""
            SPUtils.txtimgKO = it["ko_text2img"] ?: ""
            SPUtils.txtimgJA = it["ja_text2img"] ?: ""
            SPUtils.txtimgKU = it["ku_text2img"] ?: ""
            SPUtils.txtimgTR = it["tr_text2img"] ?: ""
            SPUtils.txtimgFA = it["fa_text2img"] ?: ""
            SPUtils.txtimgIW = it["iw_text2img"] ?: ""
            SPUtils.txtimgPTBR = it["pt_br_text2img"] ?: ""
            SPUtils.txtimgPT = it["pt_text2img"] ?: ""
            SPUtils.txtimgIT = it["it_text2img"] ?: ""

            SPUtils.txtimgDayNum = it["txt2img_day_num"].toIntOrZero()
            SPUtils.txtimgVipdayNum = it["txt2img_vipday_num"].toIntOrZero()
            //share模块
            SPUtils.videoPic = it["video_points_pic"] ?: ""
            SPUtils.videoPoints20s = it["video_points_20s"] ?: ""
            SPUtils.videoPoints3m = it["video_points_3m"] ?: ""
            SPUtils.videoPoints5m = it["video_points_5m"] ?: ""
            SPUtils.shareGetPoints20s = it["share_get_points_20s"] ?: ""
            SPUtils.shareGetPoints3m = it["share_get_points_3m"] ?: ""
            SPUtils.shareGetPoints5m = it["share_get_points_5m"] ?: ""


            GVM.INSTANT.isOpenShare.postValue(SPUtils.openShare)
            GVM.INSTANT.enableAdAndNotVip.postIfNot(SPUtils.enableAd)
        }
    }

    private fun getType(typeList: List<String>?) {
        if (typeList != null) {
            SPUtils.typeTags = typeList
        }
    }

    private fun getNotice(sysList: List<TagConfigBean>?) {
        if (sysList != null) {
            val map = SPUtils.noticeList.takeUnless { it.isEmpty() }?.toMap()
            val again = map?.get("again") ?: ""
            if (SPUtils.noticeRecord != again) {
                SPUtils.noticeRecord = again
                SPUtils.noticeLook = false
            }
            SPUtils.noticeList = sysList
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
//        GVM.INSTANT.retryTags.postValue(true)
    }

    fun sendAiTask(taskId: String?) {
        if (taskId != null && taskId != "")
            launchRequestWithLoadingOnIO({ Repository.queryAiTask(taskId) }) {
                onSuccess = { bean ->
                    taskBean.value = bean
                }
                onFailed = { _, _, errorMsg ->
                    loadFailed.setIfNot(true)
                    toast(errorMsg)
                }
            }
    }

    fun getInitConfig() {
        if (SPUtils.gaid.isBlank()) {
            launch {
                var count = 0
                while (count < 10) {
                    delay(500)
                    if (SPUtils.gaid.isNotBlank()) {
                        break
                    }
                    count++
                }
                // gaid不为空的话前面就报了
//                EventUtil.startApp()
                Firebase.crashlytics.setCustomKey("gaid", SPUtils.gaid)
            }
        }
    }

    private fun getDevice(isdev: Int?) {
        //it == 0 自然量
        GVM.INSTANT.isShowTool.postValue(isdev ?: 0)
        SPbaseUtils.loadAB = isdev ?: 0
        GVM.INSTANT.isEventA.postValue(isdev ?: 0)
    }

    fun getDeviceRefresh() {//刷新工具界面
        if (GVM.INSTANT.isShowTool.value == 0) {
            launchRequestOnIO({ Repository.getDeviceCampaign() }) {
                onSuccess = { bean ->
                    if (GVM.INSTANT.isShowTool.value != bean) {//切换账号后不一致就重启app
                        GVM.INSTANT.isEventA.value = -1
                    }
                    GVM.INSTANT.isShowTool.postValue(bean ?: 0)
                    SPbaseUtils.loadAB = bean ?: 0
                }
            }
        }
    }

    //缓存会员价格
    fun vipSchemes() {
        launchRequestOnIO({ Repository.getVipScheme() }) {
            onSuccess = {
                if (!it.isNullOrEmpty()) {
                    val listGoogle = it.toMutableList()
                    if (SPUtils.vipLifetime.isNotEmpty() && GVM.INSTANT.isShowTool.value != 0) {
                        val vipBean =
                            MoshiHelper.adapter(VipSchemeBean::class.java)
                                .fromJson(SPUtils.vipLifetime)
                                ?: VipSchemeBean()
                        listGoogle.add(vipBean)
                    }
                    GooglePriceBean(listGoogle, 1)
                    discountBean()
                }
            }
        }
    }


    fun discountBean() {
        val discountProductId = SPUtils.vipDiscount.split("|").getOrNull(1) ?: ""
        val discountQueryId = SPUtils.vipDiscount.split("|").getOrNull(2)
        if (discountProductId.isNotEmpty()) {
            val discountBean = VipSchemeBean()
            discountBean.productId = discountProductId
            discountBean.id = discountQueryId.toIntOrZero()
            launch(Dispatchers.IO) {
                val vipAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.SUBS,
                        discountProductId
                    )
                }
                val (_, vipDetails) = vipAsync.await()
                GooglePriceBean.toBeans(vipDetails)?.let { list ->
                    list.forEach { bean ->//根据google返回信息更新数据
                        if (discountBean.productId == bean.productId) {
                            discountBean.formattedPrice = bean.formattedPrice
                            discountBean.showPrice = bean.price
                            discountBean.showUnit = bean.unit
                            discountBean.showCode = bean.currencyCode
                            discountBean.oldGooglePrice = bean.oldGooglePrice
                            discountBean.trialPriceTitle = bean.trialTitle
                        }
                    }
                }
                SPUtils.discountSchemes = discountBean//缓存数据，下次进入没有空白界面
            }
        }

    }

    //缓存商品价格
    fun getTFLOPConfigs() {
        launchRequestOnIO({ Repository.getTFLOPConfigs() }) {
            onSuccess = {
                if (it != null) {
                    GooglePriceBean(it, 2)
                }
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }


    private fun GooglePriceBean(list: List<VipSchemeBean>, type: Int) {
        launch(Dispatchers.IO) {
            val subProductIds =
                list.filter { !it.getProductId() }.map { it.productId }.toTypedArray()
            val coinProductIds =
                list.filter { it.getProductId() }.map { it.productId }.toTypedArray()
            //查询商品原价
            val oldProductIds =
                list.filter { it.getProductId() }.map { it.remark }.toTypedArray()

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

            if (oldProductIds.isNotEmpty()) {
                val oldAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.INAPP,
                        *oldProductIds
                    )
                }
                val (_, oldDetails) = oldAsync.await()
                GooglePriceBean.toBeans(oldDetails)?.let {
                    composePrice(it)
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

    private fun composePrice(list: List<GooglePriceBean>) {
        val oldList = SPUtils.buySchemes
        list.forEach { bean ->//根据google返回信息更新数据
            oldList.find {
                bean.productId == it.remark
            }?.let {
                it.oldShowPrice = bean.price
            }
        }
        SPUtils.buySchemes = oldList
    }


    fun saveSetting() {
        val page = if (SPbaseUtils.loadAB == 1) {
            "PageB"
        } else {
            "PageA"
        }

        val version = "Version${BuildConfig.VERSION_CODE}"

        val timeZone = try {
            TimeUtil.getTimeZone()
        } catch (t: Throwable) {
            EventUtil.recordExceptionToFirebase(t)
            ""
        }

        val lastLanguage = SPUtils.lastLanguage.uppercase()
        launchRequestOnIO({
            Repository.saveUserSetting(
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
                    key = "language",
                    value = lastLanguage
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