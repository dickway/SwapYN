package com.a.adapter

import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemAlikelistItemBinding
import com.face.bean.AiFaceBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick

class AExploreLikeAdapter : BaseAdapter<AiFaceBean, ItemAlikelistItemBinding>(
    R.layout.item_alikelist_item, AiFaceBean.differCallback
) {
    override fun onBindData(
        holder: BaseHolder<ItemAlikelistItemBinding>,
        binding: ItemAlikelistItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {

                imgCover.loadImage(
                    imageUrl ,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.singleClick {
                    if (mediaType!="video"){
                        ASwapActivity.jump(imgCover.context, id)
                    }else{
                        SwapFaceNewActivity.jump(imgCover.context, id)
                    }
                }
            }
        }
    }
}
