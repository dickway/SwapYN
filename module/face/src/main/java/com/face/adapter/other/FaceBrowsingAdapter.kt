package com.face.adapter.other

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDownsampler
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemFaceItemBinding
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick

class FaceBrowsingAdapter : BaseAdapter<AiFaceBean, ItemFaceItemBinding>(
    R.layout.item_face_item, AiFaceBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemFaceItemBinding>,
        binding: ItemFaceItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                imgCover.layoutParams.width = (getScreenWidth() - 56.dp) / 3


                val url =  webpUrl.ifEmpty { imageUrl }

//                Glide.with(imgCover.context)
//                    .load(url)
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 磁盘缓存
//                    .skipMemoryCache(true) // 使用内存缓存
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(imgCover)
                imgCover.loadImage(
                    url,
                    placeholderResId = com.key.R.drawable.img_default_m
                )
                imgVideo.visibility =
                    if (mediaType == "video") View.VISIBLE else View.GONE
                if (pro == 0) {
                    imgPro.visibility = View.GONE
                } else {
                    imgPro.visibility = View.VISIBLE
                }
                imgCover.singleClick {
                    SwapFaceNewActivity.jump(imgCover.context, id)
                }
            }
        }
    }
}
