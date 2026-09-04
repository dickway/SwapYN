package com.face.adapter.explore

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemFaceItem2Binding
import com.face.ui.SwapFaceListActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick


class Explore2FaceAdapter : BaseAdapter<AiFaceBean, ItemFaceItem2Binding>(
    R.layout.item_face_item2, AiFaceBean.differCallback
) {
    override fun onBindData(
        holder: BaseHolder<ItemFaceItem2Binding>,
        binding: ItemFaceItem2Binding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {

                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
                }
//                Glide.with(imgCover.context)
//                    .load(if (mediaType == ("video")) webpUrl else imageUrl)
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 磁盘缓存
//                    .skipMemoryCache(true) // 使用内存缓存
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(imgCover)
                imgCover.loadImage(
                    if (mediaType == ("video")) webpUrl else imageUrl,
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
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) { // -1 表示 NO_POSITION
                        val list = (currentList.drop(pos) + currentList.take(pos))
                            .take(50)
                            .toMutableList()
                        SwapFaceListActivity.jump(imgCover.context, id, 0, list)
                    }
                }
            }
        }
    }
}
