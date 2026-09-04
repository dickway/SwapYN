package com.zzkj.structure.util.ktx

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.IntDef
import androidx.annotation.Px
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.zzkj.structure.R
import com.zzkj.structure.base.BaseApp
import kotlin.math.max

/**
 * @author lmk
 * @date 2022/3/31
 * @description
 */
fun statusBarHeight(view: View, ignoreVisible: Boolean = true): Int {
    val insets = ViewCompat.getRootWindowInsets(view) ?: return 0
    if (!ignoreVisible && !insets.isVisible(WindowInsetsCompat.Type.statusBars())) return 0
    return insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
}

fun navigationBarHeight(view: View, ignoreVisible: Boolean = true): Int {
    val insets = ViewCompat.getRootWindowInsets(view) ?: return 0
    if (!ignoreVisible && !insets.isVisible(WindowInsetsCompat.Type.navigationBars())) return 0
    return insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
}

fun View.applyInserts(
    addHorizonPadding: Boolean = false,
    addVerticalPadding: Boolean = false,
    addHorizonMargin: Boolean = false,
    addVerticalMargin: Boolean = false,
    addHorizonWidth: Boolean = false,
    addVerticalHeight: Boolean = false,
    addStartPadding: Boolean = addHorizonPadding,
    addTopPadding: Boolean = addVerticalPadding,
    addEndPadding: Boolean = addHorizonPadding,
    addBottomPadding: Boolean = addVerticalPadding,
    addStartMargin: Boolean = addHorizonMargin,
    addTopMargin: Boolean = addVerticalMargin,
    addEndMargin: Boolean = addHorizonMargin,
    addBottomMargin: Boolean = addVerticalMargin,
    addStartWidth: Boolean = addHorizonWidth,
    addTopHeight: Boolean = addVerticalHeight,
    addEndWidth: Boolean = addHorizonWidth,
    addBottomHeight: Boolean = addVerticalHeight,
    autoScrollTopIfRecyclerViewUpdate: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        var isUpdated = false
        val isLTR = view.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR
        val nowState = ViewState(view)
        val initialState = (view.getTag(R.id.view_initial_state) as? ViewState) ?: nowState.also {
            view.setTag(R.id.view_initial_state, it)
        }
        val originPadding = initialState.paddings
        val originMargin = initialState.margins
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val top = max(systemBars.top, insets.displayCutout?.safeInsetTop ?: 0)
        val bottom = max(systemBars.bottom, insets.displayCutout?.safeInsetBottom ?: 0)
        val left = max(systemBars.left, insets.displayCutout?.safeInsetLeft ?: 0)
        val right = max(systemBars.right, insets.displayCutout?.safeInsetRight ?: 0)
        val start = if (isLTR) left else right
        val end = if (isLTR) right else left
        val newPadding = ViewDimensions(
            start = originPadding.start + (if (addStartPadding) start else 0),
            top = originPadding.top + (if (addTopPadding) top else 0),
            end = originPadding.end + (if (addEndPadding) end else 0),
            bottom = originPadding.bottom + (if (addBottomPadding) bottom else 0)
        )
        if (newPadding != nowState.paddings) {
            view.updatePaddingRelative(
                start = newPadding.start,
                top = newPadding.top,
                end = newPadding.end,
                bottom = newPadding.bottom
            )
            isUpdated = true
        }
        val newMargin = ViewDimensions(
            start = originMargin.start + (if (addStartMargin) start else 0),
            top = originMargin.top + (if (addTopMargin) top else 0),
            end = originMargin.end + (if (addEndMargin) end else 0),
            bottom = originMargin.bottom + (if (addBottomMargin) bottom else 0)
        )

        val newHeight = (initialState.height
                + (if (addTopHeight) top else 0)
                + (if (addBottomHeight) bottom else 0))
        val newWidth = (initialState.width
                + (if (addStartWidth) start else 0)
                + (if (addEndWidth) end else 0))
        val needUpdateWidth = (addStartWidth || addEndWidth) && view.layoutParams.width > 0
        val needUpdateHeight = (addTopHeight || addBottomHeight) && view.layoutParams.height > 0
        if ((newMargin != nowState.margins && view.layoutParams is ViewGroup.MarginLayoutParams)
            || (newWidth != nowState.width && needUpdateWidth)
            || (newHeight != nowState.height && needUpdateHeight)
        ) {
            view.updateLayoutParams<ViewGroup.LayoutParams> {
                if (needUpdateWidth) {
                    width = newWidth
                }
                if (needUpdateHeight) {
                    height = newHeight
                }
                if (this is ViewGroup.MarginLayoutParams && newMargin != nowState.margins) {
                    updateMarginsRelative(
                        start = newMargin.start,
                        top = newMargin.top,
                        end = newMargin.end,
                        bottom = newMargin.bottom
                    )
                }
            }
            isUpdated = true
        }
        if (autoScrollTopIfRecyclerViewUpdate && isUpdated && view is RecyclerView) {
            view.scrollToPosition(0)
        }
        insets
    }
    if (this.isLaidOut) {
        ViewCompat.requestApplyInsets(this)
    }
}

