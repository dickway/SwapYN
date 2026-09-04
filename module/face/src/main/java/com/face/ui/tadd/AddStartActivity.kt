package com.face.ui.tadd

import android.os.Bundle
import com.apkfuns.logutils.LogUtils
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.bean.DynamicAddBean
import com.face.databinding.ActivityStartAddBinding
import com.face.ui.FunZoneHistoryActivity
import com.face.util.SPUtils
import com.face.video.CustomManager
import com.face.video.EmptyVideo
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.face.ui.BaseBindingActivity
import com.face.ui.ToolGeneratingActivity
import com.face.ui.VipActivity
import com.face.video.VideoHeaderManager
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.AddStartViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlin.getValue

class AddStartActivity : BaseBindingActivity<ActivityStartAddBinding, AddStartViewModel>(
    R.layout.activity_start_add,
    AddStartViewModel::class.java
) {

    var toolList: List<DynamicAddBean>? = MoshiHelper.convertJsonToList(SPUtils.listDynamicTools)
    val toolId by intentExtras("tool_id", -1)
    var dynamicAddBean: DynamicAddBean? = null
    var tagVideo = ""

    override fun init(savedInstanceState: Bundle?) {
        dynamicAddBean = toolList?.find { it.id == toolId }
        if (dynamicAddBean == null) {
            toast("Configuration abnormality!")
            finish()
            return
        }

        mBinding?.apply {
            tagVideo = videoView.getVideoKey()
            tvStart1.text = dynamicAddBean?.getName()
            tvStart2.text = dynamicAddBean?.getDesc()
            mBinding?.videoView?.loadBgImage4(dynamicAddBean?.urlImg ?: "")
            GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setVideoTitle("")
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setPlayTag(EmptyVideo.Tag)
                .setLooping(true)
                .setCacheWithPlay(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setPlayPosition(0)
                .setMapHeadData(VideoHeaderManager.headers)
                .setUrl(dynamicAddBean?.urlVideo)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPlayError(url: String?, vararg objects: Any?) {
                        super.onPlayError(url, *objects)
                        videoView.setUp( url,true, null,VideoHeaderManager.headers,"")
                        videoView.startPlayLogic()
                    }
                })
                .build(mBinding?.videoView)
            videoView.postDelayed({
                videoView.startPlayLogic()
            }, 200)
        }

        mModel.GeneratingId.observe(this) {
            if (it == "") {//没有生成中任务可以走下一个
                openActivity<AddActivity>{
                    putString("tool_type", dynamicAddBean?.taskType)
                }
            } else if (it == "-1") {//初始值不执行

            } else {//有生成中任务
                BaseContentDialog(
                    getString(R.string.dialog_paper_only),
                    getString(R.string.dialog_paper_only_txt),
                    getString(R.string.cancel),
                    getString(R.string.btn_record),
                    false,
                    onRightData = { toTask(it) })
                    .showIgnoreState(this)
            }
        }

    }

    fun toTask(taskId: String) {
        openActivity<ToolGeneratingActivity> {
            putString("taskId", taskId)
            putString("taskType", dynamicAddBean?.taskType)
        }
    }

    fun toRecord() {
        if (!GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = dynamicAddBean?.eventName
            VipActivity.jump(this@AddStartActivity)
            return
        }
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", dynamicAddBean?.taskType)
        }
    }

    fun onstart() {
        if (!GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = dynamicAddBean?.eventName
            VipActivity.jump(this@AddStartActivity)
            return
        }
        if (dynamicAddBean?.isSingleTask == true) {
            mModel.getTask(dynamicAddBean?.taskType ?: "")
        } else {
            openActivity<AddActivity>{
                putString("tool_type", dynamicAddBean?.taskType)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding?.videoView?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding?.videoView?.onVideoResume()
    }

    override fun onDestroy() {
        if (tagVideo.isNotEmpty()) {
            CustomManager.clearAllVideo(tagVideo)
        }
        super.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}