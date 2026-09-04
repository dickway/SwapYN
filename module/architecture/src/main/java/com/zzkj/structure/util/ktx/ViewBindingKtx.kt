package com.zzkj.structure.util.ktx

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding


inline fun <reified T : ViewDataBinding> inflateDataBinding(
    inflater: LayoutInflater,
    root: ViewGroup? = null,
    attachToParent: Boolean = false
): T {
    return T::class.java.getDeclaredMethod(
        "inflate",
        ViewGroup::class.java,
        Boolean::class.java
    ).invoke(null, inflater, root, attachToParent) as T
}