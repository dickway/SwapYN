package com.zzkj.structure.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.apkfuns.logutils.LogUtils

/**
 * @author lmk
 * @date 2022/1/26
 * @description
 */
open class DefaultAppLifecycle : DefaultLifecycleObserver {
    private val TAG = "AppLifecycle"
    override fun onStart(owner: LifecycleOwner) {
        LogUtils.tag(TAG).v("App start")
    }

    override fun onStop(owner: LifecycleOwner) {
        LogUtils.tag(TAG).v("App stop")
    }
}