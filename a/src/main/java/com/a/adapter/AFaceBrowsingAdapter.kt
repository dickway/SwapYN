package com.a.adapter

import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemAbrowsingItemBinding
import com.face.bean.AiFaceBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick
import kotlin.apply
import kotlin.text.ifEmpty

class AFaceBrowsingAdapter : BaseAdapter<AiFaceBean, ItemAbrowsingItemBinding>(
    R.layout.item_abrowsing_item, AiFaceBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemAbrowsingItemBinding>,
        binding: ItemAbrowsingItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                val url =  webpUrl.ifEmpty { imageUrl }
                imgCover.loadImage(
                    url,
                    placeholderResId = R.drawable.a_bg_load_r8
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
