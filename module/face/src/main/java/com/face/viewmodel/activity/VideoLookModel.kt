package com.face.viewmodel.activity

import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.util.NotNullMediatorLiveData

class VideoLookModel : BaseViewModel() {
    val isPause = NotNullMediatorLiveData(false)//是否暂停播放
}