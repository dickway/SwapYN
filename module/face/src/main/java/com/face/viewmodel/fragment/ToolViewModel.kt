package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.face.adapter.tool.ToolFunzoneAdapter
import com.face.bean.TaskBean
import com.face.bean.ToolFunzoneBean
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.moshi.MoshiHelper

class ToolViewModel : BaseViewModel() {
    var isShowVoice = MutableLiveData(SPbaseUtils.spLanguage in listOf("en", "es", "zh"))
    var tool2List = MutableLiveData<List<ToolFunzoneBean>>()
    var tool2ListAdapter: ToolFunzoneAdapter = ToolFunzoneAdapter()
}