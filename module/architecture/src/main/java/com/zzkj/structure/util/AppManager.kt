package com.zzkj.structure.util

import android.app.Activity
import com.blankj.utilcode.util.LogUtils
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

object AppManager {
    private val activities = mutableListOf<Activity>()
    private var curActivityRef: WeakReference<Activity>? = null

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * @param c finish第一个该Activity
     */
    fun finishSingleActivity(c: Class<out Activity?>) {
        for (a in activities) {
            if (a.javaClass == c) {
                a.finish()
                activities.remove(a)
                return
            }
        }
    }

    /**
     * @param finish所有active
     */
    fun finishActivity(c: Class<out Activity?>) {
        for (a in activities) {
            if (a.javaClass != c) {
                a.finish()
                activities.remove(a)
                return
            }
        }
    }

    val allActivities: List<Activity>
        get() = activities

    fun getCurActivity(): Activity? {
        return curActivityRef?.get()
    }

    fun setCurActivity(activity: Activity) {
        curActivityRef?.clear()
        curActivityRef = WeakReference(activity)
    }

    val topActivity: Activity?
        get() = activities.lastOrNull()

    fun activityIsLive(c: Class<out Activity?>): Boolean {
        for (a in activities) {
            if (a.javaClass == c) {
                return true
            }
        }
        return false
    }

    fun exitApp() {
        for (activity in activities) {
            activity.finish()
        }
        exitProcess(0)
    }

//    /**
//     * 当本应用位于后台时，则将它切换到最前端
//     */
//    fun setApp2Top() {
//        val activityManager =
//            BaseApp.INSTANCE.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val taskInfoList = activityManager.getRunningTasks(100)
//        for (taskInfo in taskInfoList) {
//            if (taskInfo.topActivity!!.packageName == BaseApp.INSTANCE.packageName) {
//                activityManager.moveTaskToFront(taskInfo.id, 0)
//                break
//            }
//        }
//    }
}