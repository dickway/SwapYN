package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.databinding.ItemAswapItemBinding
import com.face.bean.MyFaceImgBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage


class ASwapAdapter : BaseAdapter<MyFaceImgBean, ItemAswapItemBinding>(
    R.layout.item_aswap_item, object : DiffUtil.ItemCallback<MyFaceImgBean>() {
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

    var onAddClick: ((MyFaceImgBean?) -> Unit)? = null

    var canDisableFace = false
        set(value) {
            if (value == field) return
            field = value
            currentList.forEachIndexed { index, face ->
                if (face.isSelect) notifyItemChanged(index)
            }
        }

    var selectIndex = -1
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                if (origin in 0 until itemCount) notifyItemChanged(origin)
                if (field in 0 until itemCount) notifyItemChanged(field)
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemAswapItemBinding>,
        binding: ItemAswapItemBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isAdd = data?.isSelect
            isSelected = selectIndex == position
            isDisableEnabled = canDisableFace
            root.isEnabled = data?.isSelect != true || canDisableFace
            imgCover.loadImage(
                data?.pic
            )
            imgCover.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
            addImg.setOnClickListener {
                if (canDisableFace) onItemClick?.invoke(it, data, position)
            }
        }
    }
}
