package com.face.ui

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.util.UnstableApi
import com.face.BuildConfig
import com.face.R
import com.face.key.Constants.clipsPath
import com.face.key.Constants.opencvPath
import com.face.key.Constants.savePath
import com.face.key.Constants.saveVideoPath
import com.face.key.Constants.voicePath
import com.face.util.AppLifecycle
import com.face.util.DeviceUtil
import com.face.util.GVM
import com.face.util.InstallReferrerUtil
import com.face.util.SPUtils
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.initialize
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.lifecycle.DefaultActivityLifecycle
import com.zzkj.structure.net.NetworkUtils
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.device.DeviceUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.io.File


class App : BaseApp() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var INSTANCE: App
            private set
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initFile()
        initFirebase()
        initFacebook()
//        OpenCVLoader.initLocal()
        initLog()
        initDeviceId()
        SPbaseUtils.spTheme = true
        NetworkUtils.init()
        InstallReferrerUtil.init()

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycle())
        initRefreshLayout()
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
//      GSYVideoType.setRenderType(GSYVideoType.SUFRACE)
        if (SPbaseUtils.spCache) {
            // 清理 GSY 播放缓存
            GSYVideoManager.instance().clearAllDefaultCache(this)
            // 标记不再是第一次启动
            SPbaseUtils.spCache = false
        }
//        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
    }


    private fun initDeviceId() {
        if (SPUtils.deviceId.isBlank()) {
            GlobalScope.launch {
                DeviceUtil.getDeviceId().let { deviceId ->
                    SPUtils.deviceId = deviceId
                    GVM.INSTANT.takeIf { it.userInfo.value.userId == 0 }?.apply {
                        setThirdUserId(DeviceUtils.getAndroidID())
                    }
                }
            }
        }
    }


    private fun initLog() {
        // release 日志用的空实现
//        LogUtils.getLogConfig()
//            .configAllowLog(BuildConfig.DEBUG)
        com.blankj.utilcode.util.LogUtils.getConfig().run {
            setLogSwitch(BuildConfig.DEBUG)
            setConsoleSwitch(BuildConfig.DEBUG)
        }
    }

    fun initFacebook() {
        FacebookSdk.setAutoLogAppEventsEnabled(false)
        val customUserId: String
        val userId = GVM.INSTANT.userInfo.value.userId
        customUserId = if (userId != 0) {
            userId.toString()
        } else {
            DeviceUtils.getAndroidID()
        }
        AppEventsLogger.setUserID(customUserId)
    }

    private fun initRefreshLayout() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            MaterialHeader(context).apply {
                setColorSchemeResources(R.color.colorAppBg)
            }
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(context).setDrawableArrowSize(0f);//设置箭头图片
        }
        ClassicsHeader.REFRESH_HEADER_PULLING = "Pull down to refresh"
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "Refreshing..."
        ClassicsHeader.REFRESH_HEADER_LOADING = "Loading..."//Wait for loading...
        ClassicsHeader.REFRESH_HEADER_RELEASE = "Release to refresh"
        ClassicsHeader.REFRESH_HEADER_FINISH = "Refresh success"
        ClassicsHeader.REFRESH_HEADER_FAILED = "Refresh failed"
        ClassicsHeader.REFRESH_HEADER_SECONDARY = "Release to second floor"
        ClassicsHeader.REFRESH_HEADER_UPDATE = "'Last update' d-MMM HH:mm"
        ClassicsFooter.REFRESH_FOOTER_PULLING = "Loading..."
        ClassicsFooter.REFRESH_FOOTER_RELEASE = "Release to load more"
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = ""//Wait for refreshing...
        ClassicsFooter.REFRESH_FOOTER_LOADING = "Loading..."
        ClassicsFooter.REFRESH_FOOTER_FINISH = "Loading..."
        ClassicsFooter.REFRESH_FOOTER_FAILED = "Load failed"
        ClassicsFooter.REFRESH_FOOTER_NOTHING = "No more data"

    }

    private fun initFirebase() {
        Firebase.initialize(this)
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        val userId = GVM.INSTANT.userInfo.value.userId
        if (userId != 0) {
            Firebase.crashlytics.setUserId(userId.toString())
            Firebase.analytics.setUserId(userId.toString())
        } else {
            val deviceId = SPUtils.deviceId
            if (deviceId.isNotBlank()) {
                Firebase.crashlytics.setUserId(deviceId)
                Firebase.analytics.setUserId(deviceId)
            }
        }
    }

    private fun initFile() {

        val voicePath = File(voicePath)
        if (!voicePath.exists()) voicePath.mkdirs()

        val saveVideoPath = File(saveVideoPath)
        if (!saveVideoPath.exists()) saveVideoPath.mkdirs()

        val savePath = File(savePath)
        if (!savePath.exists()) {
            savePath.mkdirs()
        } else {
            // 获取文件夹中的所有文件
            val files = savePath.listFiles()
            // 遍历文件夹中的每个文件，删除它们
            files?.forEach { file ->
                if (file.isFile) file.delete()
            }
        }

        val clipPath = File(clipsPath)
        if (!clipPath.exists()) clipPath.mkdirs()

        val openPath = File(opencvPath)
        if (!openPath.exists()) openPath.mkdirs()

    }

    override fun getActivityLifecycle(): DefaultActivityLifecycle {
        return DefaultActivityLifecycle()
    }
}