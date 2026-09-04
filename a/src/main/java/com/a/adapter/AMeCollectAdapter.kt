package com.a.adapter

import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemMeCollectItemBinding
import com.bumptech.glide.Glide
import com.face.bean.CollectAMBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.singleClick

class AMeCollectAdapter : BaseAdapter<CollectAMBean, ItemMeCollectItemBinding>(
    R.layout.item_me_collect_item, CollectAMBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemMeCollectItemBinding>,
        binding: ItemMeCollectItemBinding,
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

                Glide.with(imgCover.context)
                    .load(loadUrl)
                    .placeholder(com.key.R.drawable.img_default_m)
                    .skipMemoryCache(false)
                    .override(
                        114.dp,
                        152.dp
                    )
                    .into(imgCover)
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
