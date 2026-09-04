package com.face.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationUtil {

    /**
     * 轻震一下手机，默认震动 5 毫秒
     * @param context 上下文，建议使用 ApplicationContext 避免泄漏
     * @param duration 震动时长（毫秒），默认 5ms
     */
    fun vibrateOnce(context: Context?, duration: Long = 3L) {
        if (context==null){
            return
        }
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}

private fun Context.getSystemService(name: Any) {}
