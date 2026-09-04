package com.face.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.app.NotificationManagerCompat
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.databinding.ActivityProductionBinding
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.BaseHintDialog
import com.face.view.CancelTaskDialog
import com.face.view.OpenMsgDialog
import com.face.viewmodel.activity.ProductionViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class ProductionActivity : BaseBindingActivity<ActivityProductionBinding, ProductionViewModel>(
    R.layout.activity_production, ProductionViewModel::class.java
) {
    val type by intentExtras("type", 0)
    val taskIdStr by intentExtras("task_id", "")
    val isHistory by intentExtras("isHistory", false)
    val urlImg by intentExtras("urlImg", "")
    var isTo = false

    companion object {
        fun jump(
            context: Context,
            type: Int,
            taskId: String,
            isHistory: Boolean = false,
            urlImg: String? = ""
        ) {
            context.openActivity<ProductionActivity> {
                putInt("type", type)//0图片，1视频
                putString("task_id", taskId)
                putBoolean("isHistory", isHistory)
                putString("urlImg", urlImg)
            }
        }
    }

    //    var countDownTimer: CountDownTimer? = null
    var timer: Timer? = null

    override fun init(savedInstanceState: Bundle?) {
        if (taskIdStr.isEmpty()) {
            toast(getString(R.string.fail))
            finish()
        }
        EventUtil.inPage("aitask_waite", mapOf("task_id" to taskIdStr))
        mModel.taskId.value = taskIdStr
        mModel.sendAiTask()

        mModel.countdown.observe(this) {//超时了显示等待时间
            if (!it.equals("") && it != "0s") {
                mBinding?.tvTime?.text = it
                mBinding?.txtTime?.text =
                    getString(R.string.production_expected_time) + " " + it
            }
        }
        mModel.progressNum.observe(this) {
            if (it > 99) {
                mBinding?.linTimeout?.visibility = VISIBLE
                mBinding?.checkingNum?.visibility = INVISIBLE
                mBinding?.txtTime?.visibility = INVISIBLE
                mBinding?.progress?.progress = 100
            } else if (it <= 0) {
                mBinding?.linTimeout?.visibility = INVISIBLE
                mBinding?.checkingNum?.visibility = INVISIBLE
                mBinding?.txtTime?.visibility = INVISIBLE
                mBinding?.progress?.progress = it
                mBinding?.checkingNum?.text = "-%"
            } else {
                mBinding?.linTimeout?.visibility = INVISIBLE
                mBinding?.txtTime?.visibility = VISIBLE
                if (0 < mModel.totalTime.value && mModel.totalTime.value < (60 * 1000)) {
                    mBinding?.checkingNum?.visibility = VISIBLE
                    mBinding?.progress?.progress = it
                    mBinding?.checkingNum?.text = "${it}%"
                } else {
                    mBinding?.checkingNum?.visibility = INVISIBLE
                    mBinding?.progress?.progress = it
                }
            }
        }


        mModel.taskBean.observe(this) {
            mModel.sendAiTask()
            if (it != null && !isTo) {
                if (it.state == 2) {//生成完成
                    isTo = true
                    if (GVM.INSTANT.isShowTool.value == 0) {
                        val mIntent = Intent().setClassName(
                            App.INSTANCE.packageName,
                            SPUtils.aCompletionName
                        )
                        mIntent.putExtra("task_id",it.id)
                        startActivity(mIntent)
                    } else {
                        if (it.taskType == AiTaskType.GEN_PERSONPIC) {//自定义风格4张
                            openActivity<CompletionFourActivity>() {
                                putParcelable("task_data", it)
                            }
                        } else {
                            openActivity<CompletionActivity>() {
                                putString("task_id", it.id)
                                putInt("task_num", 0)
                            }
                        }
                    }
                    finish()
                } else if (it.state == 3) {//生成失败
                    isTo = true
                    if (GVM.INSTANT.isShowTool.value == 0) {
                        BaseHintDialog(
                            "Hint",
                            getString(R.string.status_txt),
                            getString(R.string.ok)
                        ) {
                            finish()
                        }.showIgnoreState(mActivity)
                    } else {
                        openActivity<StatusActivity>() {
                            putParcelable("task_data", it)
                        }
                        finish()
                    }

                } else {
                    if (timer == null) {
                        if (it.taskType == AiTaskType.GEN_PERSONPIC) {
                            mModel.proStr.value = getString(R.string.production_the_production2)
                        }
                        mBinding?.tv2?.visibility = VISIBLE
                        timer = Timer()
                        timer?.schedule(object : TimerTask() {
                            override fun run() {
                                val millisFinished: Long
                                if (System.currentTimeMillis() > (mModel.taskBean.value?.estimatedTime
                                        ?: 0)
                                ) {
                                    millisFinished =
                                        System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                            ?: 0)
                                    mModel.progressNum.postValue(100)
                                } else {
                                    millisFinished =
                                        mModel.totalTime.value - (System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                            ?: 0))
                                    mModel.progressNum.postValue(
                                        (((System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                            ?: 0)).toFloat() / mModel.totalTime.value) * 100).toInt()
                                    )
                                }
                                getTime(millisFinished)
                            }
                        }, Date(), 1000)
                    }
                }
            }
        }
        mModel.proStr.value = getString(R.string.production_the_production)
        checkPushSwitchStatus()
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


    /**
     * 检查通知推送的开关状态
     */
    private fun checkPushSwitchStatus() {
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this);
        val isOpend = notificationManager.areNotificationsEnabled()
        if (isOpend) {
            mBinding?.tv4?.visibility = GONE
            mBinding?.tv3?.text=getString(R.string.production_msg1)
        } else {
            mBinding?.tv4?.visibility = VISIBLE
            mBinding?.tv3?.text=getString(R.string.production_msg)
        }

    }

    fun initClickListener() {
        val intent = Intent()
        try {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //8.0及以后版本使用这两个extra.  >=API 26
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
            } else {
                //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
                intent.putExtra("app_package", packageName)
                intent.putExtra("app_uid", applicationInfo.uid)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", packageName)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        checkPushSwitchStatus()
        if (GVM.INSTANT.isVip.value)
            mModel.sendAiTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        mModel.repsAiTask?.cancel()
        mModel.repsAiTask = null
    }

    fun onBackHome() {
        CancelTaskDialog { taskCancel() }.showIgnoreState(this)
    }

    fun taskCancel() {
        launchRequestWithLoadingOnIO({ Repository.cancelTask(taskIdStr) }) {
            onSuccess = {
                if (isHistory) {
                    finish()
                } else {
                    if (GVM.INSTANT.isShowTool.value == 0){
                        val mIntent = Intent().setClassName(
                            App.INSTANCE.packageName,
                            SPUtils.aMainName
                        )
                        startActivity(mIntent)
                    }else{
                        openActivity<MainActivity>()
                    }
                }
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun onToVip() {
        GVM.INSTANT.payPage.value =
            "Producion_${mModel.taskBean.value?.taskType}_${mModel.taskBean.value?.media?.mediaType}"
        VipActivity.jump(this@ProductionActivity)
    }

    fun onOpenMsg() {
        OpenMsgDialog(::initClickListener).showIgnoreState(this)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}