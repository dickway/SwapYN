package com.a.adapter

import android.view.View
import com.a.R
import com.a.databinding.ItemAhistoryItemBinding
import com.face.bean.TaskBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick
import kotlin.apply
import kotlin.collections.isNotEmpty
import kotlin.text.ifEmpty
import kotlin.text.isEmpty


class AHistoryAdapter : BaseAdapter<TaskBean, ItemAhistoryItemBinding>(
    R.layout.item_ahistory_item, TaskBean.differCallback
) {
    var onLongClick: ((TaskBean?) -> Unit)? = null

    override fun onBindData(
        holder: BaseHolder<ItemAhistoryItemBinding>,
        binding: ItemAhistoryItemBinding,
        data: TaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
//                if (media.width >= media.height) {//根据宽高大小设置图片正方形 还是4：3
//
//                } else {
//                    cardView.layoutParams.height = (getScreenWidth() - 48.dp) / 3 * 145 / 109
//                }
                cardView.layoutParams.width = (getScreenWidth() - 48.dp) / 3
                var img = if (media.mediaType == ("video")) result.dataWebp else {
                    if (result.dataWater.isNotEmpty()) result.dataWater[0] else result.dataWebp
                }

                if (img.isEmpty()) {
                    img = media.webpUrl.ifEmpty { media.imageUrl }
                }


                if (state == 0 || state == 1) {
                    txtBg.visibility = View.VISIBLE
                } else {
                    txtBg.visibility = View.GONE
                }

                imgSate.visibility = View.GONE
                if (state == 3) {
                    imgSate.visibility = View.VISIBLE
                }


                imgCover.loadImage(
                    img,
                    placeholderResId = R.drawable.a_bg_load_r8
                )

                imgCover.setOnLongClickListener {
                    onLongClick?.invoke(data)
                    true
                }

                imgDelete.singleClick {
                    onLongClick?.invoke(data)
                }

                imgCover.singleClick {
                    onItemClick?.invoke(imgCover, data, position)
                }
            }
        }
    }
}
