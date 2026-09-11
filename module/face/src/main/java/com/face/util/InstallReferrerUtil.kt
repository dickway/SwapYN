package com.face.util

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.blankj.utilcode.util.LogUtils
import com.face.ui.App.Companion.INSTANCE

/**
 * @author 再战科技
 * @date 2021/11/15
 * @description
 */
object InstallReferrerUtil {

    /**
     * https://developer.android.com/reference/com/android/installreferrer/api/ReferrerDetails#getgoogleplayinstantparam
     */
    fun init() {
        val installReferrerClient = InstallReferrerClient.newBuilder(INSTANCE).build()
        installReferrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    try {
                        updateData(installReferrerClient.installReferrer)
                    } catch (ex: Exception) {
                        LogUtils.w("InstallReferrerUtil fail:%s", ex.toString())
                        SPUtils.installAwarded = 2
                    } finally {
                        installReferrerClient.endConnection()
                    }
                } else {
                    LogUtils.w("InstallReferrerUtil code:%s", responseCode)
                    SPUtils.installAwarded = 2
                }
            }

            override fun onInstallReferrerServiceDisconnected() {}
        })
    }

    fun updateData(details: ReferrerDetails?) {
        if (details == null) return
        /**
         * https://developers.google.com/analytics/devguides/collection/android/v4/campaigns#google-play-url-builder
         * 已安装软件包的引荐来源网址
         * utm_source:   广告系列来源，用于确定具体的搜索引擎、简报或其他来源
         * utm_medium:   广告系列媒介，用于确定电子邮件或采用每次点击费用 (CPC) 的广告等媒介
         * utm_term:     广告系列字词，用于付费搜索，为广告提供关键字
         * utm_content:  广告系列内容，用于 A/B 测试和内容定位广告，以区分指向相同网址的不同广告或链接
         * utm_campaign: 广告系列名称，用于关键字分析，以标识具体的产品推广活动或战略广告系列
         * gclid:        Google Ads 自动标记参数，用于衡量广告。此值会动态生成，请勿修改
         */
//            "tm_source=apps.facebook.com\u0026utm_campaign=fb4a\u0026utm_content={\"app\":717668791146281,\"t\":1775181053,\"source\":{\"data\":\"d761acfbb108c98717325746969655e72123c7ad3725b64a225b8b9778fec53e4ea2dc11ee5d482e4323826388f67d6afea6c004e888eb634209a737bc266af1a3283afac9f6"
        val installReferrer = details.installReferrer
        if (!installReferrer.isNullOrBlank()) {
            SPUtils.installReferrer = installReferrer
            //安装版本
            SPUtils.installVersion = details.installVersion ?: ""
            // 安装开始时的时间戳（以秒为单位）
            SPUtils.installTime = details.installBeginTimestampServerSeconds
            // 引荐来源网址点击事件发生时的时间戳（以秒为单位）
            SPUtils.installClickTime = details.referrerClickTimestampServerSeconds
            SPUtils.installAwarded = 1
            LogUtils.i("getInstallReferrer:%s", installReferrer)
            SPUtils.installReferrer = installReferrer
        }
        if (!SPUtils.installPost) {
            EventUtil.installReferrer {
                if (it) {
                    SPUtils.installPost = true
                }
            }
        }
    }
}