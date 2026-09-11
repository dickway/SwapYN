package com.face.view

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.databinding.DialogDiscountBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import java.util.Locale


class DiscountDialog @JvmOverloads constructor(
    private val unit: String = "",
    private val price: String = "",
    private val oldPrice: String = "",
    private val day: String = "",
    private val onConfirm: (() -> Unit)? = null,
    private val onDismiss: (() -> Unit)? = null
) : BaseBindingDF<DialogDiscountBinding>(
    horizontalPadding = 52.dp,
    maxWidth = 300.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    var time1 = MutableLiveData("03")
    var time2 = MutableLiveData("00")
    var time3 = MutableLiveData("00")

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        mBinding?.apply {
            tvUnit.text = unit
            tvPrice.text = price
            tvDay.text = day
            tvOldPrice.text = "$unit$oldPrice$day"
        }
        val timer = object : CountDownTimer(3 * 60 * 1000, 10) { // 3分钟，间隔10ms
            override fun onTick(millisUntilFinished: Long) {
                time1.postValue(
                    String.format(
                        Locale.CHINESE,
                        "%02d",
                        millisUntilFinished / 1000 / 60
                    )
                )  // 分钟
                time2.postValue(
                    String.format(
                        Locale.CHINESE,
                        "%02d",
                        millisUntilFinished / 1000 % 60
                    )
                )  // 秒
                time3.postValue(
                    String.format(
                        Locale.CHINESE,
                        "%02d",
                        millisUntilFinished % 1000 / 10
                    )
                )  // 毫秒（保留两位）
            }

            override fun onFinish() {
                time1.postValue("00")  // 分钟
                time2.postValue("00")  // 秒
                time3.postValue("00")  // 毫秒（保留两位）
            }
        }
        timer.start()  // 启动倒计时
    }

    fun onConfirmClick() {
        onConfirm?.invoke()
        dismissAllowingStateLoss()
    }

    fun onDismissClick() {
        onDismiss?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}