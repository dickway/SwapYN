package com.face.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.ui.App
import com.face.BR
import com.face.BuildConfig
import com.face.R
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.databinding.DialogVersionBinding
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast

/**
 * @author 再战科技
 * @date 2022/4/11
 * @description
 */
class VersionDialog @JvmOverloads constructor(
    private val versionData: Map<String, String?>? = null
) : BaseBindingDF<DialogVersionBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    var isClose=false

    companion object {
        //是否已检查过
        private var isChecked = false
        fun checkUpdate(
            activity: BaseBindingActivity<*, *>,
            isForce: Boolean = false,
            showToast: Boolean = false
        ) {
            if (isChecked && !isForce) {
                return
            }
            activity.launchRequestOnIO({ Repository.getSysConfig("version") }) {
                onSuccess = { data ->
                    val map = data.takeUnless { it.isNullOrEmpty() }?.toMap()
                    if (map != null) {
                        if (((if (BuildConfig.DEBUG) map["debug_version"] else map["version"])
                                ?.toInt() ?: 0) > BuildConfig.VERSION_CODE
                        ) {
                            if (!isChecked || isForce) {
                                activity.lifecycleScope.launchWhenResumed {
                                    VersionDialog(map).showIgnoreState(activity)
                                }
                                isChecked = true
                            }
                        } else if (showToast) {
                            toast(getStringX(R.string.version_hint))
                        }
                    } else if (showToast) {
                        toast(getStringX(R.string.version_hint))
                    }
                }
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        if (versionData == null) {
            dismissAllowingStateLoss()
            return
        }
        mBinding?.apply {
            versionData.let {
                it["title"].takeIf { !it.isNullOrBlank() }?.let {
                    tvTitle.text = it
                }
                it["cancel_text"].takeIf { !it.isNullOrBlank() }?.let {
                    btnCancel.text = it
                }
                it["confirm_text"].takeIf { !it.isNullOrBlank() }?.let {
                    btnConfirm.text = it
                }
//                it["hide_confirm_btn"].takeIf { it == "1" }?.let {
//                    btnConfirm.isVisible = false
//                }
                it["notification_update"].takeIf { !it.isNullOrBlank() }?.let {
                    isClose = it=="1"
                }

                tvContent.text = it["content"]
                (it["force_update"] == "0").let { notForce ->
                    btnCancel.isVisible = notForce
                    isCancelable = false
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun onConfirmClick() {
        if (versionData == null||isClose) {
            dismissAllowingStateLoss()
            return
        }
        var intent: Intent? = null
        if (!versionData["uri"].isNullOrBlank()) {
            intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(versionData["uri"])
                setPackage("com.android.vending")
            }
        }
        if (!versionData["url"].isNullOrBlank()) {
            if (intent?.resolveActivity(App.INSTANCE.packageManager) == null) {
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(versionData["url"])
            }
        }
        try {
            if (intent != null) {
                startActivity(intent)
            }else{
                dismissAllowingStateLoss()
            }
        } catch (e: Exception) {
            LogUtils.e(e)
        }
        EventUtil.updateApp(versionData["version"]?.toInt() ?: 0)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}