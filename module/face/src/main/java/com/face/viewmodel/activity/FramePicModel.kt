package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.bean.TargetBean
import com.zzkj.structure.base.BaseViewModel

class FramePicModel : BaseViewModel() {
    val isDisable = MutableLiveData(true)
    //人脸数据
    var listFace = mutableListOf<TargetBean>()
    //提交的人脸
    var postFace = mutableListOf<TargetBean>()
}
