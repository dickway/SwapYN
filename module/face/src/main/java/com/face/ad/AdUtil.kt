package com.face.ad

import android.graphics.Color
import com.blankj.utilcode.util.LogUtils
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.face.BuildConfig
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.net.Repository
import com.face.ui.App
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.google.common.math.IntMath
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.toastIfDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * @author 再战科技
 * https://developers.google.com/android/reference/com/google/android/gms/ads/AdRequest#ERROR_CODE_NO_FILL
 * @description
 */
object AdUtil {

    var SDK_KEY = ""
    var AD_UNIT_BANNER = ""
    private var AD_UNIT_REWARDED = ""
    private var AD_UNIT_INTERSTITIAL = ""
    private var AD_UNIT_APPOPEN = ""


    // 只要调过ApplovinSdk.getInstance()后ApplovinSdk.getInstance().isInitialized就为true了，所以自己判断
    private var isInit = false

    private const val VIDEO_AD_LOADING_TIMEOUT = 120 * 1000L

    private val coroutineScope: CoroutineScope = CoroutineScope(
        EmptyCoroutineContext + Dispatchers.IO
    )

    private var openAd: MaxAppOpenAd? = null
    private var rewardedAd: MaxRewardedAd? = null
    private var interstitialAd: MaxInterstitialAd? = null
    private var bannerAd: MaxAdView? = null


    // 加载出来后需要直接显示
    private var shouldShowRewardedAfterLoaded = 0L
    private var shouldShowInterstitialAfterLoaded = 0L

    private var isRewardedLoading = false
    private var isRewardedShowing = false
    private var isInterstitialLoading = false
    private var isInterstitialShowing = false

    // 重试任务
    private var getKeyJob: Job? = null

    // 是否显示插屏代替激励（需要显示激励广告但激励广告没准备好时用插屏代替）
    private var isShowIvReplaceRv = false


    // 取消不显示广告
    var cancelAds = false

    // 已显示广告
    var showAds = false

    // 供外部回调
    var onRewardedAdListener: DefaultMaxRewardedAdListener? = null

    fun init() {
        if (!isInit) {
            getAdKey()
        }
    }


    private fun getAdKey() {
        getKeyJob?.cancel()
        getKeyJob = coroutineScope.launch(Dispatchers.IO) {
            var times = 0
            while (isActive) {
                val response = Repository.getSysConfig("ad_key")
                if (!response.mIsSuccess) {
                    // 次数平方，最大30s
                    delay(IntMath.pow(++times, 2).coerceAtMost(30) * 1000L)
                    continue
                }
                val map = response.mData?.toMap()
                if (!map.isNullOrEmpty()) {
                    SDK_KEY = map["sdk_key"] ?: ""
                    AD_UNIT_REWARDED = map["rv"] ?: ""
                    AD_UNIT_BANNER = map["banner"] ?: ""
                    AD_UNIT_INTERSTITIAL = map["iv"] ?: ""
                    AD_UNIT_APPOPEN = map["op"] ?: ""

                    withContext(Dispatchers.Main) {
                        innerInit()
                    }
                }
                val result = if (map.isNullOrEmpty()) "fail" else "success"
                EventUtil.logEvent(
                    name = "get_ad_key_$result",
                    mapParameter = mutableMapOf<String, Any?>("result" to result).apply {
                        if (!map.isNullOrEmpty()) {
                            putAll(map)
                        }
                    }
                )
                break
            }
        }
    }

    private fun innerInit() {
        if (SDK_KEY.isBlank()) return
        val initConfig = AppLovinSdkInitializationConfiguration
            .builder(SDK_KEY)
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .setTestDeviceAdvertisingIds(
                listOf(
                    "be8dc13f-9ef2-4503-a701-10ca1ad1abe1",
                )
            )
            .build()
        AppLovinSdk.getInstance(App.INSTANCE).settings.apply {
            setVerboseLogging(BuildConfig.DEBUG)
            isMuted = true
            userIdentifier = GVM.INSTANT.userInfo.value.userId
                .takeIf { it != 0 }?.toString() ?: SPUtils.deviceId
        }
        AppLovinPrivacySettings.setHasUserConsent(true)
        AppLovinPrivacySettings.setDoNotSell(false)
        AppLovinSdk.getInstance(App.INSTANCE).initialize(initConfig) {
            LogUtils.iTag("AdUtil", "ApplovinSdk initialize $it")
            initRewardedAd()
            initInterstitialAd()
            initBannerAd()
        }
        isInit = true
    }


