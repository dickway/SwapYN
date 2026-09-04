package com.face.adapter.explore

import android.view.View
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemLikelistItemBinding
import com.face.ui.SwapFaceListActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick

class ExploreLikeAdapter(val tag: String? = "") : BaseAdapter<AiFaceBean, ItemLikelistItemBinding>(
    R.layout.item_likelist_item, AiFaceBean.differCallback
) {
    override fun onBindData(
        holder: BaseHolder<ItemLikelistItemBinding>,
        binding: ItemLikelistItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {

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
                    if (pos !in currentList.indices) return@singleClick
                    val list =
                        (currentList.drop(pos) + currentList.take(pos)).take(50).toMutableList()
                    SwapFaceListActivity.jump(imgCover.context, id, 0, list)
                }
            }
        }
    }
}
