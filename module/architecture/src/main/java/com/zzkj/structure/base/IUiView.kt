package com.zzkj.structure.base

import androidx.lifecycle.LifecycleOwner

/**
 * @author lmk
 * @date 2022/1/26
 * @description
 */

interface IUiView : LifecycleOwner {

    // 通用的方法, 有特殊需求时可以用下
    fun generalFun(argus: Map<String, Any>? = null) {

    }

    fun showLoading(msg: String?, cancelable: Boolean)

    fun showLoading(msg: String?) {
        showLoading(msg, true)
    }

    fun showLoading(cancelable: Boolean) {
        showLoading(null, cancelable)
    }

    fun showLoading() {
        showLoading(null, true)
    }

    fun dismissLoading()

    fun isShowLoading(): Boolean
}