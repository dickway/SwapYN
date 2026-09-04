package com.face.adapter.other

import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.TargetBean
import com.face.databinding.ItemTargetBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.singleClick

class TargetAdapter : BaseAdapter<TargetBean, ItemTargetBinding>(
    R.layout.item_target, TargetBean.differCallback
) {
    var onDeleteClick: ((Int, TargetBean?) -> Unit)? = null
    var onAddClick: ((Int) -> Unit)? = null


    override fun onBindData(
        holder: BaseHolder<ItemTargetBinding>,
        binding: ItemTargetBinding,
        data: TargetBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                isShow = isAdd
                Glide.with(imgTarget.context)
                    .load(path)
                    .placeholder(com.key.R.drawable.img_default_m)
                    .into(imgTarget)
            }
            deleteImg.singleClick {
                onDeleteClick?.invoke(position, data)
            }
            addImg.singleClick {
                onAddClick?.invoke(position)
            }
        }
    }
}
