package com.zzkj.structure.util.ktx

import androidx.lifecycle.MutableLiveData

/**
 * @author lmk
 * @date 2022/3/3
 * @description
 */
fun <T> MutableLiveData<T>.setIfNot(v: T?): Boolean {
    if (value != v) {
        value = v
        return true
    }
    return false
}

fun <T> MutableLiveData<T>.postIfNot(v: T?): Boolean {
    if (value != v) {
        postValue(v)
        return true
    }
    return false
}