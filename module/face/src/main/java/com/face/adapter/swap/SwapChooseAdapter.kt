package com.face.adapter.swap

import androidx.recyclerview.widget.DiffUtil
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.MyFaceImgBean
import com.face.databinding.ItemSwapchooseItemBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage


class SwapChooseAdapter : BaseAdapter<MyFaceImgBean, ItemSwapchooseItemBinding>(
    R.layout.item_swapchoose_item, object : DiffUtil.ItemCallback<MyFaceImgBean>() {
        override fun areItemsTheSame(oldItem: MyFaceImgBean, newItem: MyFaceImgBean): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: MyFaceImgBean, newItem: MyFaceImgBean): Boolean {
            return oldItem.isSelect!=newItem.isSelect
        }
    }
) {

    var selectIndex = 0
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemSwapchooseItemBinding>,
        binding: ItemSwapchooseItemBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {

        holder.binding.apply {
            txtNA.text = data?.type
            isSelected = selectIndex == position

            imgCover.loadImage(
                data?.pic,
                placeholderResId = com.key.R.drawable.img_default_m
            )

            root.setOnClickListener {
                selectIndex = if (selectIndex == position) -1 else position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
