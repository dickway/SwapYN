package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.adapter.AReportAdapter
import com.a.databinding.DialogAreportBinding
import com.face.util.GVM
import com.face.BR
import com.face.bean.CollectBean
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.toast


class AReportDialog @JvmOverloads constructor(
    private val onReport: ((Int, String) -> Unit)? = null
) : BaseBindingDF<DialogAreportBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false
) {
    var isSelect = false

    var selectedNum = NotNullMutableLiveData(0)

    var adapter = AReportAdapter()

    var liseReport = mutableListOf(
        CollectBean(name = "Nudity or sexual"),
        CollectBean(name = "Political issue"),
        CollectBean(name = "Hateful or abusive"),
        CollectBean(name = "Violence scene"),
        CollectBean(name = "Alleged CopyrightInfr ingement")
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

    fun onConfirmClick() {
        var contet = ""
        liseReport.filter { it.isSelected }.forEach {
            contet = it.name + ";" + contet
        }
        val black = if (isSelect) 1 else 0
        if (contet.isNotEmpty()){
            onReport?.invoke(black, contet)
            dismissAllowingStateLoss()
        }else{
            toast("Please select")
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}