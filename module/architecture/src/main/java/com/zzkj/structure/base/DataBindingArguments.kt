package com.zzkj.structure.base

/**
 * @author lmk
 * @date 2021/12/30
 * @description
 */
class DataBindingArguments() {
    val map = mutableMapOf<Int, Any?>()

    constructor(brId: Int, value: Any?) : this() {
        map[brId] = value
    }

    fun addArgument(brId: Int, value: Any?) = apply {
        map[brId] = value
    }
}