package com.face.adapter.other

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.MyFaceImgBean
import com.face.databinding.ItemAipaperworkFaceBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage


class UserAdapter : BaseAdapter<MyFaceImgBean, ItemAipaperworkFaceBinding>(
    R.layout.item_aipaperwork_face, object : DiffUtil.ItemCallback<MyFaceImgBean>() {
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
        holder: BaseHolder<ItemAipaperworkFaceBinding>,
        binding: ItemAipaperworkFaceBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position

            imgCover.loadImage(
                data?.pic,
                placeholderResId = com.key.R.drawable.img_default_m
            )
//            Glide.with(imgCover.context)
//                .load(data?.pic)
//                .placeholder(com.key.R.drawable.img_default_m)
//                .into(imgCover)

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
