package com.face.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.LogUtils
import com.face.ui.App


object GoogleToPlay {


    fun launchGooglePlay(context: Activity) {
        SPUtils.notComment = true
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${App.INSTANCE.packageName}")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }
}