package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.a.adapter.AFacePageAdapter
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest

class AMoreViewModel : BaseViewModel() {
    val isNoData = MutableLiveData<Boolean?>()
    val refreshError = MutableLiveData<Boolean?>()
    val faceAdapter = AFacePageAdapter()

    fun getType(tag: String) {
        launch {
            Repository.getMediaByTag(
                "",
                "image",
                tag
            )
                .cachedIn(viewModelScope)
                .collectLatest {
                    faceAdapter.submitData(it)
                }
        }
    }

    fun getBanner() {
        launch {
            Repository.getBannerDetails()
                .cachedIn(viewModelScope)
                .collectLatest {
                    faceAdapter.submitData(it)
                }
        }
    }
}