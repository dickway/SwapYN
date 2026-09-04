package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.net.Repository
import com.face.adapter.other.MovesListAdapter
import com.face.bean.MovesBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class MovesListViewModel : BaseViewModel() {

    val movesListAdapter: MovesListAdapter = MovesListAdapter()
    var movesList = MutableLiveData<List<MovesBean>?>()
    val isNoData = MutableLiveData<Boolean?>()
    fun getUserViewHistory() {
        launchRequestWithLoadingOnIO({ Repository.img2VideoConfig() }) {
            onSuccess = { list ->
                isNoData.setIfNot((list?.size ?: 0) < 1)
                movesList.postValue(list)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

}