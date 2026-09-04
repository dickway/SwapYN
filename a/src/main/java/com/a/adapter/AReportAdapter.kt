package com.a.adapter

import com.a.R
import com.a.databinding.ItemAreportBinding
import com.face.bean.CollectBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class AReportAdapter : BaseAdapter<CollectBean, ItemAreportBinding>(
    R.layout.item_areport, CollectBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemAreportBinding>,
        binding: ItemAreportBinding,
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
