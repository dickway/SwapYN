package com.zzkj.structure.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author 再战科技
 * @date 2022/1/26
 * @description
 */
open class ActivityLifecycleEmptyImp : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}