package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.databinding.ItemAtoolFaceBinding
import com.bumptech.glide.Glide
import com.face.bean.MyFaceImgBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class AUserAdapter : BaseAdapter<MyFaceImgBean, ItemAtoolFaceBinding>(
    R.layout.item_atool_face, object : DiffUtil.ItemCallback<MyFaceImgBean>() {
        override fun areItemsTheSame(oldItem: MyFaceImgBean, newItem: MyFaceImgBean): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: MyFaceImgBean,
            newItem: MyFaceImgBean
        ): Boolean {
            return false
        }
    }
) {
    var selectIndex = -1
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemAtoolFaceBinding>,
        binding: ItemAtoolFaceBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position

            Glide.with(imgCover.context)
                .load(data?.pic)
                .placeholder(com.key.R.drawable.img_default_m)
                .into(imgCover)

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
