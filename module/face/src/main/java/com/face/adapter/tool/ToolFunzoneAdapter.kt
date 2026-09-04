package com.face.adapter.tool

import android.view.View
import com.face.R
import com.face.bean.ToolFunzoneBean
import com.face.bean.ToolTypeBean
import com.face.databinding.ItemToolFunzoneBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage

class ToolFunzoneAdapter : BaseAdapter<ToolFunzoneBean, ItemToolFunzoneBinding>(
    R.layout.item_tool_funzone, ToolFunzoneBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemToolFunzoneBinding>,
        binding: ItemToolFunzoneBinding,
        data: ToolFunzoneBean?,
        position: Int
    ) {
        holder.binding.apply {
            toolName.text = data?.getName()
            toolDesc.text = data?.getDesc()
            imgNew.visibility = if (data?.isNew == true) {
                View.VISIBLE
            } else {
                View.GONE
            }
            txtKissV.loadImage(data?.urlImg)
        }
    }
}
