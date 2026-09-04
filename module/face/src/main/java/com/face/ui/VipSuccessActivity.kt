package com.face.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.databinding.ActivityVipSuccessBinding
import com.face.util.SPUtils
import com.face.viewmodel.activity.VipSuccessViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.longToast


class VipSuccessActivity : BaseBindingActivity<ActivityVipSuccessBinding, VipSuccessViewModel>(
    R.layout.activity_vip_success,
    VipSuccessViewModel::class.java
) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            if (SPUtils.txtWhatapp == "1") {
                whatapp.visibility = View.GONE
                tvWhats.visibility = View.GONE
            }
            if (SPUtils.txtTelegram == "1") {
                telegram.visibility = View.GONE
                tvTelegram.visibility = View.GONE
            }
            if (SPUtils.txtDiscord == "1") {
                discord.visibility = View.GONE
                tvDiscord.visibility = View.GONE
            }
        }

    }

    fun onWhatapp() {
        openThree(SPUtils.txtWhatapp, "com.whatsapp")
    }

    fun onTelegram() {
        openThree(SPUtils.txtTelegram, "org.telegram.messenger")
    }

    fun onDiscord() {
        openThree(SPUtils.txtDiscord, "com.discord")
    }

    fun openThree(txtLine: String, txtPackage: String) {
        txtLine.takeIf { it.isNotBlank() }?.let {
            var intent = Intent(Intent.ACTION_VIEW, it.toUri()).apply {
                setPackage(txtPackage)
            }
            if (intent.resolveActivity(App.INSTANCE.packageManager) != null) {
                startActivity(intent)
            } else {
                intent = Intent(Intent.ACTION_VIEW, it.toUri())
                try {
                    startActivity(intent)
                } catch (t: Throwable) {
                    longToast(getString(R.string.telegram_group_info))
                }
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}