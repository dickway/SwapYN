package com.face.viewmodel.activity

import com.face.BuildConfig
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX

class SettingViewModel : BaseViewModel() {

    var isShowDownload =  NotNullMutableLiveData(SPUtils.versionDownloadShow.contains("${getStringX(
        com.key.R.string.app_ai_name)}${BuildConfig.VERSION_CODE}"))

}