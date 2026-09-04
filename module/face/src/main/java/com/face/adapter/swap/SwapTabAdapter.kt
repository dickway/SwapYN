package com.face.adapter.swap

import android.view.View
import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.Face
import com.face.bean.MyFaceImgBean
import com.face.databinding.ItemSwaptabItemBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth


class SwapTabAdapter : BaseAdapter<Face, ItemSwaptabItemBinding>(
    R.layout.item_swaptab_item, Face.differCallback
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
        holder: BaseHolder<ItemSwaptabItemBinding>,
        binding: ItemSwaptabItemBinding,
        data: Face?,
        position: Int
    ) {
        var isSelectBean: MyFaceImgBean? = data?.isSelectBean
        holder.binding.apply {
            isSelected = selectIndex == position

            if(isSelectBean==null){
                imgChooes.visibility=View.GONE
            }else{
                imgChooes.visibility=View.VISIBLE
                imgChooes.loadImage(
                    isSelectBean.pic,
                    placeholderResId = com.key.R.drawable.img_default_m,
                    crossfade=false
                )
            }

            imgTab.loadImage(
                data?.url,
                placeholderResId = com.key.R.drawable.img_default_m,
                crossfade=false
            )

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
