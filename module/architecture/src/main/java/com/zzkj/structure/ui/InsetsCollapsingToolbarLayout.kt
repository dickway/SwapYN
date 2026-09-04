package com.zzkj.structure.ui

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.CollapsingToolbarLayout

class InsetsCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyle) {

    init {
        // 不要消费 WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(this, null)
    }

}