    /**
     * 如果可以的话 显示激励广告,带有监听事件的
     * @return Boolean 是否直接显示成功
     */
    fun showRewardedAdForRewarded(
        onRewardedAdListener: DefaultMaxRewardedAdListener? = null,
        cancelT: Boolean = false,
    ): Boolean {
        cancelAds = cancelT
        showAds = cancelT
        isShowIvReplaceRv = cancelT
        this.onRewardedAdListener = onRewardedAdListener
        if (cancelAds
            || isInterstitialShowing
            || isRewardedShowing
        ) {
            toastIfDebug("不应显示激励广告")
            return false
        }
        val curActivity = AppManager.getCurActivity() ?: return false
        return if (isRewardedReady()) {
            LogUtils.i(">>激励广告")
            showAds = true
            shouldShowRewardedAfterLoaded = 0
            rewardedAd?.showAd(curActivity)
            true
        } else if (isInterstitialReady()) {
            showAds = true
            LogUtils.i(">>显示插屏代替激励")
            toastIfDebug("显示插屏代替激励")
            isShowIvReplaceRv = true
            interstitialAd?.showAd(curActivity)
            true
        } else {
            LogUtils.i(">>激励和插屏都没准备好")
            toastIfDebug("激励和插屏都没准备好")
            isShowIvReplaceRv = true
            shouldShowRewardedAfterLoaded = System.currentTimeMillis()
            shouldShowInterstitialAfterLoaded = System.currentTimeMillis()
            loadRewardedAdIfNeed()
            loadInterstitialAdIfNeed()
            false
        }
    }

    /**
     * 如果可以的话 显示插屏广告
     * @return Boolean 是否直接显示成功
     */
    fun showInterstitialAdIfNeed(cancelT: Boolean = false): Boolean {
        cancelAds = cancelT
        showAds = cancelT
        isShowIvReplaceRv = cancelT
        // 根据 isShowTool 选择对应的次数限制
        val limit = if (GVM.INSTANT.isShowTool.value == 1) {
            SPUtils.interstitialNum       // 原逻辑里的 B 面次数
        } else {
            SPUtils.interstitialNumA      // 原逻辑里的 A 面次数
        }
        // 判断是否需要显示插屏广告
        val shouldHideAd =
            limit < 0 ||                                // 限制为负：不显示广告
                    GVM.INSTANT.userInfo.value.mediaNum < limit ||  // 生成次数未达到限制：不显示广告
                    GVM.INSTANT.userInfo.value.isVip()          // VIP：不显示广告
        LogUtils.v(">插屏广告:${isShowIvReplaceRv} ${cancelAds}  ${(isShowIvReplaceRv && cancelAds)}")
        if (
            (isShowIvReplaceRv && cancelAds) ||
            shouldHideAd ||
            isInterstitialShowing ||
            isRewardedShowing
        ) {
            toastIfDebug("不应显示插屏广告")
            return false
        }

        return if (isInterstitialReady()) {
            showAds = true
            shouldShowInterstitialAfterLoaded = 0
            val curActivity = AppManager.getCurActivity() ?: return false
            interstitialAd?.showAd(curActivity)
            true
        } else {
            toastIfDebug("插屏广告没准备好")
            shouldShowInterstitialAfterLoaded = System.currentTimeMillis()
            loadInterstitialAdIfNeed()
            false
        }
    }

    /**
     * 初始化开屏广告
     */
    fun showOpenAdIfNeed() {
        // 是否启用开屏广告功能(允许显示广告), 判断vip
        if (BuildConfig.DEBUG || GVM.INSTANT.isShowTool.value == 0 || SPUtils.openNum < 0 || SPUtils.openAppNum < SPUtils.openNum || GVM.INSTANT.userInfo.value.isVip()
        ) {//生成次数小于设置，和会员 不展示插屏广告
            toastIfDebug("不应显示开屏广告")
            return
        }
        initAppopenAd()
    }

//    /**
//     * 显示开屏广告
//     * @return Boolean 是否直接显示成功
//     */
//    fun showOpenAd(adContainer: ViewGroup): Boolean {
//        return if (isOpenReady()) {
//            openAd?.showAd(adContainer)
//            true
//        } else {
//            if (openAd == null) {
//                initAppopenAd()
//            }
//            false
//        }
//    }


