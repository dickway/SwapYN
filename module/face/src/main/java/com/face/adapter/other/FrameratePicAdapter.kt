package com.face.adapter.other

import com.face.R
import com.face.bean.TargetBean
import com.face.bean.TargetImgBean
import com.face.databinding.ItemFrameratePicBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick

class FrameratePicAdapter : BaseAdapter<TargetBean, ItemFrameratePicBinding>(
    R.layout.item_framerate_pic, TargetBean.differCallback
) {


    override fun onBindData(
        holder: BaseHolder<ItemFrameratePicBinding>,
        binding: ItemFrameratePicBinding,
        data: TargetBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                isShow = isSelect
                imgTarget.loadImage(path, placeholderResId = com.key.R.drawable.img_default_m)
            }
            root.singleClick{
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
