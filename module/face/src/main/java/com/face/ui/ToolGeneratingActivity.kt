package com.face.ui

import android.os.Bundle
import android.view.View
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.bean.DynamicAddBean
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityToolGeneratingBinding
import com.face.key.AiTaskType
import com.face.ui.live.LiveStartActivity
import com.face.ui.paperwork.PaperworkStartActivity
import com.face.ui.tadd.AddActivity
import com.face.ui.tage.AgeStartActivity
import com.face.ui.thug.HugActivity
import com.face.ui.tkiss.KissActivity
import com.face.ui.tmoves.MovesActivity
import com.face.ui.tmoves.MovesStartActivity
import com.face.ui.toutfit.OutfitActivity
import com.face.ui.txtai.TxtImgStartActivity
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.ToolGeneratingViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.moshi.MoshiHelper
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class ToolGeneratingActivity :
    BaseBindingActivity<ActivityToolGeneratingBinding, ToolGeneratingViewModel>(
        R.layout.activity_tool_generating,
        ToolGeneratingViewModel::class.java
    ) {
    private val taskType by intentExtras("taskType", "")
    private val dataBeanId by intentExtras("taskId", "")

    val toolList: List<DynamicAddBean>? =
        MoshiHelper.convertJsonToList(SPUtils.listDynamicTools)
    var dynamicAddBean = DynamicAddBean()


    var timer: Timer? = null
    var timerTask: TimerTask? = null

    override fun init(savedInstanceState: Bundle?) {
        if (dataBeanId == "") {
            finish()
        }
        initView()

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
                    mBinding?.txtTime?.text = "${getString(R.string.clear_already_time)} $it"
                } else {
                    mBinding?.txtTime?.text = "${getString(R.string.clear_expected_time)} $it"
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
                    toolTaskBean.txtContent = it.content
                    toolTaskBean.type = it.taskType
                    toolTaskBean.recordUrl =
                        "${it.result.mData.firstOrNull()},${it.result.dataWater.firstOrNull()}"
                    openActivity<ToolCompletionActivity> {
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
        BaseContentDialog(
            getString(R.string.are_you2),
            getString(R.string.pro_hint2),
            getString(R.string.cancel),
            getString(R.string.interrupt),
            onRightData = { onCancelData() })
            .showIgnoreState(this)
    }

    fun initView() {
        var strTitle: String? = ""
        when (taskType) {
            AiTaskType.RMBG -> strTitle =
                getStringX(R.string.one_click_background_removal)

            AiTaskType.IMG2CARTOON -> strTitle =
                getStringX(R.string.image_to_cartoon)

            AiTaskType.OLD_PHONE -> strTitle =
                getStringX(R.string.old_photo_restoration)

            AiTaskType.ID_CARD -> strTitle =
                getStringX(R.string.id_photo_generation)

            AiTaskType.CHANGE_AGE -> strTitle =
                getStringX(R.string.age_change)

            AiTaskType.DYNAMIC -> strTitle =
                getStringX(R.string.pic_live)

            AiTaskType.TEXT2IMG -> strTitle =
                getStringX(R.string.txt_ai)

            AiTaskType.IMG2HUG -> strTitle =
                getStringX(R.string.txt_start_hug1)

            AiTaskType.IMG2KISS -> strTitle =
                getStringX(R.string.one_click_kiss)

            AiTaskType.CHANGE_CLOTHES -> strTitle =
                getStringX(R.string.one_click_outfit_change)

            AiTaskType.IMG2VIDEO -> strTitle =
                getStringX(R.string.spicy_moves)

            else -> {
                dynamicAddBean = toolList?.find { it.taskType == taskType } ?: DynamicAddBean()
                strTitle = dynamicAddBean.getName()
            }
        }
        mBinding?.tvTitle?.text = strTitle
        if (
            strTitle == getString(R.string.txt_start_hug1) ||
            strTitle == getString(R.string.one_click_kiss) ||
            strTitle == getString(R.string.one_click_outfit_change) ||
            !dynamicAddBean.isCanStop
        ) {
            mBinding?.imgClose?.visibility = View.GONE
        }

    }


    fun onRetry() {
        //先判断是不是配置的工具
        if (dynamicAddBean != null) {
            openActivity<AddActivity> {
                putString("tool_type", dynamicAddBean?.taskType)
            }
            return
        }

        when (mModel.taskBean.value?.taskType) {
            AiTaskType.ID_CARD -> openActivity<PaperworkStartActivity>()
            AiTaskType.DYNAMIC -> openActivity<LiveStartActivity>()
            AiTaskType.IMG2VIDEO -> openActivity<MovesStartActivity>()
            AiTaskType.CHANGE_AGE -> openActivity<AgeStartActivity>()
            AiTaskType.TEXT2IMG -> openActivity<TxtImgStartActivity>()
            AiTaskType.IMG2HUG -> openActivity<HugActivity>()
            AiTaskType.IMG2KISS -> openActivity<KissActivity>()
            AiTaskType.CHANGE_CLOTHES -> openActivity<OutfitActivity>()
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
