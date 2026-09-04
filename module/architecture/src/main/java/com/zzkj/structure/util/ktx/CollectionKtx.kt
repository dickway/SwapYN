package com.zzkj.structure.util.ktx

/**
 * @author lmk
 * @date 2022/3/4
 * @description
 */
fun <T> MutableList<T>?.addIfNotContains(data: T) {
    if (this == null) {
        return
    }
    if (!contains(data)) {
        add(data)
    }
}
fun <T> Collection<T>?.size() = this?.size ?: 0