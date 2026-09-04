package com.face.adapter.other

import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.TargetBean
import com.face.databinding.ItemTargetVideoBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick

class TargetVideoAdapter : BaseAdapter<TargetBean, ItemTargetVideoBinding>(
    R.layout.item_target_video, TargetBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemTargetVideoBinding>,
        binding: ItemTargetVideoBinding,
        data: TargetBean?,
        position: Int
    ) {
        holder.binding.apply {
            imgTarget.layoutParams.width = (getScreenWidth() - 64.dp) / 3
            data?.apply {
                isShow = isSelect
                Glide.with(imgTarget.context)
                    .load(path)
                    .placeholder(com.key.R.drawable.img_default_m)
                    .into(imgTarget)
            }
            root.singleClick {
                data?.isSelect = !(data?.isSelect ?: false)
                notifyItemChanged(position)
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
