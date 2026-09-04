package com.face.ui.gift

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.bean.CaskBean
import com.face.databinding.ActivityCashDetailsBinding
import com.face.ui.BaseBindingActivity
import com.face.util.GVM
import com.face.viewmodel.activity.NullViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.toast
import kotlin.getValue


class CashDetailsActivity : BaseBindingActivity<ActivityCashDetailsBinding, NullViewModel>(
    R.layout.activity_cash_details,
    NullViewModel::class.java
) {
    val giftData by intentExtras("gift", CaskBean())


    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            txtC.text = giftData.cardNo
            txtP.text = giftData.cardPaswd
            txtPrice.text = giftData.point.toString()
            txtTime.text = TimeUtil.getFormatDate(giftData.useTime, "dd/MM/yyyy HH:mm:ss")
        }
    }


    fun copyTxt(type: Int) {
        val txtCopy = if (type == 1) giftData.cardNo else giftData.cardPaswd
        val cm: ClipboardManager =
            this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager //获取cm对象
        val clipData = ClipData.newPlainText("label", txtCopy)
        cm.setPrimaryClip(clipData) //复制的文字

        val txtToast = if (type == 1)resources.getString(R.string.copy_s1)else resources.getString(R.string.copy_s2)
        toast(txtToast)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}