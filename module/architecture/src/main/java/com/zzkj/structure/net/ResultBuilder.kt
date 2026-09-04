package com.zzkj.structure.net

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zzkj.structure.util.singleToast

typealias ResponseLiveData<T> = LiveData<ApiResponse<T>>
typealias ResponseMutableLiveData<T> = MutableLiveData<ApiResponse<T>>

@MainThread
fun <T> ResponseMutableLiveData<T>.observeResult(
    owner: LifecycleOwner,
    listenerBuilder: ResultBuilder<T>.() -> Unit,
) {
    observe(owner) { apiResponse -> apiResponse.parseData(listenerBuilder) }
}

@MainThread
fun <T> LiveData<ApiResponse<T>>.observeResult(
    owner: LifecycleOwner,
    listenerBuilder: ResultBuilder<T>.() -> Unit,
) {
    observe(owner) { apiResponse -> apiResponse.parseData(listenerBuilder) }
}

fun <T> ApiResponse<T>?.parseData(listenerBuilder: ResultBuilder<T>.() -> Unit) {
    if (this == null) {
        return
    }
    val listener = ResultBuilder<T>().also(listenerBuilder)
    when (_mState) {
        API_STATE_START -> listener.onStart()
        API_STATE_SUCCESS -> listener.onSuccess(mData)
        API_STATE_FAIL -> listener.onFailed(mError, mCode, mMsg)
        API_STATE_COMPLETE -> listener.onComplete()
    }
}

class ResultBuilder<T> {
    var onStart: () -> Unit = {}
    var onSuccess: (data: T?) -> Unit = {}
    var onFailed: (
        throwable: Throwable?,
        errorCode: Int?,
        errorMsg: String?
    ) -> Unit = { _, errorCode, errorMsg ->
        errorMsg?.let { if (errorCode != 4 && errorCode != 9) singleToast(it) }
    }
    var onComplete: () -> Unit = {}
}