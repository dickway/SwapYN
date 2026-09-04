package com.a.adapter

import android.view.View
import com.a.R
import com.a.databinding.ItemMeWorkItemBinding
import com.face.bean.TaskBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick

class AMeWorkAdapter : BaseAdapter<TaskBean, ItemMeWorkItemBinding>(
    R.layout.item_me_work_item, TaskBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemMeWorkItemBinding>,
        binding: ItemMeWorkItemBinding,
        data: TaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                var img =
                    if (result.dataWater.isNotEmpty()) result.dataWater[0] else result.dataWebp
                if (state == 0 || state == 1) {
                    img = media.imageUrl
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
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.singleClick {
                    onItemClick?.invoke(imgCover, data, position)
                }
            }
        }
    }
}
