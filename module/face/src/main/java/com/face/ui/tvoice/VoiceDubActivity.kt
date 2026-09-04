package com.face.ui.tvoice

import android.os.Bundle
import android.view.View
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityVoiceDubBinding
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.VoiceDubViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class VoiceDubActivity :
    BaseBindingActivity<ActivityVoiceDubBinding, VoiceDubViewModel>(
        R.layout.activity_voice_dub,
        VoiceDubViewModel::class.java
    ) {
    private val dataBean by intentExtras("dataBean", ToolTaskBean())

    var timer: Timer? = null
    var timerTask: TimerTask? = null

    override fun init(savedInstanceState: Bundle?) {
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
        mModel.countdown.observe(this) {//显示等待时间
            if (!it.equals("")) {
                mBinding?.txtTime?.visibility = View.VISIBLE
                if (mModel.progressNum.value > 98) {
                    mBinding?.txtTime?.text = "${getString(R.string.clear_already_time)} $it"
                } else {
                    mBinding?.txtTime?.text = "${getString(R.string.clear_expected_time)} $it"
                }
            }
        }
        mModel.taskBean.observe(this) {
            if (it != null) {
                if (it.state == 2 || it.state == 3) {//生成完成
                    val toolTaskBean = ToolTaskBean()
                    toolTaskBean.taskId = it.id
                    toolTaskBean.state = it.state
                    toolTaskBean.id = it.params.recordid
                    toolTaskBean.createTime = it.createTime
                    toolTaskBean.errorMessage = it.ee
                    toolTaskBean.name = getString(R.string.ai_sound)
                    openActivity<VoiceCompletionActivity> {
                        putParcelable("dataBean", toolTaskBean)
                    }
                    finish()
                } else {
                    if (timer == null) {
                        timer = Timer()
                        timerTask = object : TimerTask() {
                            override fun run() {
                                val millisFinished: Long
                                if (System.currentTimeMillis() > mModel.taskBean.value?.estimatedTime!!) {
                                    millisFinished =
                                        System.currentTimeMillis() - mModel.taskBean.value?.createTime!!
                                    mModel.progressNum.postValue(99)
                                } else {
                                    millisFinished =
                                        mModel.totalTime.value -
                                                (System.currentTimeMillis() - mModel.taskBean.value?.createTime!!)
                                    val ccTime =
                                        (System.currentTimeMillis() - mModel.taskBean.value?.createTime!!).toFloat()
                                    mModel.progressNum.postValue((ccTime / mModel.totalTime.value * 100).toInt())
                                }
                                getTime(millisFinished)
                            }
                        }
                        timer?.schedule(timerTask, Date(), 1000)
                    }
                }
            }
        }
    }

    fun getTime(countdownTime: Long) {
        val hour = countdownTime / 1000 / 60 / 60
        val minute = countdownTime / 1000 / 60 % 60
        val second = countdownTime / 1000 % 60
        if (hour.toInt() != 0) {
            if (minute.toInt() != 0) {
                mModel.countdown.postValue("${hour}h${minute}min")
            } else {
                mModel.countdown.postValue("${hour}h")
            }
        } else if (minute.toInt() != 0) {
            if (second.toInt() != 0) {
                mModel.countdown.postValue("${minute}min${second}s")
            } else {
                mModel.countdown.postValue("${minute}min")
            }
        } else if (second.toInt() != 0) {
            mModel.countdown.postValue("${second}s")
        }
    }

    fun interrupt() {
        BaseContentDialog(
            getString(R.string.are_you2),
            getString(R.string.pro_hint2),
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