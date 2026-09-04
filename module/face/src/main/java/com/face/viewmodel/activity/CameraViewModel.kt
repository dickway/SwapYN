package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.zzkj.structure.base.BaseViewModel

class CameraViewModel : BaseViewModel() {
    var showPhoto = MutableLiveData(false)
}