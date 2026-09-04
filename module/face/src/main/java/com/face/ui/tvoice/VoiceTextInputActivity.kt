package com.face.ui.tvoice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.face.BR
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityVoiceTextinputBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.viewmodel.activity.VoiceTextInputViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.KeyboardUtil
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.toIntOrZero


class VoiceTextInputActivity :
    BaseBindingActivity<ActivityVoiceTextinputBinding, VoiceTextInputViewModel>(
        R.layout.activity_voice_textinput,
        VoiceTextInputViewModel::class.java
    ) {
    private val dataBean by intentExtras("dataBean", ToolTaskBean())
    override fun init(savedInstanceState: Bundle?) {

        val hintLanguage = when (SPbaseUtils.spLanguage) {
            "zh" -> "请输入中文"
            "es" -> "Escribe en español"
            else -> getString(R.string.enter_voice)
        }
        mBinding?.edit?.hint = String.format(
            getString(R.string.please_enter),
            hintLanguage
        )
        mBinding?.tvNumSize?.text ="/${SPUtils.readNumSize}"
        mBinding?.edit?.maxEms=SPUtils.readNumSize.toIntOrZero()

        mBinding?.edit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mModel.inputNum.postValue(s?.length ?: 0)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        mBinding?.edit?.run {
            isFocusable = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    mBinding?.edit?.windowToken?.let { token ->
                        KeyboardUtil.hideKeyboard(token)
                    }
                }
                false
            }
            WindowCompat.getInsetsController(window, this).show(WindowInsetsCompat.Type.ime())
        }

        mModel.completionTaskBean.observe(this) {
            if (it.taskId != "") {
                openActivity<VoiceDubActivity> {
                    putParcelable("dataBean", it)
                }
                finish()
            }
        }

    }

    override fun onStop() {
        super.onStop()
        mBinding?.apply {
            KeyboardUtil.hideKeyboard(edit.windowToken)
        }
    }

    fun dubVoice() {
        var inputTxt = mBinding?.edit?.text ?: ""
        if (inputTxt.isNotEmpty()) {
            val language = when (SPbaseUtils.spLanguage) {
                "zh" -> "zh-CN"
                "es" -> "es-ES"
                else -> "en-US"
            }
            //souces里面用 | 分隔，第一个原始链接，分析那个任务会返回，第二个是文本，第三个是语言
            mModel.sources =
                dataBean.getSource() + "|" + inputTxt + "|" + language+ "|" +mModel.typeVoice
            mModel.sendAiTask()
        }
    }


    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
