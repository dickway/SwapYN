package com.zzkj.structure.net

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.zzkj.structure.R
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.IUiView
import com.zzkj.structure.util.ktx.getStringX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * 发起请求
 * 直接返回数据
 */
fun <T> launchRequest(
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    requestBlock: suspend () -> ApiResponse<T>
): Flow<ApiResponse<T>> {
    return flow {
        emit(requestBlock())
    }.onStart {
        startCallback?.invoke()
        emit(ApiResponse(API_STATE_START))
    }.onCompletion {
        completeCallback?.invoke()
        emit(ApiResponse(API_STATE_COMPLETE))
    }
}

/**
 * 在IO线程发起请求
 * resultLiveData接收数据
 */
fun <T> CoroutineScope.launchRequestOnIO(
    resultLiveData: MutableLiveData<ApiResponse<T>>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    requestBlock: suspend () -> ApiResponse<T>
) = launch {
    launchRequest(startCallback, completeCallback, requestBlock)
        .collect {
            resultLiveData.value = it
        }
}

/**
 * 在IO线程发起请求
 * listenerBuilder接收数据
 */
fun <T> CoroutineScope.launchRequestOnIO(
    requestBlock: suspend () -> ApiResponse<T>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) = launch {
    launchRequest(startCallback, completeCallback, requestBlock)
        .collect {
            it.parseData(listenerBuilder)
        }
}


/**
 * 在IO线程发起请求
 * resultLiveData接收数据
 */
fun <T> ViewModel.launchRequestOnIO(
    resultLiveData: MutableLiveData<ApiResponse<T>>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    requestBlock: suspend () -> ApiResponse<T>
) = viewModelScope.launchRequestOnIO(resultLiveData, startCallback, completeCallback, requestBlock)


/**
 * 在IO线程发起请求
 * listenerBuilder接收数据
 */
fun <T> ViewModel.launchRequestOnIO(
    requestBlock: suspend () -> ApiResponse<T>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) = viewModelScope.launchRequestOnIO(requestBlock, startCallback, completeCallback, listenerBuilder)


/**
 * 发起带loading的请求
 * 直接返回数据
 */
fun <T> BaseViewModel.launchRequestWithLoading(
    msg: String = getStringX(R.string.loading),
    cancelable: Boolean = true,
    requestBlock: suspend () -> ApiResponse<T>
): Flow<ApiResponse<T>> {
    return launchRequest(
        { showLoading(msg, cancelable) },
        { dismissLoading() },
        { requestBlock.invoke() }
    )
}

/**
 * 在IO线程发起带loading的请求
 * resultLiveData接收数据
 */
fun <T> BaseViewModel.launchRequestWithLoadingOnIO(
    resultLiveData: MutableLiveData<ApiResponse<T>>,
    msg: String = getStringX(R.string.loading),
    cancelable: Boolean = true,
    requestBlock: suspend () -> ApiResponse<T>
) = viewModelScope.launchRequestOnIO(
    resultLiveData,
    { showLoading(msg, cancelable) },
    { dismissLoading() },
    requestBlock
)

/**
 * 在IO线程发起带loading的请求
 * listenerBuilder接收数据
 */
fun <T> BaseViewModel.launchRequestWithLoadingOnIO(
    requestBlock: suspend () -> ApiResponse<T>,
    msg: String = getStringX(R.string.loading),
    cancelable: Boolean = true,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) = viewModelScope.launchRequestOnIO(
    requestBlock,
    { showLoading(msg, cancelable) },
    { dismissLoading() },
    listenerBuilder
)


/**
 * 发起带loading的请求
 * 直接返回数据
 */
fun <T> IUiView.launchRequestWithLoading(
    msg: String = getStringX(R.string.loading),
    cancelable: Boolean = true,
    requestBlock: suspend () -> ApiResponse<T>
): Flow<ApiResponse<T>> {
    return launchRequest(
        { showLoading(msg, cancelable) },
        { dismissLoading() },
        { requestBlock.invoke() }
    )
}

/**
 * 在IO线程发起请求
 * resultLiveData接收数据
 */
fun <T> IUiView.launchRequestOnIO(
    resultLiveData: MutableLiveData<ApiResponse<T>>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    requestBlock: suspend () -> ApiResponse<T>
) = lifecycleScope.launchRequestOnIO(resultLiveData, startCallback, completeCallback, requestBlock)

/**
 * 在IO线程发起请求
 * listenerBuilder接收数据
 */
fun <T> IUiView.launchRequestOnIO(
    requestBlock: suspend () -> ApiResponse<T>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    lifecycleScope.launchRequestOnIO(requestBlock, startCallback, completeCallback, listenerBuilder)
}

/**
 * 在IO线程发起带loading的请求
 * resultLiveData接收数据
 */
fun <T> IUiView.launchRequestWithLoadingOnIO(
    resultLiveData: MutableLiveData<ApiResponse<T>>,
    msg: String = getStringX(R.string.loading),
    cancelable: Boolean = true,
    requestBlock: suspend () -> ApiResponse<T>
) = launchRequestOnIO(
    resultLiveData,
    { showLoading(msg, cancelable) },
    { dismissLoading() },
    requestBlock
)


/**
 * 在IO线程发起带loading的请求
 * listenerBuilder接收数据
 */
fun <T> IUiView.launchRequestWithLoadingOnIO(
    requestBlock: suspend () -> ApiResponse<T>,
    msg: String = getStringX(R.string.loading),
    cancelable: Boolean = true,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) = launchRequestOnIO(
    requestBlock,
    { showLoading(msg, cancelable) },
    { dismissLoading() },
    listenerBuilder
)


fun <T> Flow<ApiResponse<T>>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    listenerBuilder: ResultBuilder<T>.() -> Unit,
) {
    if (owner is Fragment) {
        owner.viewLifecycleOwner.lifecycleScope.launch {
            owner.viewLifecycleOwner.repeatOnLifecycle(minActiveState) {
                collect { apiResponse: ApiResponse<T> ->
                    apiResponse.parseData(listenerBuilder)
                }
            }
        }
    } else {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(minActiveState) {
                collect { apiResponse: ApiResponse<T> ->
                    apiResponse.parseData(listenerBuilder)
                }
            }
        }
    }
}



