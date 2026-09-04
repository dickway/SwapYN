package com.a.adapter

import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemMeHistoryItemBinding
import com.bumptech.glide.Glide
import com.face.bean.AiFaceBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.singleClick

class AMeHistoryAdapter : BaseAdapter<AiFaceBean, ItemMeHistoryItemBinding>(
    R.layout.item_me_history_item, AiFaceBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemMeHistoryItemBinding>,
        binding: ItemMeHistoryItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {

            data?.apply {
                val url =  webpUrl.ifEmpty { imageUrl }
                Glide.with(imgCover.context)
                    .load(url)
                    .placeholder(com.key.R.drawable.img_default_m)
                    .skipMemoryCache(false)
                    .override(
                        114.dp,
                        114.dp
                    )
                    .into(imgCover)
//                imgCover.loadImage(
//                    imageUrl,
//                    placeholderResId =com.key.R.drawable.img_default_m
//                )
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