    /**
     * 初始化激励广告
     */
    private fun initRewardedAd() {
        if (rewardedAd != null) return
        val id = AD_UNIT_REWARDED.takeUnless { it.isBlank() } ?: return
        rewardedAd = MaxRewardedAd.getInstance(id)
        rewardedAd?.setListener(object : DefaultMaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                LogUtils.v(">Reward：onAdLoaded")
                onRewardedAdListener?.onAdLoaded(ad, rewardedAd)
                isRewardedLoading = false
                if (shouldShowRewardedAfterLoaded > 0) {
                    shouldShowRewardedAfterLoaded = 0
                    shouldShowInterstitialAfterLoaded = 0
                    showRewardedAdForRewarded(cancelT = cancelAds)
                }
            }

            override fun onAdDisplayed(ad: MaxAd) {// 广告展示
                LogUtils.v(">RewardedAd onAdDisplayed")
                isRewardedShowing = true
                onRewardedAdListener?.onAdDisplayed(ad)
                EventUtil.showAd("rewarded", "success", ad.toString())
            }

            override fun onAdHidden(ad: MaxAd) {// 激励广告看完
                LogUtils.v(">>>>RewardedAd onAdHidden")
                isShowIvReplaceRv = false
                isRewardedShowing = false
                onRewardedAdListener?.onAdHidden(ad)
                loadRewardedAd()
                EventUtil.rewardedCompleted(ad.toString())
            }

            override fun onAdClicked(ad: MaxAd) {
                onRewardedAdListener?.onAdClicked(ad)
                EventUtil.clickAd("rewarded", ad.toString())
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                LogUtils.v("RewardedAd onAdDisplayFailed\ncode = ${error.code}\nmsg = ${error.message}")
                isRewardedLoading = false
                onRewardedAdListener?.onAdDisplayFailed(ad, error)
                EventUtil.showAd("rewarded", "fail", "code = ${error.code} msg = ${error.message}")
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                LogUtils.v("RewardedAd onAdLoadFailed\ncode = ${error.code}\nmsg = ${error.message}")
                isRewardedLoading = false
                onRewardedAdListener?.onAdLoadFailed(adUnitId, error)
                EventUtil.showAd("rewarded", "fail", "code = ${error.code} msg = ${error.message}")
            }

            override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
                LogUtils.v(">TradPlusLog Reward： onAdReward")
                GVM.INSTANT.hasAD.postValue(true)
                onRewardedAdListener?.onAdReward(ad, reward)
            }
        })
        rewardedAd?.loadAd()
    }


    /**
     * 初始化插屏广告
     */
    private fun initInterstitialAd() {
        if (interstitialAd != null) return
        val id = AD_UNIT_INTERSTITIAL.takeUnless { it.isBlank() } ?: return
        interstitialAd = MaxInterstitialAd(id)
        interstitialAd?.setListener(object : MaxAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                LogUtils.v(">Interstitial onAdLoaded:${isShowIvReplaceRv}")
                if (isShowIvReplaceRv) {
                    onRewardedAdListener?.onAdLoaded(ad)
                }
                isInterstitialLoading = false
                if (shouldShowInterstitialAfterLoaded > 0) {
                    shouldShowRewardedAfterLoaded = 0
                    shouldShowInterstitialAfterLoaded = 0
                    showInterstitialAdIfNeed(cancelT = cancelAds)
                }
            }

            override fun onAdDisplayed(ad: MaxAd) {
                LogUtils.v(">Interstitial onAdDisplayed:${isShowIvReplaceRv}")
                if (isShowIvReplaceRv) {
                    onRewardedAdListener?.onAdDisplayed(ad)
                }
                isInterstitialShowing = true
                EventUtil.showAd("interstitial", "success", ad.toString())
            }

            override fun onAdHidden(ad: MaxAd) {
                LogUtils.v(">Interstitial onAdClosed:${isShowIvReplaceRv}")
                if (isShowIvReplaceRv) {
                    GVM.INSTANT.hasAD.postValue(true)
                    onRewardedAdListener?.onAdHidden(ad)
                    onRewardedAdListener?.onAdReward(ad, null)
                    isShowIvReplaceRv = false
                }
                isInterstitialShowing = false
                loadInterstitialAd()
            }

            override fun onAdClicked(p0: MaxAd) {
                EventUtil.clickAd("interstitial", p0.toString())
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                if (isShowIvReplaceRv) {
                    onRewardedAdListener?.onAdLoadFailed(adUnitId, error)
                }
                EventUtil.showAd(
                    "interstitial",
                    "fail",
                    "code = ${error.code} msg = ${error.message}"
                )
                isInterstitialLoading = false
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
//                loadInterstitialAd()
                LogUtils.v(">Interstitial onAdFailed:${error.message}")
                if (isShowIvReplaceRv) {
                    onRewardedAdListener?.onAdLoadFailed(ad.adUnitId, error)
                }
                EventUtil.showAd(
                    "interstitial",
                    "fail",
                    "code = ${error.code} msg = ${error.message}"
                )
                isInterstitialLoading = false
            }
        })
        interstitialAd?.loadAd()
    }


    /**
     * 初始化开屏广告
     */
    private fun initAppopenAd() {
        if (openAd != null || !isInit) return
        val id = AD_UNIT_APPOPEN.takeUnless { it.isBlank() } ?: return
        openAd = MaxAppOpenAd(id)
        openAd?.setListener(object : DefaultMaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                openAd?.showAd()
            }

            override fun onAdDisplayed(ad: MaxAd) {
                EventUtil.showAd("appopen", "success", ad.toString())
            }

            override fun onAdHidden(ad: MaxAd) {// 广告看完
                EventUtil.showAd("appopen", "success", ad.toString())
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                LogUtils.v(">>>>onAdLoadFailed\ncode = ${error.code}\nmsg = ${error.message}")
                EventUtil.showAd("appopen", "fail", "code = ${error.code} msg = ${error.message}")
            }
        })
        openAd?.loadAd()
    }


    private fun loadRewardedAdIfNeed(): Boolean {
        return if (!isRewardedShowing
            && !isRewardedReady()
        ) {
            loadRewardedAd()
            true
        } else {
            false
        }
    }


    private fun loadInterstitialAdIfNeed(): Boolean {
        return if (!isInterstitialShowing
            && !isInterstitialReady()
        ) {
            loadInterstitialAd()
            true
        } else {
            false
        }
    }

    /**
     * 加载激励广告
     */
    private fun loadRewardedAd() {
        if (rewardedAd == null) {
            initRewardedAd()
        }
        rewardedAd?.takeIf { !it.isReady }?.let {
            isRewardedLoading = true
            it.loadAd()
        }
    }


    /**
     * 加载插页广告
     */
    private fun loadInterstitialAd() {
        if (interstitialAd == null) {
            initInterstitialAd()
        }
        interstitialAd?.takeIf { !it.isReady }?.let {
            isInterstitialLoading = true
            it.loadAd()
        }
    }

    /**
     * 创建横幅广告
     * @return MaxAdView
     */
    fun initBannerAd() {
        if (!SPUtils.enableAd) return
        val id = AD_UNIT_BANNER.takeUnless { it.isBlank() } ?: return
        bannerAd = MaxAdView(id, MaxAdFormat.BANNER).apply {
            setBackgroundColor(Color.TRANSPARENT)
        }
        bannerAd?.setListener(object : DefaultMaxBannerAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                LogUtils.v(">banner:onAdLoaded")
            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                LogUtils.v(">banner:onAdLoadFailed")
                EventUtil.showAd("banner", "fail", error.toString())
            }
        })
        bannerAd?.loadAd()
    }

    /**
     * 横幅广告
     * result: (MaxAdView) -> Unit
     */
    fun getBannerAd(): MaxAdView? {
        return bannerAd
    }

    /**
     * 激励广告是否准备好
     * @return Boolean
     */
    fun isRewardedReady() = rewardedAd?.isReady == true

    /**
     * 插屏广告是否准备好
     * @return Boolean
     */
    fun isInterstitialReady() = interstitialAd?.isReady == true

    /**
     * 销毁所有，退出时调
     */
    /**
     * 销毁所有，退出时调
     */
    fun destroyAllAd() {
        getKeyJob?.cancel()
        getKeyJob = null

        isInterstitialLoading = false
        interstitialAd?.destroy()
        interstitialAd = null

        openAd?.destroy()
        openAd = null

        isRewardedShowing = false
        isRewardedLoading = false
        rewardedAd?.destroy()
        rewardedAd = null

        bannerAd?.destroy()
        bannerAd = null

        shouldShowRewardedAfterLoaded = 0L
        shouldShowInterstitialAfterLoaded = 0L
    }


}
