package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.face.BR
import com.face.R
import com.face.databinding.DialogMianToolBinding
import com.face.ui.tcustom.CustomStartActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getScreenWidth


class MainToolDialog @JvmOverloads constructor(
    private val Type: String = "",
) : BaseBindingDF<DialogMianToolBinding>(
    width = getScreenWidth(),
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true,
) {
    var checked = NotNullMutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.apply {
            if (Type == "image") {
                dialogTitle.text = getString(R.string.dialog_tool_title)
                dialogTxt1.text = getString(R.string.dialog_tool_txt1)
                dialogTxtVideo.visibility = View.GONE
            } else {
                dialogTitle.text = getString(R.string.dialog_tool_title_video)
                dialogTxt1.text = getString(R.string.dialog_tool_txt1_video)
                dialogTxtVideo.visibility = View.VISIBLE
            }
        }

    }
    fun onOkClick() {
        if (Type == "image") {
            SPUtils.toolPicHint= checked.value == false
        }else{
            SPUtils.toolVideoHint= checked.value == false
        }
        CustomStartActivity.jump(mActivity,Type)
        dismissAllowingStateLoss()
    }
    fun onCheckboxClick() {
        checked.value = !checked.value
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}