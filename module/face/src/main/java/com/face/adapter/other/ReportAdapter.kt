package com.face.adapter.other

import com.face.R
import com.face.bean.CollectBean
import com.face.databinding.ItemReportBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class ReportAdapter : BaseAdapter<CollectBean, ItemReportBinding>(
    R.layout.item_report, CollectBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemReportBinding>,
        binding: ItemReportBinding,
        data: CollectBean?,
        position: Int
    ) {
        holder.binding.apply {
            tvContent.text = data?.name
            isSelected=data?.isSelected
            root.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
