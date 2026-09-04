package com.zzkj.structure.util

import androidx.lifecycle.MutableLiveData

/**
 * @author
 * @date 2022/11/1
 * @description
 */
class NotNullMutableLiveData<T>(value: T) : MutableLiveData<T>(value) {

    override fun postValue(value: T) {
        super.postValue(value)
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

    override fun getValue(): T {
        return super.getValue()!!
    }
}