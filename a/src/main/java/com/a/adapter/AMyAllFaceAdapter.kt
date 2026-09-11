package com.a.adapter

import com.a.R
import com.a.databinding.ItemAmyallfaceItemBinding
import com.face.bean.MyFaceImgBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick


class AMyAllFaceAdapter : BaseAdapter<MyFaceImgBean, ItemAmyallfaceItemBinding>(
    R.layout.item_amyallface_item, MyFaceImgBean.differCallback
) {

    var showDete = false

    var onAddClick: ((MyFaceImgBean?) -> Unit)? = null

    var onDeleteClick: ((MyFaceImgBean?) -> Unit)? = null

    fun showDelect(isb: Boolean) {
        showDete = isb
        notifyDataSetChanged()
    }


    override fun onBindData(
        holder: BaseHolder<ItemAmyallfaceItemBinding>,
        binding: ItemAmyallfaceItemBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isAdd = data?.isSelect
            isDelete = showDete && data?.isSelect == false
            imgCover.loadImage(
                data?.pic,
                placeholderResId = com.key.R.drawable.img_default_m
            )
            addImg.singleClick {
                onAddClick?.invoke(data)
            }
            imgDelete.singleClick {
                onDeleteClick?.invoke(data)
            }
        }
    }

}
