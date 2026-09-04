package com.face.adapter.explore

import android.view.View
import com.face.R
import com.face.bean.ToolTypeBean
import com.face.databinding.ItemToolTypeBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage

class ToolTypeAdapter : BaseAdapter<ToolTypeBean, ItemToolTypeBinding>(
    R.layout.item_tool_type, ToolTypeBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemToolTypeBinding>,
        binding: ItemToolTypeBinding,
        data: ToolTypeBean?,
        position: Int
    ) {
        holder.binding.apply {
            tvName.text = data?.getName()
            imgToolVideo.loadImage(data?.urlIcon)
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
