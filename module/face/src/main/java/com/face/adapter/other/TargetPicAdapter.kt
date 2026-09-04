package com.face.adapter.other

import com.face.R
import com.face.bean.TargetImgBean
import com.face.databinding.ItemTargetPicBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth

class TargetPicAdapter : BaseAdapter<TargetImgBean, ItemTargetPicBinding>(
    R.layout.item_target_pic, TargetImgBean.differCallback
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
        holder: BaseHolder<ItemTargetPicBinding>,
        binding: ItemTargetPicBinding,
        data: TargetImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                isShow = selectIndex == position
                if (data.mBitmap != null){
                    imgTarget.setImageBitmap(data.mBitmap);
                    imgTarget.apply {
                        layoutParams.width = (getScreenWidth() - 64.dp) / 3
                        layoutParams.height =
                            (getScreenWidth() - 64.dp) / 3 * data.mBitmap.height / data.mBitmap.width
                    }
                }
            }
            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
