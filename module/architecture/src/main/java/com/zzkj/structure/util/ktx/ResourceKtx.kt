package com.zzkj.structure.util.ktx

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.zzkj.structure.base.BaseApp.Companion.INSTANCE as app


/**
 * @author
 * @date 2022/1/26
 * @description
 */
fun getStringX(@StringRes resId: Int, vararg formatArgs: Any?): String {
    return app.resources.getString(resId, *formatArgs)
}
@ColorInt
fun getColorAttr(@AttrRes resId: Int): Int {
    val attrs = intArrayOf(resId)
    val typedArray: TypedArray = app.obtainStyledAttributes(attrs)
    val color = typedArray.getColor(0, -0x50506)
    typedArray.recycle()
    return color
}
fun getColorX(@ColorRes resId: Int) = ContextCompat.getColor(app, resId)

fun getDrawableX(@DrawableRes resId: Int) = ContextCompat.getDrawable(app, resId)

fun getDimensionPixelSizeX(@DimenRes resId: Int) = app.resources.getDimensionPixelSize(resId)

fun getDimensionX(@DimenRes resId: Int) = app.resources.getDimension(resId)


fun getScreenWidth() = app.resources.displayMetrics.widthPixels

// 不含状态栏和导航栏  WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(app).bounds.height()
fun getScreenHeight() = app.resources.displayMetrics.heightPixels

fun isInstall(packageName: String) = try {
    app.packageManager.getPackageInfo(
        packageName,
        PackageManager.GET_GIDS
    )
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}


//dp2px
val Int.dp: Int
    get() = (0.5f + this * app.resources.displayMetrics.density).toInt()

val Float.dp: Int
    get() = (0.5f + this * app.resources.displayMetrics.density).toInt()

//sp2px
val Int.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(), app.resources.displayMetrics
    ).toInt()

val Float.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this, app.resources.displayMetrics
    ).toInt()

fun Int.px2dp() = this / app.resources.displayMetrics.density

fun Int.px2sp() = this / app.resources.displayMetrics.scaledDensity
