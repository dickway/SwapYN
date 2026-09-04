package com.face.util

import androidx.lifecycle.Observer
import com.apkfuns.logutils.LogUtils
import com.zzkj.structure.util.ktx.md5
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author 再战科技
 * @date 2023/11/13
 * @description 上报在线时长
 */
object UpdateLiveTImeUtil {

    private var eventId: String = ""
    private var openTime: Long = 0
    private var liveTime: Long = 0
    private var inBackgroundTime: Long = 0

    fun init() {
        try {
            eventId = (SPUtils.deviceId + System.currentTimeMillis()).md5()
            GVM.INSTANT.isForeground.observeForever(observer)
            GlobalScope.launch {
                delay(3 * 60_000)
                if (GVM.INSTANT.isForeground.value == true && openTime != 0L) {
                    EventUtil.liveTime(
                        eventId,
                        (liveTime + System.currentTimeMillis() - openTime).toString()
                    )
                }
            }
        } catch (_: Exception) {
        }
    }

    fun destroy() {
        GVM.INSTANT.isForeground.removeObserver(observer)
    }

    private val observer = Observer<Boolean> {
        if (it) {
            onStart()
        } else {
            onStop()
        }
    }

    private fun onStart() {
        val liveInfo = SPUtils.liveTime
        if (liveInfo.isNotBlank()) {
            val (id, time) = liveInfo.split(",")
            EventUtil.liveTime(id, time) {
                if (it && SPUtils.liveTime.startsWith(eventId)) {
                    SPUtils.liveTime = ""
                }
            }
        }
        openTime = System.currentTimeMillis()
        // 后台超过10分钟重新计时
        if (inBackgroundTime > 0 && System.currentTimeMillis() - inBackgroundTime > 10 * 60_000) {
            eventId = (SPUtils.deviceId + System.currentTimeMillis()).md5()
            inBackgroundTime = 0
            liveTime = 0
        }
    }

    private fun onStop() {
        inBackgroundTime = System.currentTimeMillis()
        if (openTime == 0L) {
            return
        }
        liveTime += (inBackgroundTime - openTime)
        LogUtils.d("liveTime = ${liveTime / 1000}")
        SPUtils.liveTime = "$eventId,$liveTime"
        val id = eventId
        EventUtil.liveTime(id, liveTime.toString()) {
            if (it && SPUtils.liveTime.startsWith(id)) {
                SPUtils.liveTime = ""
            }
        }
    }
}