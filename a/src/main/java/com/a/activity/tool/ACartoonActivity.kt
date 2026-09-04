package com.a.activity.tool

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.addCallback
import com.a.BR
import com.a.R
import com.a.activity.AToolCompletionActivity
import com.a.databinding.ActivityAtoolCartoonBinding
import com.a.viewmodel.ACartoonViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.bean.ToolTaskBean
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.CancelClearDialog
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class ACartoonActivity : BaseBindingActivity<ActivityAtoolCartoonBinding, ACartoonViewModel>(
    R.layout.activity_atool_cartoon,
    ACartoonViewModel::class.java
) {

    val imgUrl by intentExtras("img_url", "")

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    override fun init(savedInstanceState: Bundle?) {
        bar {
            transparent()
            light(false)
        }
        onBackPressedDispatcher.addCallback(this) {
            eixtActivity()
        }
        mModel.getStyle()
        Glide.with(this@ACartoonActivity).asBitmap()
            .load(imgUrl)
            .skipMemoryCache(true)  // 禁用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    //检查是否需要压缩
                    mModel.initBitmap = resource
                    val width = resource.getWidth()
                    val height = resource.getHeight()
                    var inSampleSize = 1
                    val max = 20 * 1024 * 1024
                    while (width * height * 4 / inSampleSize > max) {
                        inSampleSize *= 2
                    }
                    if (inSampleSize > 1) {
                        val m2 = Matrix()
                        m2.setScale(1 / inSampleSize.toFloat(), 1 / inSampleSize.toFloat())
                        mModel.initBitmap =
                            Bitmap.createBitmap(resource, 0, 0, width, height, m2, false);
                    }

                    val widths = getScreenWidth()//控件最大宽度
                    val heights = (getScreenHeight() - 260.dp)//控件最大高度

                    val thresholdValue = widths / heights.toDouble() //阈值
                    var realValue = width / height //实际比例
                    dismissLoading()
                    mBinding?.initImg?.apply {
                        if (thresholdValue > realValue) {
                            layoutParams.width = heights * width / height
                            layoutParams.height = heights
                        } else {
                            layoutParams.width = widths
                            layoutParams.height = widths * height / width
                        }

                        mModel.hightImg = layoutParams.height
                        mModel.wigthImg = layoutParams.width

                        setImageBitmap(mModel.initBitmap)

                    }

                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    showLoading()
                    super.onLoadStarted(placeholder)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    dismissLoading()
                    super.onLoadFailed(errorDrawable)
                }

                override fun onDestroy() {
                    dismissLoading()
                    super.onDestroy()
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        mModel.styleAdapter.onItemClick = { _, bean, _ ->
            mModel.style = bean?.key ?: "cute"
        }
        mBinding?.loadingView?.singleClick {//事件穿透

        }
        mModel.loadingView.observe(this) {
            if (it == 4) finish()
        }

        mModel.countdown.observe(this) {//显示等待时间
            if (!it.equals("")) {
                if (mModel.progressNum.value > 98) {
                    mBinding?.txtTime?.text = "Already waited $it"
                } else {
                    mBinding?.txtTime?.text = "Expect to wait $it"
                }
            }
        }
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
                if (it.state == 2) {//生成完成
                    SPUtils.isMaterial = true
                    timer?.cancel()
                    timerTask?.cancel()
                    val toolTaskBean = ToolTaskBean()
                    toolTaskBean.id = it.params.recordid
                    toolTaskBean.taskId = it.id
                    toolTaskBean.recordUrl = "${it.result.mData[0]},${it.result.dataWater[0]}"
                    openActivity<AToolCompletionActivity>() {
                        putParcelable("task", toolTaskBean)
                    }
                    finish()

                } else if (it.state == 3) {//生成失败
                    mModel.loadingView.postValue(3)
                    mBinding?.tvHint?.text = it.ee
                    SPUtils.isMaterial = true
                    timer?.cancel()
                    timerTask?.cancel()

                } else {
                    if (timer == null) {
                        timer = Timer()
                        timerTask = object : TimerTask() {
                            override fun run() {
                                val millisFinished: Long
                                if (System.currentTimeMillis() > it.estimatedTime) {
                                    millisFinished = System.currentTimeMillis() - it.createTime
                                    mModel.progressNum.postValue(99)
                                } else {
                                    millisFinished =
                                        mModel.totalTime.value -
                                                (System.currentTimeMillis() - it.createTime)
                                    val ccTime =
                                        (System.currentTimeMillis() - it.createTime).toFloat()
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

    override fun onDestroy() {
        super.onDestroy()
        mModel.initBitmap = null
        timer?.cancel()
        timerTask?.cancel()
    }

    fun eixtActivity() {
        if (mModel.loadingView.value == 2) {
            CancelClearDialog {
                cancelTask()
            }.showIgnoreState(this)
        } else {
            finish()
        }
    }

    /**
     * 取消任务
     */
    fun cancelTask() {
        mModel.onCancelData()
    }

    /**
     * 提交图片
     */
    fun saveBitmap() {
        showLoading("Uploading picture…", false)
        mModel.uploadPicture()
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

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}