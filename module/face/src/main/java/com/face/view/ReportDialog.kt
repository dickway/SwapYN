package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.adapter.other.ReportAdapter
import com.face.bean.CollectBean
import com.face.databinding.DialogReportBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getStringX


class ReportDialog @JvmOverloads constructor(
    private val onReport: ((Int, String) -> Unit)? = null
) : BaseBindingDF<DialogReportBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    height = getScreenHeight() / 5 * 4,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    var isSelect = false

    var selectedNum = NotNullMutableLiveData(0)

    var adapter = ReportAdapter()

    var liseReport = mutableListOf(
        CollectBean(name = getStringX(R.string.report1)),
        CollectBean(name = getStringX(R.string.report2)),
        CollectBean(name = getStringX(R.string.report3)),
        CollectBean(name = getStringX(R.string.report4)),
        CollectBean(name = getStringX(R.string.report5))
    )

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        mBinding?.apply {
            recyclerView.adapter = adapter
            lin1.setOnClickListener {
                isSelect = !isSelect
                imgBlock.isSelected = isSelect
            }
        }
        adapter.onItemClick = { _, bean, position ->
            liseReport.find { it.name == bean?.name }?.apply {
                isSelected = !isSelected
            }
            adapter.notifyItemChanged(position)
            selectedNum.postValue(liseReport.filter { it.isSelected }.size)
        }
        adapter.submitList(liseReport)
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        bar {
//            showNavigationBar(false)
//            setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
//        }
//    }

    fun onConfirmClick() {
        var contet = ""
        liseReport.filter { it.isSelected }.forEach {
            contet = it.name + ";" + contet
        }
        var black = if (isSelect) 1 else 0
        if (contet.contains(getStringX(R.string.report2))) {
            black=1
        }

        onReport?.invoke(black, contet)
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}