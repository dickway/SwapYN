package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.a.adapter.AFacePageAdapter
import com.a.adapter.ASearchHistoryAdapter
import com.face.bean.SearchHistoryBean
import com.face.database.AppDatabase
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ASearchViewModel : BaseViewModel() {
    val faceAdapter = AFacePageAdapter()
    val isNoData = MutableLiveData<Boolean?>()

    val input = MutableLiveData<String>()

    val searchHistories = MutableLiveData<List<SearchHistoryBean>>()

    val historyAdapter = ASearchHistoryAdapter()

    val showIndex = MutableLiveData(0)
    val startSearch = MutableLiveData(0)

    fun getSeatch() {
        isNoData.value = null
        launch {
            Repository.searchAi("image", getKeywords())
                .cachedIn(viewModelScope)
                .collectLatest {
                    faceAdapter.submitData(it)
                }
        }
    }

    init {
        viewModelScope.launch {
            AppDatabase.INSTANT.searchHistoryDao().getAll()
                .collectLatest { searchHistories.value = it }
        }
    }

    fun getKeywords(): String {
        if (!input.value.isNullOrBlank()) {
            return input.value?:""
        }
        return ""
    }
}