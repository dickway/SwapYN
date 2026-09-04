package com.face.adapter.other

import android.graphics.drawable.AnimationDrawable
import android.view.View
import com.face.R
import com.face.bean.TargetImgBean
import com.face.databinding.ItemFramerateImgBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.singleClick


class FramerateAdapter : BaseAdapter<TargetImgBean, ItemFramerateImgBinding>(
    R.layout.item_framerate_img, TargetImgBean.differCallback
) {
    var onDeleteClick: ((Int, TargetImgBean?) -> Unit)? = null

    override fun onBindData(
        holder: BaseHolder<ItemFramerateImgBinding>,
        binding: ItemFramerateImgBinding,
        data: TargetImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isAdd=data?.name == "add"
            if (data?.name == "add") {
                val animationDrawable = imgCover2.drawable as? AnimationDrawable
                imgCover2.post {
                    animationDrawable?.start()
                }
            } else {
                imgCover.setImageBitmap(data?.mBitmap)
            }
            root.singleClick {
                onItemClick?.invoke(it, data, position)
            }
            deleteImg.singleClick {
                onDeleteClick?.invoke(position, data)
            }
        }
    }
}
