package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.face.net.Repository
import com.face.adapter.other.FacePageAdapter
import com.face.bean.AiFaceBean
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.toast
import kotlinx.coroutines.flow.collectLatest

class DetailsViewModel : BaseViewModel() {
    val isNoData = MutableLiveData<Boolean?>()
    val refreshError = MutableLiveData<Boolean?>()

    var faceAdapter = FacePageAdapter()

    fun getUserLikeMedia() {
        launch {
            Repository.getUserLikeMedias()
                .cachedIn(viewModelScope)
                .collectLatest {
                    faceAdapter.submitData(it)
                }
        }
    }


    fun getPicture(openValue: Int) {
        launch {
            Repository.getTopic(openValue)
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

    fun getHot() {
        launch {
            Repository.getHotsDetails()
                .cachedIn(viewModelScope)
                .collectLatest {
                    faceAdapter.submitData(it)
                }
        }
    }

    fun getType(tag: String) {
        launch {
            Repository.getMediaByTag(
                "",
                if (GVM.INSTANT.isShowTool.value != 0) "" else "image",
                tag
            )
                .cachedIn(viewModelScope)
                .collectLatest {
                    faceAdapter.submitData(it)
                }
        }
    }
}