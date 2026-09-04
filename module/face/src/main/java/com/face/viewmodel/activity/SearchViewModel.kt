package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.face.net.Repository
import com.face.adapter.search.SearchHistoryAdapter
import com.face.adapter.search.SearchHotAdapter
import com.face.bean.SearchHistoryBean
import com.face.bean.TopSearchBean
import com.face.database.AppDatabase
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel : BaseViewModel() {

    var showHot = NotNullMutableLiveData(false)

    val input = MutableLiveData<String>()

    var resultTabIndex = 0

    val searchHistories = MutableLiveData<List<SearchHistoryBean>>()

    val historyAdapter = SearchHistoryAdapter()
    val hotAdapter = SearchHotAdapter()

    val expandHistory = MutableLiveData(false)
    val showHistoryExpand = MutableLiveData(false)

    val showIndex = MutableLiveData(0)
    val startSearch = MutableLiveData(0)

    val expandHistoryTitle = expandHistory.map {
        if (it) "PackUp" else "Expand"
    }


    var topSearchBean: TopSearchBean = SPUtils.searchTop


    init {
        viewModelScope.launch {
            AppDatabase.INSTANT.searchHistoryDao().getAll()
                .collectLatest { searchHistories.value = it }
        }
    }


    fun refreshSearchHotsIfNeed() {
        hotAdapter.submitList(topSearchBean.keyTop)
        launchRequestOnIO({ Repository.getTopSearchData() }) {
            onSuccess = { bean ->
                bean?.let {
                    showHot.value=it.keyTop.size>0
                    hotAdapter.submitList(it.keyTop)
                    SPUtils.searchTop = it
                }
            }
        }
    }

    fun getKeywords(): String? {
        if (!input.value.isNullOrBlank()) {
            return input.value
        }
        return null
    }

    fun onExpandHistoryClick() {
        expandHistory.value = !expandHistory.value!!
    }
}