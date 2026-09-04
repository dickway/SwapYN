package com.face.ui.tcustom

import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.bean.TargetBean
import com.face.databinding.ActivityVideoClipsBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.videoclips.interfaces.VideoTrimListener
import com.face.viewmodel.activity.VideoClipsModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import java.io.File

class VideoClipsActivity : BaseBindingActivity<ActivityVideoClipsBinding, VideoClipsModel>(
    R.layout.activity_video_clips,
    VideoClipsModel::class.java
), VideoTrimListener {


    val path by intentExtras("path", "")
    val paramType by intentExtras("paramType", "")
    val isShare by intentExtras("isShare", false)


    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
        }
        try {
            mBinding?.apply {
                trimmerView.setOnTrimVideoListener(this@VideoClipsActivity)
                trimmerView.stopPlay()
                trimmerView.initVideoByURI(Uri.parse(path))
            }
        } catch (e: Exception) {
            toast(getString(R.string.path_error))
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.isMaterial) {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding?.trimmerView?.onVideoPause()
        mBinding?.trimmerView?.stopPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.trimmerView?.onDestroy()
    }

    override fun onStartTrim() {
        launch(Dispatchers.Main) {
            showLoading(getResources().getString(R.string.trimming), false)
        }
    }

    override fun onFinishTrim(url: String?, duration: Long) {
        launch(Dispatchers.Main) {
            dismissLoading()
        }
        GVM.INSTANT.videoFaceList.postValue(mutableListOf(TargetBean(isAdd = true)))
        val file = File(url ?: "")  // 替换为实际的文件路径
        if (file.exists()) {
            GVM.INSTANT.videoFaceList.postValue(mutableListOf(TargetBean(isAdd = true)))
            mBinding?.trimmerView?.onDestroy()
            openActivity<FrameRateActivity> {
                putString("path", url)
                putString("paramType", paramType)
                putBoolean("isShare", isShare)
            }
        }else{
            toast(getString(R.string.path_error))
            finish()
        }
    }

    override fun onError(errorMsg: String?) {
        launch(Dispatchers.Main) {
            dismissLoading()
        }
        toast(errorMsg)
        finish()
    }

    override fun onCancel() {
        mBinding?.trimmerView?.onDestroy()
        finish()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }


}