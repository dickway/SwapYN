package com.a.activity.tool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import com.a.R
import com.a.activity.AToolCompletionActivity
import com.a.databinding.ActivityAtoolRemovalBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.AToolRemovalViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.bean.ToolTaskBean
import com.face.util.GVM
import com.face.util.SPUtils
import com.king.drawboard.draw.Draw
import com.king.drawboard.view.DBV
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick
import java.util.Date
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

class ARemovalActivity : BaseBindingActivity<ActivityAtoolRemovalBinding, AToolRemovalViewModel>(
    R.layout.activity_atool_removal,
    AToolRemovalViewModel::class.java
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
        mModel.showSave.observe(this, Observer {
            mBinding?.linWelcome1?.alpha = if (it) 1f else 0.3f
        })


        Glide.with(this@ARemovalActivity).asBitmap()
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
                    dismissLoading()
                    mBinding?.drawBoardView?.setImageBitmap(mModel.initBitmap)
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

        mBinding?.drawBoardView?.setDrawBitmap(
            BitmapFactory.decodeResource(resources, com.key.R.mipmap.img_tool_icon_close),
            this
        )

        mBinding?.loadingView?.singleClick {//事件穿透

        }
        mModel.loadingView.observe(this) {
            if (it == 4) finish()
        }

        mBinding?.drawBoardView?.paintStyle = Paint.Style.FILL
        mBinding?.drawBoardView?.drawMode = DBV.DrawMode.DRAW_RECT
//        mBinding?.drawBoardView?.setLineStrokeWidth(mModel.seekbarNum.value.toFloat())
        mBinding?.drawBoardView?.paintColor = Color.parseColor("#80FF00F7")


        mBinding?.drawBoardView?.setOnDrawListener {
            mModel.showSave.value = mBinding?.drawBoardView?.listDraw?.size!! != 0
        }

        mBinding?.drawBoardView?.setOnSingleClickListener { x, y, bitmapRatio ->
            val listDraw: LinkedList<Draw>? = mBinding?.drawBoardView?.listDraw
            var indexN = -1
            listDraw?.forEachIndexed { index, draw ->
                val absX = abs(draw.upX - x)
                val absY = abs(draw.upY - y)

                val cXY = getScreenWidth() / 24 * bitmapRatio
                if (absX < cXY && absY < cXY) {
                    indexN = index
                }
            }
            mBinding?.drawBoardView?.undo(indexN)
            mModel.showSave.value = mBinding?.drawBoardView?.listDraw?.size!! != 0
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
                    var toolTaskBean = ToolTaskBean()
                    toolTaskBean.id=it.params.recordid
                    toolTaskBean.taskId = it.id
                    toolTaskBean.recordUrl = "${it.result.mData[0]},${it.result.dataWater[0]}"
                    openActivity<AToolCompletionActivity>() {
                        putParcelable("task", toolTaskBean)
                        putBoolean("ist", true)
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
        mModel.cutBitmap = null
        timer?.cancel()
        timerTask?.cancel()
    }

    fun eixtActivity() {
        if (mModel.loadingView.value == 2) {
            ABaseContentDialog(
                "Adiscard photo",
                "The current changes will not be saved",
                "Discard",
                "Cancel",
                onOkData = {
                    cancelTask()
                })
                .showIgnoreState(this)
        } else {
            finish()
        }
    }

    /**
     * 取消任务
     */
    fun cancelTask() {
        timer?.cancel()
        timerTask?.cancel()
        mModel.onCancelData()
    }

    /**
     * 提交图片
     */
    fun saveBitmap() {
        if (mModel.showSave.value) {
            showLoading("Uploading picture…", false)
            mModel.uploadPicture(mBinding?.drawBoardView?.cutBitmap)
//            mBinding?.imgShow?.setImageBitmap(mBinding?.drawBoardView?.cutBitmap)
//            mBinding?.imgShow?.visibility=VISIBLE
        }
    }

    /**
     * 提交图片
     */
    fun postImg() {
        if (mModel.showSave.value) {
            ABaseContentDialog(
                "Do you want to start extracting?",
                "Extraction may take time, please be patient",
                "Confirm",
                "Cancel",
                onOkData = { saveBitmap() })
                .showIgnoreState(this)
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

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(com.face.BR.handler, this)
            .addArgument(com.face.BR.vm, mModel)
            .addArgument(com.face.BR.gvm, GVM.INSTANT)
    }
}