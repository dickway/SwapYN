package com.zzkj.structure.base

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zzkj.structure.R
import com.zzkj.structure.util.ktx.getStringX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    val viewEffectLiveData: MutableLiveData<ViewEffect> = MutableLiveData()
    val viewEffectFlow: MutableStateFlow<ViewEffect> = MutableStateFlow(ViewEffect.NoneEffect)

    fun showLoading(msg: String = getStringX(R.string.loading), cancelable: Boolean = true) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            viewEffectLiveData.value = ViewEffect.ShowLoading(msg, cancelable)
        } else {
            viewEffectLiveData.postValue(ViewEffect.ShowLoading(msg, cancelable))
        }
    }

    fun dismissLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            viewEffectLiveData.value = ViewEffect.HideLoading
        } else {
            viewEffectLiveData.postValue(ViewEffect.HideLoading)
        }
    }

    private val commonLiveData by lazy { mutableMapOf<String, MutableLiveData<out Any>>() }

    /**
     * 获取一个MutableLiveData
     */
    fun <T : Any> getCommonLiveData(key: String): MutableLiveData<T> {
        var data = commonLiveData[key]
        if (data == null) {
            data = MutableLiveData<T>()
            commonLiveData[key] = data
        }
        return data as MutableLiveData<T>
    }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(context, start, block)
}
