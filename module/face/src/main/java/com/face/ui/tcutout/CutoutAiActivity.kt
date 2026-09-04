package com.face.ui.tcutout

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.king.drawboard.draw.Draw
import com.king.drawboard.view.DBV
import com.face.BR
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityAicutoutBinding
import com.face.ui.ToolCompletionActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.view.CancelClearDialog
import com.face.viewmodel.activity.CutoutAiViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
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

class CutoutAiActivity : BaseBindingActivity<ActivityAicutoutBinding, CutoutAiViewModel>(
    R.layout.activity_aicutout,
    CutoutAiViewModel::class.java
) {
    val imgUrl by intentExtras("img_url", "")

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            eixtActivity()
        }
        Glide.with(this@CutoutAiActivity).asBitmap()
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
        mBinding?.drawBoardView?.paintColor = Color.parseColor("#8000FFA8")


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
                    mBinding?.txtTime?.text = "${getString(R.string.clear_already_time)} $it"
                } else {
                    mBinding?.txtTime?.text = "${getString(R.string.clear_expected_time)} $it"
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
                    toolTaskBean.taskId = it.id
                    toolTaskBean.type = it.taskType
                    toolTaskBean.recordUrl = "${it.result.mData[0]},${it.result.dataWater[0]}"
                    openActivity<ToolCompletionActivity>() {
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
        timer?.cancel()
        timerTask?.cancel()
        mModel.onCancelData()
    }

    /**
     * 提交图片
     */
    fun saveBitmap() {
        if (mModel.showSave.value) {
            showLoading(getString(R.string.picpost_title), false)
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
            BaseContentDialog(
                getString(R.string.aicutout_dialog_title),
                getString(R.string.aicutout_dialog_content),
                getString(R.string.cancel),
                getString(R.string.confirm),
                onRightData = { saveBitmap() })
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
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}