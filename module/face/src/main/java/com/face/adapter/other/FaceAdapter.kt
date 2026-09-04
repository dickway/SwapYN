package com.face.adapter.other

import android.view.View
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemFaceItemBinding
import com.face.ui.SwapFaceListActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick


open class FaceAdapter(val tag: String? = "") : BaseAdapter<AiFaceBean, ItemFaceItemBinding>(
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
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
                }

                imgCover.loadImage(
                    if (mediaType == ("video")) webpUrl else imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgVideo.visibility =
                    if (mediaType == "video") View.VISIBLE else View.GONE

                imgPro.visibility = if (pro == 0) View.GONE else View.VISIBLE

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
