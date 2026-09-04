package com.face.util

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import coil.request.ImageRequest
import com.apkfuns.logutils.LogUtils
import com.face.bean.NotificationBean
import com.face.ui.App
import com.zzkj.structure.util.ImgLoader
import com.zzkj.structure.util.ktx.toIntOrZero
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author 再战科技
 * @date 2023/10/25
 * @description
 */
object NotificationUtil {

    private const val CHANNEL_ID = "100"

    const val SIGN_ID = 10086//签到提醒
    const val NEW_ID = 10087//热门素材提醒
    const val HOT_ID = 10088//新素材提醒

    private var notifyId = (System.currentTimeMillis() / 1000).toString().drop(4).toInt()

    private val manager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(App.INSTANCE)
    }

    fun createChannel(activity: Activity? = null) {
        if (activity != null) checkPermission(activity)
        manager.createNotificationChannel(
            NotificationChannelCompat
                .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName("Default")
                .setShowBadge(true)
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .build()
        )
    }

    fun deleteNotification(num: Int) {
        manager.cancel(num) // 取消 ID 为 1001 的通知
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun showNotification(
        title: String?,
        content: String?,
        imgUrl: String? = null,
        intent: PendingIntent? = null,
        type: Int = 0
    ) {
        if (!checkPermission(null)) return
        val builder = NotificationCompat.Builder(App.INSTANCE, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSmallIcon(com.key.R.drawable.img_notification)
        intent?.let {
            builder.setContentIntent(intent)
        }


        if (!imgUrl.isNullOrBlank()) {
            GlobalScope.launch {
                ImgLoader.imageLoader.execute(
                    ImageRequest.Builder(App.INSTANCE)
                        .data(imgUrl)
                        .build()
                ).drawable?.toBitmap()?.let {
                    builder.setLargeIcon(it)
                        .setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(it)
                        )
                }
                manager.notify(type, builder.build())
            }
        } else {
            manager.notify(type, builder.build())
        }
    }

    // 任务完成
    fun showTaskNotification(
        taskId: String?,
        title: String?,
        content: String?,
        cls: Class<*>? = null
    ) {
        if (!GVM.INSTANT.isLogin() || cls == null) {
            return
        }
        EventUtil.pushShow(taskId)
        val intent = Intent(App.INSTANCE, cls).apply {
            putExtra("task_id", taskId)
            putExtra("msg_type", 100)//MSG_TYPE_NEW_TASK
            data = Uri.parse("custom://notify/$taskId")
        }
        val notigyIdNum = ++notifyId
        val pendingIntent = PendingIntent.getActivity(
            App.INSTANCE, notigyIdNum,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        SPUtils.notificationList += NotificationBean(notigyIdNum, taskId)
        showNotification(title, content, null, pendingIntent, notigyIdNum)
    }

    // 签到提醒
    fun showCheckInNotification(title: String?, content: String?, cls: Class<*>? = null) {
        EventUtil.pushNotification(title, content)
        if (!GVM.INSTANT.isLogin() || GVM.INSTANT.isTodayChecked.value || cls == null) {
            return
        }
        val intent = Intent(App.INSTANCE, cls).apply {
            putExtra("msg_type", 201)
            data = Uri.parse(
                "custom://notify/${
                    (System.currentTimeMillis() / 1000).toString().drop(4).toInt()
                }"
            )
        }
        val pendingIntent = PendingIntent.getActivity(
            App.INSTANCE, SIGN_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        showNotification(title, content, null, pendingIntent, SIGN_ID)
    }

    // 反馈推送
    fun showFeedBackNotification(
        title: String?,
        content: String?,
        feedbackId: String?,
        cls: Class<*>? = null
    ) {
        EventUtil.pushNotification(title, content)
        if (!GVM.INSTANT.isLogin() || cls == null) {
            return
        }
        val notigyIdNum = feedbackId.toIntOrZero()
        val intent = Intent(App.INSTANCE, cls).apply {
            putExtra("msg_type", 301)
            putExtra("feedback_id", feedbackId)
        }
        val pendingIntent = PendingIntent.getActivity(
            App.INSTANCE, notigyIdNum,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        showNotification(title, content, null, pendingIntent, notigyIdNum)
    }


    // 热门素材
    fun showHotNotification(
        mediaId: String?,
        title: String?,
        content: String?,
        url: String?,
        type: String?,
        cls: Class<*>? = null
    ) {
//        if (!GVM.INSTANT.isLogin() || cls == null) {
//            return
//        }
        EventUtil.pushHot(mediaId)
        val intent = Intent(App.INSTANCE, cls).apply {
            putExtra("media_id", mediaId)
            putExtra("msg_type", 101)
            data = Uri.parse("custom://notify/$mediaId")
        }
        val notigyIdNum = if (type != "new") HOT_ID else NEW_ID
        val pendingIntent = PendingIntent.getActivity(
            App.INSTANCE, notigyIdNum,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        showNotification(title, content, url, pendingIntent, notigyIdNum)
    }


    fun checkPermission(activity: Activity?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        if (ActivityCompat.checkSelfPermission(
                App.INSTANCE,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (activity != null) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
            return false
        }
        return true
    }
}