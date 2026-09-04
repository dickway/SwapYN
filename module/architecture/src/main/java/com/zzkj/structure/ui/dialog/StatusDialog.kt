package com.zzkj.structure.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.zzkj.structure.R
import com.zzkj.structure.databinding.DialogStatusBinding
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getStringX
import java.util.*
import kotlin.concurrent.schedule

/**
 * @author lmk
 * @date 2022/2/18
 * @description
 */
class StatusDialog(context: Context) : Dialog(context) {
    private var binding: DialogStatusBinding? = null
    private var text = ""
    private var imgResId = 0
    private var showLoadingImg = false
    private var autoDismissDelay = 0
    private var schedule: TimerTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogStatusBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding?.text = text
        binding?.imgResId = imgResId
        binding?.showLoadingImg = showLoadingImg
    }

    override fun onStart() {
        super.onStart()
        setWindowSize()
    }

    private fun setWindowSize() {
        window?.apply {
            attributes.width = 120.dp
            attributes.height = 120.dp
            attributes.dimAmount = 0f // 设置黑暗度
            attributes = attributes
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun setText(resId: Int) = apply {
        text = getStringX(resId)
        binding?.text = text
    }

    fun setText(text: String) = apply {
        this.text = text
        binding?.text = text
    }

    fun setAutoDismiss(delayMs: Int) = apply {
        autoDismissDelay = delayMs
        if (autoDismissDelay > 0) {
            setOnDismissListener {
                schedule?.cancel()
            }
        } else {
            setOnDismissListener(null)
        }
    }

    fun setImgResId(resId: Int) = apply {
        imgResId = resId
        showLoadingImg = false
        binding?.imgResId = imgResId
        binding?.showLoadingImg = showLoadingImg
    }

    fun setError(textResId: Int = R.string.fail) = setError(getStringX(textResId))

    fun setError(text: String = getStringX(R.string.fail)) = apply {
        setImgResId(R.mipmap.img_status_error)
        setText(text)
        setAutoDismiss(1500)
    }


    fun setSuccess(textResId: Int = R.string.success) = setSuccess(getStringX(textResId))

    fun setSuccess(text: String = getStringX(R.string.success)) = apply {
        setImgResId(R.mipmap.img_status_success)
        setText(text)
        setAutoDismiss(1500)
    }


    fun setWarning(textResId: Int) = setWarning(getStringX(textResId))

    fun setWarning(text: String) = apply {
        setImgResId(R.mipmap.img_status_warning)
        setText(text)
        setAutoDismiss(1500)
    }


    fun setLoading(textResId: Int = R.string.loading) = setLoading(getStringX(textResId))

    fun setLoading(text: String?) = apply {
        setImgResId(0)
        setText(text ?: getStringX(R.string.loading))
        showLoadingImg = true
    }

    override fun show() {
        super.show()
        if (autoDismissDelay > 0) {
            schedule = Timer().schedule(autoDismissDelay.toLong()) {
                dismiss()
            }
        }
    }
}