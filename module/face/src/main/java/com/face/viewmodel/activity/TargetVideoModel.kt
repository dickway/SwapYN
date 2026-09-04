package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.adapter.other.TargetAdapter
import com.face.adapter.other.TargetVideoAdapter
import com.face.bean.TargetBean
import com.zzkj.structure.base.BaseViewModel

class TargetVideoModel : BaseViewModel() {
    val faceAdapter = TargetVideoAdapter()
    val targetFaceAdapter = TargetAdapter()
    var faceList = MutableLiveData<List<TargetBean>>(mutableListOf())
    var targetFaceList = MutableLiveData<List<TargetBean>>(mutableListOf())
}