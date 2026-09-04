package com.face.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.ActivityShareBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.viewmodel.activity.ShareViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.toast


class ShareActivity : BaseBindingActivity<ActivityShareBinding, ShareViewModel>(
    R.layout.activity_share,
    ShareViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            txtSm.text = String.format(
                resources.getString(R.string.share_context),
                SPUtils.addDay
            )
        }
        mModel.isShow.value= GVM.INSTANT.isShowTool.value != 0
        mModel.getUserAiMedia()
    }

    fun copyTxt() {
        if (mModel.shareBean.value?.url == "") {
            return
        }
        val cm: ClipboardManager =
            this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager //获取cm对象
        val clipData = ClipData.newPlainText("label", getString(R.string.share_txt)+mModel.shareBean.value?.url)
        cm.setPrimaryClip(clipData) //复制的文字
        toast(resources.getString(R.string.copy_s))
    }

    fun onShareClick() {
        copyTxt()

        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.share_txt) + mModel.shareBean.value?.url
        )
        sendIntent.setType("text/plain")
        try {
            startActivity(Intent.createChooser(sendIntent, ""));
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}