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
            // Localize the visible label while retaining the existing report payload values.
            val labelRes = when (data?.name) {
                "Nudity or sexual" -> com.face.R.string.report1
                "Hateful or abusive" -> R.string.a_report_hateful
                "Violence scene" -> com.face.R.string.report3
                "Political issue" -> com.face.R.string.report4
                "Alleged CopyrightInfr ingement" -> com.face.R.string.report5
                else -> null
            }
            tvContent.text = labelRes?.let(root.resources::getString) ?: data?.name
            isSelected=data?.isSelected
            root.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
