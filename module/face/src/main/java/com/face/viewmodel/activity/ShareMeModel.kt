package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel

class ShareMeModel : BaseViewModel() {
    val isShowEdit = MutableLiveData(false)
    val isShareRefresh = MutableLiveData(-1)
    val isSelectDelete = GVM.INSTANT.showListSize.map {it->
        it>1
    }
}