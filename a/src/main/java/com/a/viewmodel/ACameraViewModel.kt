package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.zzkj.structure.base.BaseViewModel

class ACameraViewModel : BaseViewModel() {
    var showPhoto = MutableLiveData(false)
}