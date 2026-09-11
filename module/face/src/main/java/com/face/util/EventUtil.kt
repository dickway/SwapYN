package com.face.util

import android.os.Build
import android.os.Bundle
import com.android.billingclient.api.Purchase
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.ui.App
import com.face.BuildConfig
import com.face.bean.VipSchemeBean
import com.face.util.SPUtils.deviceId
import com.face.util.SPUtils.gaid
import com.face.util.SPUtils.installClickTime
import com.face.util.SPUtils.installReferrer
import com.face.util.SPUtils.installTime
import com.face.util.SPUtils.installVersion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.singular.sdk.Singular
import com.zzkj.structure.net.NetworkUtils
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.device.DeviceUtils
import com.zzkj.structure.util.moshi.MoshiHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

/**
 * @author 再战科技
 * @date 2023/11/10
 * @description
 */
object EventUtil {

    // 自动报Google支付
    private val firebaseAnalytics by lazy { Firebase.analytics }

    // 自动报Google支付
//    private val facebookLogger by lazy { AppEventsLogger.newLogger(App.INSTANCE) }

    /**
     * 支付 purchase firebase必须用[FirebaseAnalytics.Event.PURCHASE]且后面不能跟其他东西
     */
    private const val KEY_PAY = "purchase"

    // 每次进入上报
    private const val KEY_PAY_PAGE = "pay_page"

    // 删除账号
    private const val KEY_DELETE = "delete_user"

    // 每次进入上报
    private const val KEY_INSTALL_AB = "install_ab"

    // 安装归因参数
    private const val KEY_INSTALL_REFERRER = "install_referrer"

    // singular安装归因参数
    private const val KEY_SINGULAR_REFERRER = "singular_referrer"

    // singular深度链接参数
    private const val KEY_SINGULAR_LINK = "singular_link"

    // 打开软件
    private const val KEY_START_APP = "start_app"

    // 重试
    private const val KEY_START_RETRY = "start_retry"

    // 更新软件
    private const val KEY_UPDATE_APP = "update"

    // 完成推送
    private const val KEY_PUSH_SHOW = "push_show"

    // 素材点击了按钮
    private const val KEY_CLICK = "app_click"

    // 热门推送
    private const val KEY_PUSH_HOT = "push_show_hot"

    // 点击推送
    private const val KEY_PUSH_CLICK = "push_click"

    // 处于前台时长
    private const val KEY_LIVE_TIME = "live_time"

    // 初始化接口失败
    private const val KEY_INIT_FAILED = "init_failed"

    // 登录
    private const val KEY_LOGIN = "login"

    // 登录协议
    private const val KEY_LOGIN_PRIVACY = "login_privacy"

    // 退出登录
    private const val KEY_LOGOUT = "logout"

    // 切换语言
    private const val KEY_CHANGE_LANGUAGE = "change_language"

    // 显示广告
    private const val KEY_SHOW_AD = "show_ad"

    // 看完激励广告
    private const val KEY_REWARDED_COMPLETED = "rewarded_completed"

    // 视频加载
    private const val KEY_SWAP_TIME = "swap_time"

    // 点击广告
    private const val KEY_CLICK_AD = "click_ad"

    // 自定义视频原文件
    private const val KEY_VIDEO = "video_path"

    // 自定义视频剪切文件
    private const val KEY_VIDEO_CLIP = "video_path_clip"

    // 视频上传上报
    private const val KEY_VIDEO_POST = "video_post"

    // 搜索
    private const val KEY_SEARCH = "search_words"

    // 发起换脸
    private const val KEY_AITASK_SEND = "aitask_send"

    // 点击收藏
    private const val KEY_CLICK_COLLECT = "click_collect"

    // 取消收藏
    private const val KEY_CLICK_DISCOLLECT = "click_discollect"

    // 取消收藏素材
    private const val KEY_CLICK_DISCOLLECT_MEDIA = "click_discollect_media"


    // 点击下载
    private const val KEY_CLICK_DOWNLOAD = "click_download"

    // 点击上传人脸
    private const val KEY_UPLOAD_FACE = "click_upload_face"

