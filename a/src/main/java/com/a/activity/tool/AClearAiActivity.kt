package com.a.activity.tool

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.OnTouchListener
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.activity.addCallback
import com.a.activity.AToolCompletionActivity
import com.a.databinding.ActivityAtoolAiclearBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.AClearAiViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.king.drawboard.draw.Draw
import com.a.BR
import com.a.R
import com.face.util.GVM
import com.face.util.SPUtils
import com.king.drawboard.view.DBV
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.toast
import java.util.Date
import java.util.LinkedList
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs

class AClearAiActivity : BaseBindingActivity<ActivityAtoolAiclearBinding, AClearAiViewModel>(
    R.layout.activity_atool_aiclear,
    AClearAiViewModel::class.java
) {
    val imgUrl by intentExtras("img_url", "")

    var timer: Timer? = null
    var timerTask: TimerTask? = null
    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            eixtActivity()
        }
        Glide.with(this@AClearAiActivity).asBitmap()
            .load(imgUrl)
            .placeholder(com.key.R.drawable.img_default_m)
            .skipMemoryCache(true)  // 禁用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {
                override fun onStart() {
                    showLoading()
                    super.onStart()
                }

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
                    mModel.upBitmap = mModel.initBitmap
                    mModel.upWaterBitmap = mModel.initBitmap
                    mModel.postBitmap = mModel.initBitmap
                    mBinding?.initImg?.setImageBitmap(mModel.initBitmap)
                    mBinding?.drawBoardView?.setImageBitmap(mModel.postBitmap)

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

        mBinding?.drawBoardView?.setLineStrokeWidth(mModel.seekbarNum.value.toFloat())
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
        mBinding?.seekbarEraser?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                mModel.seekbarNum.value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mBinding?.drawBoardView?.drawMode ==  DBV.DrawMode.DRAW_PATH) {
                    mBinding?.drawBoardView?.setLineStrokeWidth(seekBar.progress.toFloat())
                }
            }
        })

        mBinding?.imgOriginal?.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (mModel.showOriginacl.value) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mBinding?.initImg?.visibility = VISIBLE
                        }

                        MotionEvent.ACTION_UP -> {
                            mBinding?.initImg?.visibility = GONE
                        }
                    }
                }
                return true
            }
        })

        mBinding?.imgUp?.singleClick {
            if (mModel.showUp.value) {
                mModel.showSave.postValue(false)
                mModel.showUp.postValue(false)
                mModel.showNext.postValue(true)
                mBinding?.drawBoardView?.setImageBitmap(mModel.upWaterBitmap)
                mModel.postBitmap = mModel.upBitmap
                mModel.postUrl.postValue(mModel.upUrl)
                mModel.postWaterUrl.postValue(mModel.upWaterUrl)
            }
        }
        mBinding?.imgNext?.singleClick {
            if (mModel.showNext.value) {
                mModel.showSave.postValue(false)
                mModel.showUp.postValue(true)
                mModel.showNext.postValue(false)
                mBinding?.drawBoardView?.setImageBitmap(mModel.nextWaterBitmap)
                mModel.postBitmap = mModel.nextBitmap
                mModel.postUrl.postValue(mModel.nextUrl)
                mModel.postWaterUrl.postValue(mModel.nextWaterUrl)
            }
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
        mModel.saveAiclearBean.observe(this) {
            if (it != null) {
                SPUtils.isMaterial = true
                it.recordUrl = it.getCutUrl()
                openActivity<AToolCompletionActivity>() {
                    putParcelable("task", it)
                    putBoolean("ispost", true)
                }
                finish()
            }
        }


        mModel.taskBean.observe(this) {
            if (it != null) {
                if (it.state == 2) {//生成完成
                    mModel.showUp.postValue(true)
                    if (mModel.nextBitmap != null && !mModel.showNext.value) {
                        mModel.upBitmap = mModel.nextBitmap
                        mModel.upUrl = mModel.nextUrl
                    }
                    if (mModel.nextWaterBitmap != null && !mModel.showNext.value) {
                        mModel.upWaterBitmap = mModel.nextWaterBitmap
                        mModel.upWaterUrl = mModel.nextWaterUrl
                    }

                    mModel.showSave.postValue(false)
                    mModel.showOriginacl.postValue(true)
                    Glide.with(this@AClearAiActivity).asBitmap()
                        .load(it.result.dataWater.first())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                mModel.nextWaterBitmap = resource
                                mBinding?.drawBoardView?.setImageBitmap(resource)
                                mModel.nextWaterUrl = it.result.dataWater.first()
                                mModel.postWaterUrl.postValue(it.result.dataWater.first())
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                    Glide.with(this@AClearAiActivity).asBitmap()
                        .load(it.result.mData.first())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                mModel.nextBitmap = resource
//                                mBinding?.drawBoardView?.setImageBitmap(resource)
                                mModel.postBitmap = resource
                                mModel.nextUrl = it.result.mData.first()
                                mModel.postUrl.postValue(it.result.mData.first())

                                mModel.loading.postValue(false)
                                mModel.showNext.postValue(false)

                                timerTask?.cancel()
                                timer?.cancel()
                                timer = null
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                } else if (it.state == 3) {//生成失败
                    timer?.cancel()
                    timerTask?.cancel()
                    timer = null
                    mModel.loading.postValue(false)
                    toast(it.ee)
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


//    override fun onBackPressed() {
//        eixtActivity()
//    }

    override fun onDestroy() {
        super.onDestroy()
        mModel.originalUrl = ""
        mModel.initBitmap = null
        mModel.nextBitmap = null
        mModel.upBitmap = null
        mModel.postBitmap = null
        mModel.maskBitmap = null
        mModel.nextWaterBitmap = null
        mModel.upWaterBitmap = null
        timer?.cancel()
        timerTask?.cancel()
    }

    fun eixtActivity() {
        ABaseContentDialog(
            "Adiscard photo",
            "The current changes will not be saved",
            "Discard",
            "Cancel",
            onOkData = {
                finish()
            })
            .showIgnoreState(this)
    }


//    fun loadBitmap(loadUrl: String): Bitmap {
//        Glide.with(this@AIclearActivity).asBitmap()
//            .load(loadUrl)
//            .placeholder(com.key.R.drawable.img_default_m)
//            .override(getScreenWidth(), getScreenHeight())
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onStart() {
//                    showLoading()
//                    super.onStart()
//                }
//
//                override fun onResourceReady(
//                    resource: Bitmap,
//                    transition: Transition<in Bitmap?>?
//                ) {
//                    //检查是否需要压缩
//                    mModel.originalBitmap = resource
//                    val width = resource.getWidth()
//                    val height = resource.getHeight()
//                    var inSampleSize = 1
//                    val max = 10 * 1024 * 1024
//                    while (width * height * 4 / inSampleSize > max) {
//                        inSampleSize *= 2
//                    }
//                    if (inSampleSize > 1) {
//                        val m2 = Matrix()
//                        m2.setScale(1 / inSampleSize.toFloat(), 1 / inSampleSize.toFloat())
//                        mModel.originalBitmap =
//                            Bitmap.createBitmap(resource, 0, 0, width, height, m2, false);
//                    }
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    mModel.originalBitmap = null
//                }
//            })
//    }

    /**
     * 提交图片
     */
    fun saveBitmap() {
        if (mModel.showSave.value) {
            showLoading("Uploading picture…", false)
            mModel.maskBitmap = mBinding?.drawBoardView?.maskBitmap
            mModel.uploadPicture(mModel.postBitmap, 1)
        }
    }


    fun uptadaImg() {
        if (mModel.postUrl.value.isNotEmpty()) {
            val upImg = "${mModel.postUrl.value},${mModel.postWaterUrl.value}"
            val name = TimeUtil.getFormatDate(
                System.currentTimeMillis(),
                TimeUtil.DEFAULT_DATE_TIME_DATA
            )
            mModel.saveUserGenerateRecords(upImg, name)
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