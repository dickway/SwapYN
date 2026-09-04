package com.face.adapter.explore

import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemBannerItemBinding
import com.face.ui.SwapFaceListActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick

class ExploreBannerAdapter : BaseAdapter<AiFaceBean, ItemBannerItemBinding>(
    R.layout.item_banner_item, AiFaceBean.differCallback
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
        holder: BaseHolder<ItemBannerItemBinding>,
        binding: ItemBannerItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        data?.apply {
            holder.binding.apply {

                imgCover.apply {
                    layoutParams.width = (getScreenWidth() * 0.50).toInt()
                    layoutParams.height = (getScreenWidth() * 0.50).toInt()
                }

//                Glide.with(imgCover.context)
//                    .load(data.bannerImg.ifEmpty { data.imageUrl })
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 磁盘缓存
//                    .skipMemoryCache(false) // 使用内存缓存
//                    .override((getScreenWidth() * 0.50).toInt(), (getScreenWidth() * 0.50).toInt())
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(imgCover)
                imgCover.loadImage(
                    data.bannerImg.ifEmpty { data.imageUrl },
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.singleClick {
                    val pos = holder.bindingAdapterPosition
                    if (pos !in currentList.indices) return@singleClick
                    val list=  (currentList.drop(pos) + currentList.take(pos)).take(50).toMutableList()
                    SwapFaceListActivity.jump(imgCover.context, id, 0, list)
                }
            }
        }
    }
}
