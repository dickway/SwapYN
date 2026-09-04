package com.zzkj.structure.util

import androidx.lifecycle.MediatorLiveData

/**
 * @author
 * @date 2022/11/8
 * @description
 */
class NotNullMediatorLiveData<T>(value: T) : MediatorLiveData<T>() {

    init {
        setValue(value)
    }

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