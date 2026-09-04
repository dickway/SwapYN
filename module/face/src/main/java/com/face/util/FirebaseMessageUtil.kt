package com.face.util

import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.singular.sdk.Singular
import com.face.BuildConfig
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.postIfNot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author 再战科技
 * @date 2022/10/11
 * @description 主题 [a-zA-Z0-9-_.~%]{1,900}
 */
object FirebaseMessageUtil {

    val tokenLiveData = MutableLiveData<String>()

    fun init() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveToken(task.result)
                LogUtils.e(">>>>>FirebaseMessaging token ${task.result}")
            } else {
                LogUtils.e(">>>>FirebaseMessaging token failed \n${task.exception}")
            }
        }
    }

    fun subscribeApp() {//用于首页获取渠道后刷新订阅

        val lastVersion = SPUtils.lastVersion
        val newVersion = "Version${BuildConfig.VERSION_CODE}"
        if (lastVersion.isNotBlank() && lastVersion != newVersion) {
            unsubscribeTopic(lastVersion)
        }
        subscribeTopic(newVersion){
            SPUtils.lastVersion =newVersion
        }

        if (SPbaseUtils.loadAB == 1) {
            subscribeTopic("PageB")
            unsubscribeTopic("PageA")
        } else {
            subscribeTopic("PageA")
            unsubscribeTopic("PageB")
        }
        val timeZone = try {
            TimeUtil.getTimeZone()
        } catch (t: Throwable) {
            EventUtil.recordExceptionToFirebase(t)
            null
        }

        val lastTimeZone = SPUtils.lastTimeZone
        if (lastTimeZone.isNotBlank() && timeZone != lastTimeZone) {
            unsubscribeTopic(lastTimeZone)
        }
        if (!timeZone.isNullOrBlank()) {
            subscribeTopic(timeZone) {
                SPUtils.lastTimeZone = timeZone
            }
        }

        val lastLanguage = SPUtils.lastLanguage.uppercase()
        if (lastLanguage.isNotBlank() && SPbaseUtils.spLanguage.uppercase() != lastLanguage) {
            unsubscribeTopic(lastLanguage)
        }
        if (SPbaseUtils.spLanguage != "") {
            subscribeTopic(SPbaseUtils.spLanguage.uppercase()) {
                SPUtils.lastLanguage = SPbaseUtils.spLanguage
            }
        }
    }

    fun saveToken(token: String) {
        Singular.setFCMDeviceToken(token)
        tokenLiveData.postIfNot(token)
        uploadPushToken()
    }

    fun uploadPushToken() {
        if (GVM.INSTANT.userInfo.value.token.isNotBlank()) {
            tokenLiveData.value?.takeIf { it.isNotBlank() }?.let {
                GlobalScope.launch {
                    Repository.savePushToken(it)
                }
            }
        }
    }

    fun subscribeTopic(topic: String, onComplete: ((Boolean) -> Unit)? = null) {
        Firebase.messaging.subscribeToTopic(topic).addOnCompleteListener { task ->
            LogUtils.d("subscribe $topic Topic ${if (task.isSuccessful) "success" else "fail"}")
            onComplete?.invoke(task.isSuccessful)
        }
    }

    fun unsubscribeTopic(topic: String, onComplete: ((Boolean) -> Unit)? = null) {
        Firebase.messaging.unsubscribeFromTopic(topic).addOnCompleteListener { task ->
            LogUtils.d("unsubscribe $topic Topic ${if (task.isSuccessful) "success" else "fail"}")
            onComplete?.invoke(task.isSuccessful)
        }
    }

    fun logout() {
        unsubscribeTopic(SPUtils.checkInNotificationTopic)
    }
}