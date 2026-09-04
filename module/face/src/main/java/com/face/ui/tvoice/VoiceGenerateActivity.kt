package com.face.ui.tvoice

import android.os.Bundle
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityGenerateBinding
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.VoiceGenerateViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class VoiceGenerateActivity :
    BaseBindingActivity<ActivityGenerateBinding, VoiceGenerateViewModel>(
        R.layout.activity_generate,
        VoiceGenerateViewModel::class.java
    ) {
    private val isPost by intentExtras("ispost", false)//是否是上传界面过来
    private val dataBean by intentExtras("dataBean", ToolTaskBean())

    var timer: Timer? = null
    var timerTask: TimerTask? = null

    override fun init(savedInstanceState: Bundle?) {

        if (isPost) SPUtils.isMaterial = true

        mModel.taskId = dataBean.taskId
        mModel.queryAiTask()


        mModel.progressNum.observe(this) {
            if (it > 98) {
                mBinding?.progress?.progress = 99
                mBinding?.checkingNum?.text = "${it}%"
            } else if (it <= 0) {
                mBinding?.progress?.progress = it
                mBinding?.checkingNum?.text = "1%"
            } else {
                mBinding?.progress?.progress = it
                mBinding?.checkingNum?.text = "${it}%"
            }
        }

        mModel.taskBean.observe(this) {
            if (it != null) {
                if (it.state == 2 || it.state == 3) {//生成完成
                    openActivity<VoiceMaterialActivity>()
                    finish()
                } else {
                    if (timer == null) {
                        timer = Timer()
                        timerTask = object : TimerTask() {
                            override fun run() {
                                if (System.currentTimeMillis() > mModel.taskBean.value?.estimatedTime!!) {
                                    mModel.progressNum.postValue(99)
                                } else {
                                    val ccTime =
                                        (System.currentTimeMillis() - mModel.taskBean.value?.createTime!!).toFloat()
                                    mModel.progressNum.postValue((ccTime / mModel.totalTime.value * 100).toInt())
                                }
                            }
                        }
                        timer?.schedule(timerTask, Date(), 1000)
                    }
                }
            }
        }
    }


    fun interrupt() {
        BaseContentDialog(
            getString(R.string.are_you),
            getString(R.string.pro_hint),
            getString(R.string.cancel),
            getString(R.string.interrupt),
            onRightData = { onCancelData() })
            .showIgnoreState(this)
    }

    fun onCancelData() {
        launchRequestWithLoadingOnIO({ Repository.cancelTask(dataBean.taskId) }) {
            onSuccess = {
                finish()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timerTask?.cancel()
    }


    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}