    // 人脸检测失败
    private const val KEY_CHECK = "face_check"

    // 保存人脸
    private const val KEY_SAVE_FACE = "save_face_tag"

    // 注册
    private const val KEY_REGISTER = "register"
//    purchase_veri_success 购买验证成功
//
//    purchase_veri_fail  购买验证失败


    // 进入页面
    private const val KEY_IN_PAGE = "in_page"

    private val SUPPORT_GOOGLE by lazy {
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(App.INSTANCE) == ConnectionResult.SUCCESS
    }
    private const val CURRENCY = "USD"

    // 用Locale.CHINESE 一直是 GMT-10:00 格式
    val TIME_ZONE: String by lazy {
        TimeUtil.getCompleteTimeZone()
    }

    val SYSTEM_LANGUAGE: String by lazy {
        Locale.getDefault().language
    }
    private val packageName = App.INSTANCE.packageName
    private val androidVersionCode = Build.VERSION.SDK_INT
    private val androidVersionName = Build.VERSION.RELEASE
    private val androidBrand = Build.BRAND
    private val androidModel = Build.MODEL


    /**
     * 上报事件
     *
     * @param name 事件名
     * @param mapParameter  参数
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun logEvent(
        name: String,
        mapParameter: MutableMap<String, Any?>? = null,
        listener: ((Boolean) -> Unit)? = null
    ) {
        var map = mapParameter
        if (map == null) {
            map = mutableMapOf()
        }
        GVM.INSTANT.userInfo.value.userId.takeIf { it > 0 }?.let {
            map["user_id"] = it
        }
        map["package_name"] = packageName
        map["android_id"] = DeviceUtils.getAndroidID()
        map["android_version_code"] = androidVersionCode
        map["android_version_name"] = androidVersionName
        map["android_brand"] = androidBrand
        map["android_model"] = androidModel
        map["device_id"] = deviceId
        map["gaid"] = gaid
        map["app_version_name"] = BuildConfig.VERSION_NAME
        map["devicecampaign"] = GVM.INSTANT.isShowTool.value
//        map["app_version_code"] = BuildConfig.VERSION_CODE
        map["language"] = SPbaseUtils.spLanguage
        map["system_language"] = SYSTEM_LANGUAGE
        map["time_zone"] = TIME_ZONE
        map["support_google"] = SUPPORT_GOOGLE
        map["debug"] = BuildConfig.DEBUG
        map["network_info"] = NetworkUtils.networkInfo
        map["current_time"] = System.currentTimeMillis()
//        map["current_date"] = TimeUtil.getFormatDate(
//            System.currentTimeMillis(),
//            TimeUtil.DEFAULT_DATE_TIME_DATA
//        )

        if (installReferrer.isNotBlank()) {
            map["install_referrer"] = installReferrer
            map["install_version"] = installVersion
            map["install_time"] = installTime
            map["install_click_time"] = installClickTime
        }
        try {
            Singular.eventJSON(name, JSONObject(map))
        } catch (t: Throwable) {
            LogUtils.w(t)
            recordExceptionToFirebase(t)
        }

        try {
            val jsonMapString = MoshiHelper.adapter(MutableMap::class.java).toJson(map)
            //后台上报
            GlobalScope.launch {
                Repository.saveEvent(name, jsonMapString)
                    .let { response ->
                        listener?.invoke(response.mIsSuccess)
                    }
            }
        } catch (t: Throwable) {
            LogUtils.w(t)
            recordExceptionToFirebase(t)
        }

        val bundle = map2Bundle(map)
        //facebook上报
//        facebookLogger.logEvent(convertFacebookEventName(name), bundle)

        //firebase上报
        if (KEY_PAY + "_success" == name) {
            /**
             * Firebase收益参数Key必须用FirebaseAnalytics.Param.VALUE
             * 值必须要double类型
             */
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, map["price"].toString().toDouble())
        }

        try {

//            if (updateFirebase(name)) {//限定路径，先不用
            firebaseAnalytics.logEvent(convertFirebaseEventName(name), bundle)
//            }
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }

    private fun map2Bundle(map: Map<String, Any?>): Bundle {
        val bundle = Bundle()
        for (key in map.keys) {
            val value = map[key] ?: continue
            when (value) {
                is Int -> {
                    bundle.putInt(key, value)
                }

                is Float -> {
                    bundle.putFloat(key, value)
                }

                is Double -> {
                    bundle.putDouble(key, value)
                }

                else -> {
                    bundle.putString(key, value.toString())
                }
            }
        }
        return bundle
    }

    private fun updateFirebase(name: String): Boolean {
        return name.startsWith(KEY_IN_PAGE)
                || name.startsWith(KEY_PAY)
                || name.startsWith(KEY_LOGIN)
                || name == KEY_SEARCH + "_no_data"
    }

    /**
     * facebook事件名
     * [AppEventsConstants]
     *
     * @param name
     * @return
     */
