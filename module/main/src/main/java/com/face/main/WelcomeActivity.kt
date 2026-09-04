package com.face.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import com.a.activity.ACompletionActivity
import com.a.activity.ALoginActivity
import com.a.activity.AMainActivity
import com.a.activity.AVipActivity
import com.face.ad.AdUtil
import com.face.main.databinding.ActivityWelcomeBinding
import com.face.ui.App
import com.face.ui.BaseBindingActivity
import com.face.ui.MainActivity
import com.face.util.EventUtil
import com.face.util.FirebaseMessageUtil
import com.face.util.GVM
import com.face.util.NotificationUtil
import com.face.util.SPUtils
import com.face.viewmodel.InitViewModel
import com.key.DiffKey
import com.key.SingularListener
import com.singular.sdk.SingularConfig
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.device.DeviceUtils
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.moshi.MoshiHelper


class WelcomeActivity : BaseBindingActivity<ActivityWelcomeBinding, InitViewModel>(
    R.layout.activity_welcome,
    InitViewModel::class.java
) {
    override var isRestartApp = false

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initValue()
        handleIntent(intent)
    }

    override fun init(savedInstanceState: Bundle?) {
        bar { transparent() }
        onBackPressedDispatcher.addCallback(this) {
            // 什么都不做，屏蔽返回
        }

        SPUtils.fishTask = ""
        SPUtils.tokenUser=""
//        打开app
        EventUtil.startApp()
//        初始化值
        initValue()
//        聚合获取渠道
        initSingular()
//        广告初始
        AdUtil.init()
//        通知初始化
        FirebaseMessageUtil.init()
        //登录后刷新用户数据
        if (GVM.INSTANT.isLogin()) GVM.INSTANT.refreshUserInfo(5)
        //刷新初始化后台数据
        mModel.retryRefresh()
        //调试和分析崩溃提供更多的信息
        mModel.getInitConfig()
        //刷新数据结果
        mModel.IsSuccess.observe(this@WelcomeActivity) {
            if (it) {
                //显示开屏广告
                AdUtil.showOpenAdIfNeed()
                if (GVM.INSTANT.isShowTool.value == 0) {
                    handleIntentA(intent)
                } else {
                    handleIntent(intent)
                }
                finish()
            }
        }
    }

    fun rBtn() {
        EventUtil.startRetry()
        mModel.retryRefresh(true)
    }

    private fun handleIntentA(intent: Intent?) {
        if (intent == null || intent.extras == null) {//如果不为空是推送打开
            // 设置目标 Activity 的完整类路径
            val mIntent = Intent().setClassName(
                App.INSTANCE.packageName,
                SPUtils.aMainName
            )
            startActivity(mIntent)
            return
        }
        intent.extras?.apply {
            EventUtil.pushClick(getString("task_id") ?: "", getString("media_id") ?: "")
            var msgType = getString("msg_type", "0")
            if (msgType == "0") {//msg_type推送后端不固定是int 还是string
                msgType = getInt("msg_type", 0).toString()
            }
            val taskId = getString("task_id", "")
            val mediaId = getString("media_id", "")
            val feedbackId = getString("feedback_id", "")
            val mIntent = Intent().setClassName(
                App.INSTANCE.packageName,
                SPUtils.aMainName
            )
            mIntent.putExtra("msg_type", msgType.toIntOrZero())
            mIntent.putExtra("task_id", taskId)
            mIntent.putExtra("media_id", mediaId)
            mIntent.putExtra("feedback_id", feedbackId)
            startActivity(mIntent)
        }
    }

    private fun handleIntent(intent: Intent?) {//推送打开app获取数据跳转完成界面
        NotificationUtil.deleteNotification(NotificationUtil.SIGN_ID)//打开移除签到提醒
//        NotificationUtil.deleteNotification(NotificationUtil.NEW_ID)//打开移除热门素材提醒
//        NotificationUtil.deleteNotification(NotificationUtil.HOT_ID)//打开移除新素材提醒
        if (intent == null || intent.extras == null) {//如果不为空是推送打开
            openActivity<MainActivity>()
            return
        }
        intent.extras?.apply {
            if (getBoolean("isSetting", false)) {//如果不为空是推送打开
                openActivity<MainActivity>()
                return
            }
            EventUtil.pushClick(getString("task_id") ?: "", getString("media_id") ?: "")
            var msgType = getString("msg_type", "0")
            if (msgType == "0") {
                msgType = getInt("msg_type", 0).toString()
            }
            val taskId = getString("task_id", "")
            val mediaId = getString("media_id", "")
            val feedbackId = getString("feedback_id", "")
            openActivity<MainActivity> {
                putInt("msg_type", msgType.toIntOrZero())
                putString("task_id", taskId)
                putString("media_id", mediaId)
                putString("feedback_id", feedbackId)
            }
        }
    }

    private fun initValue() {
        GVM.INSTANT.isPostAB.value = -1//是否上报ab面的标识
        SPUtils.aLoginName = ALoginActivity::class.java.name
        SPUtils.aMainName = AMainActivity::class.java.name
        SPUtils.aVipName = AVipActivity::class.java.name
        SPUtils.aCompletionName = ACompletionActivity::class.java.name
        SPUtils.mainAct = WelcomeActivity::class.java.name
    }


    private fun initSingular() {
        val customUserId: String
        val userId = GVM.INSTANT.userInfo.value.userId
        customUserId = if (userId != 0) {
            userId.toString()
        } else {
            DeviceUtils.getAndroidID()
        }
        val config = SingularConfig(DiffKey.SINGULAR_KEY, DiffKey.SINGULAR_SECRET)
            .withOAIDCollection()
            .withLimitDataSharing(false)
            .withCustomUserId(customUserId)
        if (BuildConfig.DEBUG) {
            config.withLoggingEnabled()
            config.logLevel = 1
        }
        config.withFacebookAppId(getString(com.key.R.string.facebook_app_id));
        config.withSingularLink(intent) { params ->
            val deeplink = params?.deeplink ?: ""
            val passthrough = params?.passthrough ?: ""
            val isDeferred = params?.isDeferred
            EventUtil.singularLink(deeplink, passthrough, isDeferred)
        }
        DiffKey.initSingular(this, config, object : SingularListener {
            override fun withSingularDeviceAttribution(
                isUse: Boolean,
                attribution: Map<String, Any>?
            ) {
                if (isUse) {
                    EventUtil.singularReferrer(MoshiHelper.convertObjectToJson(attribution))
                }
            }
        })
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}