fun View.clearInsets() {
    (getTag(R.id.view_initial_state) as? ViewState)?.let {
        setPaddingRelative(
            it.paddings.start,
            it.paddings.top,
            it.paddings.end,
            it.paddings.bottom
        )
        updateLayoutParams<ViewGroup.LayoutParams> {
            height = it.height
            width = it.width
            if (this is ViewGroup.MarginLayoutParams) {
                marginStart = it.margins.start
                topMargin = it.margins.top
                marginEnd = it.margins.end
                bottomMargin = it.margins.bottom
            }
        }
    }
}

fun FragmentActivity.setLightBars(
    isLightStatus: Boolean? = null,
    isLightNavigation: Boolean? = null
) {
    window?.setLightBars(isLightStatus, isLightNavigation)
}

fun Fragment.setLightBars(
    isLightStatus: Boolean? = null,
    isLightNavigation: Boolean? = null
) {
    activity?.window?.setLightBars(isLightStatus, isLightNavigation)
}

fun Window.setLightBars(
    isLightStatus: Boolean? = null,
    isLightNavigation: Boolean? = null
) {
    WindowCompat.getInsetsController(this, decorView).apply {
        if (isLightStatus != null) {
            isAppearanceLightStatusBars = isLightStatus
        }
        if (isLightNavigation != null) {
            isAppearanceLightNavigationBars = isLightNavigation
        }
    }
}

fun FragmentActivity.setStatusBarColor(colorInt: Int? = null, colorResId: Int? = null) {
    window?.setStatusBarColor(colorInt, colorResId)
}

fun Fragment.setStatusBarColor(colorInt: Int? = null, colorResId: Int? = null) {
    activity?.window?.setStatusBarColor(colorInt, colorResId)
}

fun FragmentActivity.setNavigationBarColor(colorInt: Int? = null, colorResId: Int? = null) {
    window?.setNavigationBarColor(colorInt, colorResId)
}

fun Fragment.setNavigationBarColor(colorInt: Int? = null, colorResId: Int? = null) {
    activity?.window?.setNavigationBarColor(colorInt, colorResId)
}

fun Window.setStatusBarColor(colorInt: Int? = null, colorResId: Int? = null) {
    (colorInt ?: colorResId?.let { getColorX(it) })?.let { statusBarColor = it }
}

fun Window.setNavigationBarColor(colorInt: Int? = null, colorResId: Int? = null) {
    (colorInt ?: colorResId?.let { getColorX(it) })?.let { navigationBarColor = it }
}


private val View.paddingDimensions: ViewDimensions
    get() = ViewDimensions(paddingStart, paddingTop, paddingEnd, paddingBottom)

