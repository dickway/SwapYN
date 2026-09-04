package com.face.adapter.myface

import com.face.bean.MyFaceImgBean
import com.face.R
import com.face.databinding.ItemMyallfaceHItemBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage

class MyAllFaceHAdapter : BaseAdapter<MyFaceImgBean, ItemMyallfaceHItemBinding>(
    R.layout.item_myallface_h_item, MyFaceImgBean.differCallback
) {
    override fun onBindData(
        holder: BaseHolder<ItemMyallfaceHItemBinding>,
        binding: ItemMyallfaceHItemBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            imgCover.loadImage(
                data?.pic,
                placeholderResId = com.key.R.drawable.img_default_m
            )
        }
    }


}
