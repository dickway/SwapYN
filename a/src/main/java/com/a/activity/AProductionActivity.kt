package com.a.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.addCallback
import coil.load
import com.a.BR
import com.a.R
import com.a.databinding.ActivityAproductionBinding
import com.a.dialog.AFailYDialog
import com.a.viewmodel.AProductionViewModel
import com.base.BlurTransformation
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class AProductionActivity : BaseBindingActivity<ActivityAproductionBinding, AProductionViewModel>(
    R.layout.activity_aproduction, AProductionViewModel::class.java
) {
    val taskIdStr by intentExtras("task_id", "")
    val urlImg by intentExtras("urlImg", "")
    val imgw by intentExtras("imgw", 1)
    val imgh by intentExtras("imgh", 1)

    companion object {
        fun jump(
            context: Context,
            taskId: String? = "",
            urlImg: String? = "",
            imgw: Int? = 1,
            imgh: Int? = 1
        ) {
            context.openActivity<AProductionActivity> {
                putString("task_id", taskId)
                putString("urlImg", urlImg)
                putInt("imgw", imgw ?: 1)
                putInt("imgh", imgh ?: 1)
            }
        }
    }

    var timer: Timer? = null
    var isTo = false
    override fun init(savedInstanceState: Bundle?) {
        if (taskIdStr.isEmpty()) {
            toast(getStringX(R.string.afail))
            finish()
        }
        onBackPressedDispatcher.addCallback(this) {

        }
        mBinding?.imgCompletion?.apply {
//            layoutParams.height = 160.dp
//            layoutParams.width = 160.dp * imgw / imgh
            load(urlImg) {
                transformations(BlurTransformation(radius = 8, scale = 0.2f))
            }
        }

        EventUtil.inPage("aitask_waite", mapOf("task_id" to taskIdStr))
        mModel.taskId.value = taskIdStr
        mModel.sendAiTask()

        mModel.progressNum.observe(this) {
            if (it > 99) {
                mBinding?.progress?.progress = 99
            } else if (it <= 0) {
                mBinding?.progress?.progress = it
                mBinding?.checkingNum?.text = "-%"
            } else {
                mBinding?.progress?.progress = it
                mBinding?.checkingNum?.text = "${it}%"
            }
        }


        mModel.taskBean.observe(this) {
            mModel.sendAiTask()
            if (it != null && !isTo) {
                if (it.state == 2) {//生成完成
                    isTo = true
                    openActivity<ACompletionActivity>() {
                        putString("task_id", it.id)
                    }
                    finish()
                } else if (it.state == 3) {//生成失败
                    isTo = true
                    AFailYDialog(onBtnOK = {
                        if (it.media.mediaType != "video") {
                            ASwapActivity.jump(this, it.media.mediaId)
                        } else {
                            SwapFaceNewActivity.jump(this, it.media.mediaId)
                        }
                    }
                    ).showIgnoreState(this)
                } else {
                    if (timer == null) {
                        timer = Timer()
                        timer?.schedule(object : TimerTask() {
                            override fun run() {
                                if (System.currentTimeMillis() > (mModel.taskBean.value?.estimatedTime
                                        ?: 0)
                                ) {
                                    mModel.progressNum.postValue(100)
                                } else {
                                    mModel.progressNum.postValue(
                                        (((System.currentTimeMillis() - (mModel.taskBean.value?.createTime
                                            ?: 0)).toFloat() / mModel.totalTime.value) * 100).toInt()
                                    )
                                }
                            }
                        }, Date(), 1000)
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        mModel.repsAiTask?.cancel()
        mModel.repsAiTask = null
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}