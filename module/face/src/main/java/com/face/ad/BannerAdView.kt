package com.face.ad

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.applovin.mediation.ads.MaxAdView
import com.face.R
import com.face.util.GVM
import com.zzkj.structure.util.ktx.dp

/**
 * @author 再战科技
 * @date 2022/4/13
 * @description banner广告
 */
class BannerAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var currAdView: MaxAdView? = null

    init {
        setBackgroundResource(R.color.colorTransparent)
    }

    private fun takeFromServerOrCache() {
        if (!GVM.INSTANT.enableAdAndNotVip.value) return
        if (currAdView != null) {
            addAdToView()
            return
        }
        currAdView = AdUtil.getBannerAd()
        if (currAdView != null) {
            addAdToView()
        }
    }

    private fun addAdToView() {
        if (currAdView?.parent == this) return
        currAdView?.let {
            val viewGroup = it.parent as? ViewGroup
            if (viewGroup != null && viewGroup != this) {
                viewGroup.removeAllViews()
            }
            removeAllViews()
            addView(
                it, LayoutParams(
                    WRAP_CONTENT,
                    // 屏幕越宽广告越高
                    50.dp,
                    Gravity.CENTER
                )
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) return
        takeFromServerOrCache()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeAllViews()
//        currAdView?.destroy()
//        currAdView = null
    }
}