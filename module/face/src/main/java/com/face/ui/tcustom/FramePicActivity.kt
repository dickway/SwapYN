package com.face.ui.tcustom

import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.adapter.other.FrameratePicAdapter
import com.face.bean.TargetBean
import com.face.databinding.ActivityFrameratePicBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.viewmodel.activity.FramePicModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity

class FramePicActivity : BaseBindingActivity<ActivityFrameratePicBinding, FramePicModel>(
    R.layout.activity_framerate_pic,
    FramePicModel::class.java
) {
    val videoUrl by intentExtras("video_url", "")
    val videoHW by intentExtras("video_hw", "")
    val isShare by intentExtras("isShare", false)
    val paramType by intentExtras("paramType", "")
    var frameratePicAdapter = FrameratePicAdapter()
    private val listData by intentExtras("data", mutableListOf<TargetBean>())
    override fun init(savedInstanceState: Bundle?) {
        mModel.listFace = listData
        mBinding?.recyclerView?.adapter = frameratePicAdapter
        frameratePicAdapter.submitList(mModel.listFace)
        mBinding?.tvNum?.text = String.format(
            resources.getString(R.string.selected_face),
            "0"
        )
        mModel.isDisable.observe(this) {
            if (it) {
                mBinding?.imgBtn?.alpha = 0.3f
            } else {
                mBinding?.imgBtn?.alpha = 1f
            }
        }

        frameratePicAdapter.onItemClick = { _, bean, position ->
            mModel.listFace.find { bean?.path == it.path }.apply {
                if (mModel.postFace.size < 3 || this?.isSelect == true) {
                    this?.isSelect = !(this?.isSelect ?: false)
                }
            }
            if (bean?.isSelect == true) {
                mModel.postFace.add(bean)
            } else {
                mModel.postFace.remove(bean)
            }
            val nowNum = mModel.postFace.size
            mModel.isDisable.postValue(nowNum < 1)
            mBinding?.tvNum?.text = String.format(
                resources.getString(R.string.selected_face),
                nowNum.toString()
            )
            frameratePicAdapter.notifyItemChanged(position)
        }
    }

    override fun onResume() {
        super.onResume()
        if (SPUtils.isMaterial) {
            finish()
        }
    }

    fun postTask() {
        if (mModel.postFace.isNotEmpty()) {
            openActivity<FramePostVideoActivity> {
                putString("video_url", videoUrl)
                putString("video_hw", videoHW)
                putString("paramType", paramType)
                putBoolean("isShare", isShare)
                putParcelableArrayList("face_data", mModel.postFace as ArrayList)
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}