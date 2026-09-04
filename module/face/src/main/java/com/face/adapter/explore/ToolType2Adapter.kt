package com.face.adapter.explore

import android.view.View
import com.face.R
import com.face.bean.ToolTypeBean
import com.face.databinding.ItemToolType2Binding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage

class ToolType2Adapter : BaseAdapter<ToolTypeBean, ItemToolType2Binding>(
    R.layout.item_tool_type2, ToolTypeBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemToolType2Binding>,
        binding: ItemToolType2Binding,
        data: ToolTypeBean?,
        position: Int
    ) {
        holder.binding.apply {
            tvName.text = data?.getName()
            imgTool.loadImage(data?.urlImg)
            imgNew.visibility = if (data?.isNew == true) {
                View.VISIBLE
            } else {
                View.GONE
            }

            root.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
