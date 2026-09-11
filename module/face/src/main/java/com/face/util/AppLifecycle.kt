package com.face.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.LogUtils

/**
 * @author 再战科技
 * @date 2022/1/26
 * @description
 */
class AppLifecycle : DefaultLifecycleObserver {
    private val TAG = javaClass.simpleName

    override fun onStart(owner: LifecycleOwner) {
        LogUtils.dTag(TAG, "App start")
        GVM.INSTANT.isForeground.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        LogUtils.dTag(TAG, "App stop")
        GVM.INSTANT.isForeground.value = false
    }
}
