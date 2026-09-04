package com.zzkj.structure.ui

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout

class InsetsDrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DrawerLayout(context, attrs, defStyleAttr) {

    init {
        // 不要消费 WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(this, null)
    }
}