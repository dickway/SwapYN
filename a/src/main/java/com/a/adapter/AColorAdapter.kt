package com.a.adapter

import android.graphics.Color
import com.a.R
import com.a.databinding.ItemAtoolBackgroundBinding
import com.face.bean.ColorBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class AColorAdapter : BaseAdapter<ColorBean, ItemAtoolBackgroundBinding>(
    R.layout.item_atool_background, ColorBean.differCallback
) {
    var selectIndex = -1
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemAtoolBackgroundBinding>,
        binding: ItemAtoolBackgroundBinding,
        data: ColorBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            if (selectIndex == position) {
                imgCover.alpha = 1f
            } else {
                imgCover.alpha = 0.5f
            }
            imgCover.setBackgroundColor(Color.parseColor(data?.color))
            tvName.text = data?.name
            root.setOnClickListener {
//                selectIndex = if (selectIndex == position) -1 else position
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
