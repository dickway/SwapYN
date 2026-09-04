package com.a.activity

import android.os.Bundle
import android.view.View
import com.a.BR
import com.a.R
import com.a.activity.tool.AChangeActivity
import com.a.activity.tool.AChangeStartActivity
import com.a.activity.tool.APaperworkStartActivity
import com.a.databinding.ActivityAtoolGeneratingBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.AToolGeneratingViewModel
import com.face.net.Repository
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.face.ui.live.LiveStartActivity
import com.face.ui.paperwork.PaperworkStartActivity
import com.face.ui.tage.AgeStartActivity
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.BarHelper.bar
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class AToolGeneratingActivity :
    BaseBindingActivity<ActivityAtoolGeneratingBinding, AToolGeneratingViewModel>(
        R.layout.activity_atool_generating,
        AToolGeneratingViewModel::class.java
    ) {
    private val strTitle by intentExtras("title", "ID photo generation")
    private val dataBeanId by intentExtras("taskId", "")

    var timer: Timer? = null
    var timerTask: TimerTask? = null

    override fun init(savedInstanceState: Bundle?) {
        bar {
            transparent()
            light(false)
        }
        if (dataBeanId == "") {
            finish()
        }

        mBinding?.tvTitle?.text = strTitle

        mModel.taskId = dataBeanId

        showLoading()
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
                    mBinding?.txtTime?.text = "Already waited $it"
                } else {
                    mBinding?.txtTime?.text = "Expect to wait $it"
                }
            }
        }
        mModel.taskBean.observe(this) {
            if (it != null) {
                if (it.state == 2) {//生成完成
                    timer?.cancel()
                    timerTask?.cancel()
                    timer = null
                    val toolTaskBean = ToolTaskBean()
                    toolTaskBean.id = it.params.recordid
                    toolTaskBean.recordUrl =
                        "${it.result.mData.firstOrNull()},${it.result.dataWater.firstOrNull()}"
                    openActivity<AToolCompletionActivity> {
                        putString("title", strTitle)
                        putParcelable("task", toolTaskBean)
                    }
                    finish()
                } else if (it.state == 3) {
                    timer?.cancel()
                    timerTask?.cancel()
                    timer = null
                    mBinding?.imgClose?.visibility = View.GONE
                    mBinding?.tvHint?.text = it.ee
                    mModel.loadingView.postValue(3)
                } else {
                    if (timer == null) {
                        timer = Timer()
                        timerTask = object : TimerTask() {
                            override fun run() {
                                val millisFinished: Long
                                if (System.currentTimeMillis() > (mModel.taskBean.value?.estimatedTime
                                        ?: 0)
                                ) {
                                    millisFinished =
                                        System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                            ?: 0)
                                    mModel.progressNum.postValue(99)
                                } else {
                                    millisFinished =
                                        mModel.totalTime.value -
                                                (System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                                    ?: 0))
                                    val ccTime =
                                        (System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                            ?: 0)).toFloat()
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
        ABaseContentDialog(
            "Terminate generation",
            "Are you sure you want to terminate the build?",
            "Cancel",
            "Interrupt",
            onCancelData = { onCancelData() })
            .showIgnoreState(this)
    }

    fun onRetry() {

        when (mModel.taskBean.value?.taskType) {
            AiTaskType.ID_CARD -> openActivity<APaperworkStartActivity>()
            AiTaskType.DYNAMIC -> openActivity<LiveStartActivity>()
            AiTaskType.CHANGE_AGE -> openActivity<AChangeStartActivity>()
            else -> finish()
        }


    }

    fun onCancelData() {
        launchRequestWithLoadingOnIO({ Repository.cancelTask(mModel.taskId) }) {
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
