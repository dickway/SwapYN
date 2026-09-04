package com.zzkj.structure.util

import android.os.Process
import com.apkfuns.logutils.LogUtils
import kotlin.system.exitProcess

object CrashHandler : Thread.UncaughtExceptionHandler {
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    fun init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, t: Throwable) {
        t.printStackTrace()
        LogUtils.e(t)
        //日志立即写入文件
        LogUtils.getLog2FileConfig().flushAsync()
        if (mDefaultHandler != null) {
            mDefaultHandler!!.uncaughtException(thread, t)
        } else {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }
    }
}