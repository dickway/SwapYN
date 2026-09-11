package com.face.ui.tcustom

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.bean.TargetBean
import com.face.bean.TargetImgBean
import com.face.databinding.ActivityFramerateBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.videoclips.interfaces.IFramerateView
import com.face.view.FrameHintDialog
import com.face.viewmodel.activity.FrameViewModel
import com.face.ui.BaseBindingActivity
import com.face.videoclips.trim.VideoTrimmerUtil
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.toast


class FrameRateActivity : BaseBindingActivity<ActivityFramerateBinding, FrameViewModel>(
    R.layout.activity_framerate,
    FrameViewModel::class.java
) {
    val path by intentExtras("path", "")
    val isShare by intentExtras("isShare", false)
    val paramType by intentExtras("paramType", "")
    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            if (mBinding?.conImg?.visibility == View.VISIBLE) {
                mBinding?.conImg?.visibility = View.GONE
            }
        }
        if (SPUtils.openLongView) {
            SPUtils.openLongView = false
            onToHint()
        }
        try {
            mBinding?.apply {
                trimmerView.stopPlay()
                trimmerView.initVideoByURI(Uri.parse(path))
                trimmerView.initImg(conImg, imgOpen, object : IFramerateView {
                    override fun onTimeOut(time: Int) {
                        LogUtils.e(">>>>>>>>time:$time")
                        if (time>400*1000){//超长视频就返回
                            toast(getString(R.string.cut_failed_retry))
                            finish()
                        }
                    }
                    override fun onData(listData: List<TargetImgBean>) {
                        if (listData.isNotEmpty()) {
                            mModel.runNum = listData.size
                            mModel.newRunCount = 0
                            mModel.postImgList = mutableListOf()
                            mModel.listData = listData.toMutableList()
                            mModel.faceList.clear()
                            mModel.uploadPicture()
                        }
                    }
                })
            }
        } catch (e: Exception) {
            toast(getString(R.string.path_error))
            finish()
        }
        mBinding?.imgDe?.singleClick {
            mBinding?.conImg?.visibility = View.GONE
        }
        mBinding?.retry?.singleClick {
            mModel.newRunCount = 0
            mModel.faceList.clear()
            mModel.postImgList = mutableListOf()
            mModel.uploadPicture()
        }

        mModel.taskBean.observe(this) {
            if (it?.state == 2) {//生成完成
                it.result.faceUrl.forEach { str ->
                    if (str != "") {
                        mModel.faceList.add(TargetBean(path = str))
                    }
                }
                val videoPair = VideoTrimmerUtil.getVideoResolutionFast(path)
                dismissLoading()
                if (mModel.faceList.isNotEmpty()) {
                    openActivity<FramePicActivity> {
                        putString("video_url", path)
                        putString("video_hw", "${videoPair?.second},${videoPair?.first}")
                        putString("paramType", paramType)
                        putBoolean("isShare", isShare)
                        putParcelableArrayList("data", mModel.faceList as ArrayList)
                    }
                }
            } else if (it?.state == 3) {
                toast(it.ee)
                dismissLoading()
            }
        }

    }

    fun onToHint() {
        FrameHintDialog().showIgnoreState(mActivity)
    }

    override fun onPause() {
        super.onPause()
        mBinding?.trimmerView?.onVideoPause()
        mBinding?.trimmerView?.stopPlay()
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.isMaterial) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.trimmerView?.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