private val View.marginDimensions: ViewDimensions
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
        ViewDimensions(lp.marginStart, lp.topMargin, lp.marginEnd, lp.bottomMargin)
    } ?: ViewDimensions.EMPTY


data class ViewState(
    val paddings: ViewDimensions = ViewDimensions.EMPTY,
    val margins: ViewDimensions = ViewDimensions.EMPTY,
    @Px val width: Int = 0,
    @Px val height: Int = 0,
) {
    constructor(view: View) : this(
        paddings = view.paddingDimensions,
        margins = view.marginDimensions,
        width = view.layoutParams.width.takeIf { it >= 0 } ?: view.width,
        height = view.layoutParams.height.takeIf { it >= 0 } ?: view.height
    )
}

data class ViewDimensions(
    @Px val start: Int,
    @Px val top: Int,
    @Px val end: Int,
    @Px val bottom: Int
) {
    companion object {
        val EMPTY = ViewDimensions(0, 0, 0, 0)
    }
}

object BarHelper {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        value = [
            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT,
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ]
    )
    internal annotation class Behavior

    fun Activity.bar(builder: BarBuilder.() -> Unit) {
        BarBuilder(window).apply(builder)
    }

    fun Fragment.bar(builder: BarBuilder.() -> Unit) {
        BarBuilder(requireActivity().window).apply(builder)
    }

    @JvmStatic
    fun start(activity: Activity) = BarBuilder(activity.window)

    @JvmStatic
    fun start(fragment: Fragment) = BarBuilder(fragment.requireActivity().window)

    class BarBuilder(private val window: Window) {

        private val controller by lazy {
            WindowCompat.getInsetsController(window, window.decorView)
        }

        fun transparent() = apply {
            fitsSystemWindows(false)
            statusBarColorInt(Color.TRANSPARENT)
            navigationBarColorInt(Color.TRANSPARENT)
        }

        fun light(light: Boolean) = apply {
            lightStatusBar(light)
            lightNavigationBar(light)
        }

        fun isLightStatusBar() = controller.isAppearanceLightStatusBars

        fun isLightNavigationBar() = controller.isAppearanceLightNavigationBars

        fun fitsSystemWindows(fits: Boolean) = apply {
            WindowCompat.setDecorFitsSystemWindows(window, fits)
        }

        fun barColorResId(@ColorRes resId: Int) = ContextCompat.getColor(
            BaseApp.INSTANCE, resId
        ).let {
            statusBarColorInt(it)
            navigationBarColorInt(it)
        }

        fun barColorInt(@ColorInt color: Int) = apply {
            statusBarColorInt(color)
            navigationBarColorInt(color)
        }

        fun statusBarColorResId(@ColorRes resId: Int) = statusBarColorInt(
            ContextCompat.getColor(BaseApp.INSTANCE, resId)
        )

        fun statusBarColorInt(@ColorInt color: Int) = apply {
            window.statusBarColor = color
        }

        fun navigationBarColorResId(@ColorRes resId: Int) = navigationBarColorInt(
            ContextCompat.getColor(BaseApp.INSTANCE, resId)
        )

        fun navigationBarColorInt(@ColorInt color: Int) = apply {
            window.navigationBarColor = color
        }

        fun lightStatusBar(light: Boolean) = apply {
            controller.isAppearanceLightStatusBars = light
        }

        fun lightNavigationBar(light: Boolean) = apply {
            controller.isAppearanceLightNavigationBars = light
        }

        fun showSystemBar(show: Boolean) = apply {
            if (show) {
                controller.show(WindowInsetsCompat.Type.systemBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.systemBars())
            }
        }

        fun showStatusBar(show: Boolean) = apply {
            if (show) {
                controller.show(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.statusBars())
            }
        }

        fun showNavigationBar(show: Boolean) = apply {
            if (show) {
                controller.show(WindowInsetsCompat.Type.navigationBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.navigationBars())
            }
        }

        fun setSystemBarsBehavior(@Behavior behavior: Int) = apply {
            controller.systemBarsBehavior = behavior
        }
    }
}
