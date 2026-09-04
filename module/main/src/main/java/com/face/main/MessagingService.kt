package com.face.main

import android.content.Intent
import android.os.Bundle
import com.apkfuns.logutils.LogUtils
import com.face.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.face.util.FirebaseMessageUtil
import com.face.util.GVM
import com.face.util.NotificationUtil
import com.face.util.SPUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.toIntOrNullX
import org.json.JSONObject

class MessagingService : FirebaseMessagingService() {

    companion object {
        // 退出登录
        const val MSG_TYPE_LOGIN_EXPIRE = 1

        // 素材完成
        const val MSG_TYPE_NEW_TASK = 100

        // 签到
        const val MSG_TYPE_POINT = 201

        // 反馈回复
        const val MSG_TYPE_FEEDBACK = 301

        // 素材推送
        const val MSG_TYPE_HOT = 101
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseMessageUtil.saveToken(token)
        LogUtils.i("onNewToken token = $token")
    }

    /**
     * @param intent Intent
     * task_id=94008726-5fa2-40db-bcf9-a8dd9159b67d
     * create_time=1698198772738
     * gcm.notification.title=Face-swapped image task is complete
     * desc=Task from {create_time} is finished.
     */
    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        LogUtils.i(">>>>handleIntent = ${intent?.extras}")
        intent?.extras?.let {
            if (it.containsKey("msg_type")) {
                handleMsg(it)
            }
        }
    }

    /**
     * 前台
     * https://firebase.google.com/docs/cloud-messaging/android/receive?hl=zh-cn
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        LogUtils.i(
            ">>>>onMessageReceived = ${message.notification}" +
                    "\ndata = ${message.data}"
        )
//        if (GVM.INSTANT.isForeground.value == true) {
//            message.notification?.let {
//                when (message.data["msg_type"].toIntOrNullX() ?: 201) {
//                    MSG_TYPE_NEW_TASK -> {
//                        var timeLong = System.currentTimeMillis()
//                        val timeMsg = message.data["create_time"]
//                        if (timeMsg != null) {
//                            timeLong = timeMsg.toLong()
//                        }
//                        val time: String =
//                            TimeUtil.getFormatDate(timeLong, "MMMM dd,yyyy")!!
//
//                        SPUtils.notificationMsg = true
//                        NotificationUtil.showTaskNotification(
//                            message.data["task_id"],
//                            message.data["title"],
//                            message.data["desc"]?.replace("{create_time}", time),
//                            MainActivity::class.java
//                        )
//                    }
//
//                    MSG_TYPE_HOT -> {
//                        NotificationUtil.showHotNotification(
//                            message.data["media_id"],
//                            message.data["title"],
//                            message.data["desc"],
//                            message.data["img"],
//                            MainActivity::class.java
//                        )
//                    }
//
//                    MSG_TYPE_POINT -> {
//                        NotificationUtil.showCheckInNotification(
//                            message.data["title"],
//                            message.data["content"],
//                            WelcomeActivity::class.java
//                        )
//                    }
//                }
//            }
//        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        LogUtils.i("onDeletedMessages")
    }

    private fun handleMsg(bundle: Bundle) {
        when (bundle.getString("msg_type").toIntOrNullX() ?: bundle.getInt("msg_type", 201)) {
            MSG_TYPE_LOGIN_EXPIRE -> {
                GVM.INSTANT.refreshUserInfo()
            }

            MSG_TYPE_NEW_TASK -> {

                var timeLong = System.currentTimeMillis()
                val timeMsg = bundle.getString("create_time")
                if (timeMsg != null) {
                    timeLong = timeMsg.toLong()
                }
                val time: String =
                    TimeUtil.getFormatDate(timeLong, "MMMM dd,yyyy")!!

                val taskId = bundle.getString("task_id") ?: "---"
                if (!SPUtils.fishTask.contains(taskId)) {
                    SPUtils.notificationMsg = true
                    NotificationUtil.showTaskNotification(
                        bundle.getString("task_id"),
                        bundle.getString("title"),
                        bundle.getString("desc")?.replace("{create_time}", time),
                        WelcomeActivity::class.java
                    )
                }
            }

            MSG_TYPE_HOT -> {
                NotificationUtil.showHotNotification(
                    bundle.getString("media_id"),
                    bundle.getString("title"),
                    bundle.getString("desc"),
                    bundle.getString("img"),
                    bundle.getString("info_type"),
                    WelcomeActivity::class.java
                )
            }

            MSG_TYPE_POINT -> {
                NotificationUtil.showCheckInNotification(
                    bundle.getString("title"),
                    bundle.getString("content"),
                    WelcomeActivity::class.java
                )
            }

            MSG_TYPE_FEEDBACK -> {
                val desc = bundle.getString("desc")?:""
                val contentValue = JSONObject(desc).getString("content")
                NotificationUtil.showFeedBackNotification(
                    bundle.getString("title"),
                    contentValue,
                    bundle.getString("feedback_id"),
                    WelcomeActivity::class.java
                )
            }
        }
    }

}