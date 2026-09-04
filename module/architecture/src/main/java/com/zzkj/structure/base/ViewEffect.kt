package com.zzkj.structure.base

import com.zzkj.structure.R
import com.zzkj.structure.util.ktx.getStringX

/**
 * @author
 * @date 2022/1/26
 * @description
 */
sealed class ViewEffect {
    data class ShowLoading(
        val msg: String = getStringX(R.string.loading),
        val cancelable: Boolean = true
    ) : ViewEffect()

    data class CustomEffect @JvmOverloads constructor(
        val type: String? = null,
        val args: Map<String, Any>? = null
    ) : ViewEffect() {
        constructor(type: String? = null, vararg args: Pair<String, Any>) : this(type, args.toMap())
    }

    object HideLoading : ViewEffect()

    object NoneEffect : ViewEffect()

    class SimpleCommonEffect(val type: Int) : ViewEffect()
}