//    private fun convertFacebookEventName(name: String): String {
//        return if (KEY_REGISTER == name) {
//            AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION
//        } else {
//            name
//        }
//    }

    /**
     * firebase事件名不能有空格且支付事件名必须用 FirebaseAnalytics.Event.PURCHASE
     *
     * @param name
     * @return
     */
    private fun convertFirebaseEventName(name: String): String {
        return name.replace(" ", "_")
    }

    /**
     * 上报异常至Firebase
     * @param throwable Throwable
     * @param onlyRelease Boolean 是否仅Release时才上传
     */
    fun recordExceptionToFirebase(throwable: Throwable, onlyRelease: Boolean = true) {
        if (!onlyRelease || !BuildConfig.DEBUG) {
            Firebase.crashlytics.recordException(throwable)
        }
    }

    /**
     * 安装归因
     */
    fun singularReferrer(attribution: String, listener: ((Boolean) -> Unit)? = null) {
        val map: MutableMap<String, Any?> = HashMap()
        map["attribution"] = attribution
        logEvent(KEY_SINGULAR_REFERRER, map, listener = listener)
    }

    /**
     * 深度链接
     */
    fun singularLink(
        deeplink: String,
        passthrough: String,
        isDeferred: Boolean?,
        listener: ((Boolean) -> Unit)? = null
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["deeplink"] = deeplink
        map["passthrough"] = passthrough
        map["isDeferred"] = isDeferred
        logEvent(KEY_SINGULAR_LINK, map, listener = listener)
    }


    /**
     * 安装归因
     */
    fun installReferrer(listener: ((Boolean) -> Unit)? = null) {
        logEvent(KEY_INSTALL_REFERRER, listener = listener)
    }


    /**
     * 打开app, 每次进入
     */
    fun startAB() {
        val map: MutableMap<String, Any?> = HashMap()
        map["devicecampaign"] = GVM.INSTANT.isShowTool.value
        logEvent(KEY_INSTALL_AB, map)
    }

    /**
     * 删除账号
     */
    fun deleteUser() {
        val map: MutableMap<String, Any?> = HashMap()
        logEvent(KEY_DELETE, map)
    }


    /**
     * 打开app, 每次进入 MainActivity
     */
    fun startApp() {
        val map: MutableMap<String, Any?> = HashMap()
        map["language"] = SPbaseUtils.spLanguage
        logEvent(KEY_START_APP, map)
    }


    /**
     * 素材点击了按钮
     */
    fun appClick(btnName: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["btn_name"] = btnName
        logEvent(KEY_CLICK, map)
    }

    /**
     * 启动页重试
     */
    fun startRetry() {
        val map: MutableMap<String, Any?> = HashMap()
        logEvent(KEY_START_RETRY, map)
    }

    /**
     * 更新app
     * @param newVersionCode
     */
    fun updateApp(newVersionCode: Int) {
        val map: MutableMap<String, Any?> = HashMap()
        map["new_version"] = newVersionCode
        logEvent(KEY_UPDATE_APP, map)
    }

    /**
     * 显示推送,任务id
     */
    fun pushShow(taskId: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["task_id"] = taskId
        logEvent(KEY_PUSH_SHOW, map)
    }

    /**
     * 签到通知
     */
    fun pushNotification(title: String?, content: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["title_notification"] = title
        map["content_notification"] = content
        logEvent(KEY_PUSH_SHOW + "_" + "notification", map)
    }

    /**
     * 显示推送,热门id
     */
    fun pushHot(mediaId: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["media_Id"] = mediaId
        logEvent(KEY_PUSH_HOT, map)
    }

    /**
     * 点击推送,任务id
     */
    fun pushClick(taskId: String?, mediaId: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["task_id"] = taskId
        map["media_id"] = mediaId
        logEvent(KEY_PUSH_CLICK, map)
    }

    /**
     * 在前台时长
     * @param eventId String
     * @param time String  Long
     * @param listener Function1<Boolean, Unit>?
     */
    fun liveTime(eventId: String, time: String, listener: ((Boolean) -> Unit)? = null) {
        val map = mutableMapOf<String, Any?>()
        map["event_id"] = eventId
        map["time"] = time
        logEvent(KEY_LIVE_TIME, map, listener)
    }

    /**
     * 登录协议
     */
    fun loginp(type: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["login_type"] = type
        logEvent(KEY_LOGIN_PRIVACY, map)
    }

    /**
     * 首页配置失败
     */
    fun initFailed(mError: String = "") {
        val map: MutableMap<String, Any?> = HashMap()
        map["failed_info"] = mError
        logEvent(KEY_INIT_FAILED, map)
    }

    /**
     * 登录
     */
    fun login(type: String, loginState: Boolean = true, mError: String = "") {
        val map: MutableMap<String, Any?> = HashMap()
        map["login_type"] = type
        map["login_state"] = loginState
        map["login_error"] = mError
        logEvent(KEY_LOGIN, map)
    }

    /**
     * 退出登录
     */
    fun logout() {
        logEvent(KEY_LOGOUT)
    }

    /**
     * 切换语言
     * @param 切换后的语言
     */
    fun languageChange(language: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["language"] = language
        logEvent(KEY_CHANGE_LANGUAGE, map)
    }

    /**
     * 显示广告
     * rewarded 激励
     */
    fun showAd(
        what: String,
        step: String,
        adInfo: String? = null,
        arguments: Map<String, Any?>? = null
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["what"] = what
        map["step"] = step
        adInfo?.let { map["ad_info"] = it }
        arguments?.let { map.putAll(it) }
        logEvent(KEY_SHOW_AD + "_" + what, map)
    }

    /**
     * 激励广告看完
     */
    fun rewardedCompleted(adInfo: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        adInfo?.let { map["ad_info"] = it }
        logEvent(KEY_REWARDED_COMPLETED, map)
    }

    /**
     * 上报自定义视频原文件路径
     * type:1是短视频，2是长视频
     */
    fun videoPath(info: String, type: Int) {
        val map: MutableMap<String, Any?> = HashMap()
        map["info"] = info
        map["type"] = type
        logEvent(KEY_VIDEO, map)
    }

    /**
     * 上报自定义视频原文件路径
     * rewarded 激励
     */
    fun videoClipPath(info: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["info"] = info
        logEvent(KEY_VIDEO_CLIP, map)
    }

    /**
     * 上报自定义视频原文件路径
     * rewarded 激励
     */
    fun videoPost(type: String = "short", info: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["info"] = info
        logEvent(KEY_VIDEO_POST + "_" + type, map)
    }

    /**
     * 视频加载时间
     * rewarded 激励
     */
    fun loadTime(pageTime: String, videoTime: String, link: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["pageTime"] = pageTime
        map["videoTime"] = videoTime
        map["link"] = link ?: ""
        logEvent(KEY_SWAP_TIME, map)
    }

    /**
     * 点击广告
     * rewarded 激励
     */
    fun clickAd(what: String, adInfo: String? = null) {
        val map: MutableMap<String, Any?> = HashMap()
        map["what"] = what
        adInfo?.let { map["ad_info"] = it }
        logEvent(KEY_CLICK_AD, map)
    }

    /**
     * 进入页面
     * @param page  页面
     */
    fun inPage(page: String, arguments: Map<String, Any?>? = null) {
        val map: MutableMap<String, Any?> = HashMap()
        map["page"] = page
        arguments?.let {
            map.putAll(it)
        }
        logEvent(KEY_IN_PAGE + "_" + page, map)
    }

    /**
     * 搜索
     * @param keywords String
     */
    fun search(
        keywords: String?,
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["keywords"] = keywords
        logEvent(KEY_SEARCH, map)
    }

    /**
     * 发起换脸
     * type类型，media_id素材id, sources要替换的人脸
     */
    fun taskSend(media_id: String?, type: String, sources: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["media_id"] = media_id
        map["type"] = type
        map["sources"] = sources
        logEvent(KEY_AITASK_SEND, map)
    }

    /**
     * 点击收藏
     */
    fun clickCollect(
        id: String?,
        type: String?
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["id"] = id
        map["type"] = type
        if (type == "media") {
            logEvent(KEY_CLICK_COLLECT + "_media", map)
        } else {
            logEvent(KEY_CLICK_COLLECT, map)
        }

    }

    /**
     * 取消收藏
     */
    fun clickDisCollect(
        id: String?
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["id"] = id
        logEvent(KEY_CLICK_DISCOLLECT, map)
    }

    /**
     * 取消收藏
     */
    fun clickDisCollectMedia(
        id: String?
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["id"] = id
        logEvent(KEY_CLICK_DISCOLLECT_MEDIA, map)
    }

    /**
     * 点击下载
     */
    fun clickDownload(
        downloadUrl: String?
    ) {
        val map: MutableMap<String, Any?> = HashMap()
        map["download_url"] = downloadUrl
        logEvent(KEY_CLICK_DOWNLOAD, map)
    }

    /**
     * 点击上传人脸
     */
    fun clickUploadFace() {
        logEvent(KEY_UPLOAD_FACE)
    }

    /**
     * 人脸检测
     */
    fun clickCheck(page: String?, taskId: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        map["taskId"] = taskId
        logEvent(KEY_CHECK + "_" + page, map)
    }

    /**
     * 保存人脸
     */
    fun clickCheck(tag: String) {
        val map: MutableMap<String, Any?> = HashMap()
        map["tag"] = tag
        logEvent(KEY_SAVE_FACE, map)
    }

    /**
     * 支付成功前的界面
     * @param bean
     * @param step
     * commit          发起支付
     * success         支付成功
     * fail            支付失败
     * cancel          取消支付
     */
    fun payPage(bean: VipSchemeBean, orderNo: String = "") {
        val map: MutableMap<String, Any?> = HashMap()
        map["page_name"] = GVM.INSTANT.payPage.value
        map["scheme_id"] = bean.id
        map["title"] = bean.name
        map["sku"] = bean.productId
        map["orderNo"] = orderNo
        map["showPrice"] = bean.showPrice
        //商品价格
        map["price"] = bean.price
        logEvent(KEY_PAY_PAGE, map)
    }

    /**
     * 支付
     * @param bean
     * @param step
     */
    fun pay(bean: VipSchemeBean, step: String, errorMsg: String? = null, orderNo: String = "") {
        val map: MutableMap<String, Any?> = HashMap()
        map["step"] = step
        map["scheme_id"] = bean.id
        map["title"] = bean.name
        map["sku"] = bean.productId
        map["subscription"] = bean.subscription
        map["orderNo"] = orderNo
        //商品价格
        map["showPrice"] = bean.showPrice
        map["price"] = bean.price
        if (errorMsg != null) {
            map["error_msg"] = errorMsg
        }
        logEvent(KEY_PAY + "_" + step, map)
    }

    /**
     * google 支付
     * @param purchase
     * @param allPrice
     */
//    (id=12, day=365, zkday=0, name=1 Yearly, oldPrice=39.99, price=39.99, productId=1_year_discount, type=1, remark=1460, description=Cancel anytime in 'Google Play -> Payments & Subscriptions'., showPrice=0.32, oldGooglePrice=39.99, formattedPrice=US$0.32, subscription=1, trialPriceTitle=P3D, showUnit=US$, showCode=USD)
    fun logGooglePayEvent(purchase: Purchase, allPrice: Double, showCode: String) {
        val code = if (showCode == "") CURRENCY else showCode
        Singular.revenue(code, allPrice, purchase)
    }
}