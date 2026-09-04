package com.zzkj.structure.lifecycle

import android.app.Activity
import android.os.Bundle
import com.apkfuns.logutils.LogUtils
import com.zzkj.structure.util.AppManager

/**
 * @author 再战科技
 * @date 2022/1/26
 * @description
 */
open class DefaultActivityLifecycle : ActivityLifecycleEmptyImp() {
    private val TAG = "ActivityLifecycle"

    private val enablePrintLifecycle = false

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG).v("onActivityCreated:${activity.javaClass.simpleName}")
        }
        AppManager.addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG).v("onActivityStarted:${activity.javaClass.simpleName}")
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG).v("onActivityResumed:${activity.javaClass.simpleName}")
        }
        AppManager.setCurActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG).v("onActivityPaused:${activity.javaClass.simpleName}")
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG).v("onActivityStopped:${activity.javaClass.simpleName}")
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG)
                .v("onActivitySaveInstanceState:${activity.javaClass.simpleName}\nbundle = $outState")
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (enablePrintLifecycle) {
            LogUtils.tag(TAG).v("onActivityDestroyed:${activity.javaClass.simpleName}")
        }
        AppManager.removeActivity(activity)
    }
}