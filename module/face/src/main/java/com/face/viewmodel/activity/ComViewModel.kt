package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.zzkj.structure.base.BaseViewModel

class ComViewModel : BaseViewModel() {
    val isSlide = MutableLiveData<Boolean>()

    val isZoom= MutableLiveData(false)
}