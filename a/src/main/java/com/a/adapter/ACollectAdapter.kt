package com.a.adapter

import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemAcollectItemBinding
import com.face.bean.CollectAMBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick
import kotlin.apply
import kotlin.text.ifEmpty

class ACollectAdapter : BaseAdapter<CollectAMBean, ItemAcollectItemBinding>(
    R.layout.item_acollect_item, CollectAMBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemAcollectItemBinding>,
        binding: ItemAcollectItemBinding,
        data: CollectAMBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {

                val loadUrl = if (contentBean.mediaType == ("video")) {
                    contentBean.webpUrl.ifEmpty {
                        contentBean.imageUrl
                    }
                } else {
                    contentBean.imageWater.ifEmpty {
                        contentBean.imageUrl
                    }
                }

                imgCover.loadImage(
                    loadUrl,
                    placeholderResId = R.drawable.a_bg_load_r8
                )
                imgCover.singleClick {
                    if (contentBean.mediaType!="video"){
                        ASwapActivity.jump(imgCover.context, contentBean.mediaId)
                    }else{
                        SwapFaceNewActivity.jump(imgCover.context, contentBean.mediaId)
                    }
                }
            }
        }
    }
}
