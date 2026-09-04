package com.zzkj.structure.util

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hjq.toast.Toaster
import com.zzkj.structure.BuildConfig
import com.zzkj.structure.base.BaseApp

private val handler = Handler(Looper.getMainLooper())
private val SINGLE_TOAST_LOCK = Any()
private var singleToast: Toast? = null
fun toastIfDebug(text: String) {
    if (BuildConfig.DEBUG) {
        toast(text)
    }
}

fun toast(text: String?) {
    toast(text, Toast.LENGTH_SHORT)
}

fun toast(stringRes: Int) {
    toast(stringRes, Toast.LENGTH_SHORT)
}

fun longToast(text: String?) {
    toast(text, Toast.LENGTH_LONG)
}

fun longToast(stringRes: Int) {
    toast(stringRes, Toast.LENGTH_LONG)
}

fun toast(stringRes: Int, duration: Int) {
    toast(BaseApp.INSTANCE.getString(stringRes), duration)
}

fun toast(text: String?, duration: Int) {
    if (!text.isNullOrBlank()) {
        if (duration == Toast.LENGTH_LONG) {
            Toaster.showLong(text)
        } else {
            Toaster.show(text)
        }
    }
}


fun singleToast(text: String?) {
    singleToast(text, Toast.LENGTH_SHORT)
}

fun singleLongToast(text: String?) {
    singleToast(text, Toast.LENGTH_LONG)
}

fun singleToast(stringRes: Int) {
    singleToast(stringRes, Toast.LENGTH_SHORT)
}

fun singleLongToast(stringRes: Int) {
    singleToast(stringRes, Toast.LENGTH_LONG)
}

fun singleToast(stringRes: Int, duration: Int) {
    singleToast(BaseApp.INSTANCE.getString(stringRes), duration)
}

/**
 * 避免一下显示很多
 *
 * @param text
 * @param duration
 * @return
 */
fun singleToast(text: String?, duration: Int) {
    if (!text.isNullOrBlank()) {
        if (duration == Toast.LENGTH_LONG) {
            Toaster.showLong(text)
        } else {
            Toaster.show(text)
        }
    }
}