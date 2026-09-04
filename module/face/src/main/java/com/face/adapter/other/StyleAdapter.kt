package com.face.adapter.other

import com.face.R
import com.face.bean.StyleBean
import com.face.databinding.ItemStyleHBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.SPbaseUtils


class StyleAdapter : BaseAdapter<StyleBean, ItemStyleHBinding>(
    R.layout.item_style_h, StyleBean.differCallback
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
        holder: BaseHolder<ItemStyleHBinding>,
        binding: ItemStyleHBinding,
        data: StyleBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            when (SPbaseUtils.spLanguage) {
                "zh" -> tvName.text = data?.nameCN
                "es" -> tvName.text = data?.nameES
                "de" -> tvName.text = data?.nameDE
                "fr" -> tvName.text = data?.nameFR
                "sv" -> tvName.text = data?.nameSV
                "ar" -> tvName.text = data?.nameAr
                "ja" -> tvName.text = data?.nameJa
                "ko" -> tvName.text = data?.nameKo
                "ku" -> tvName.text = data?.nameKu
                "fa" -> tvName.text = data?.nameFa
                "iw" -> tvName.text = data?.nameIw
                "tr" -> tvName.text = data?.nameTr
                "zh-tw" -> tvName.text = data?.nameZhTW
                "pt-br" -> tvName.text = data?.namePtBR
                "pt-pt" -> tvName.text = data?.namePt
                "it" -> tvName.text = data?.nameIt
                else -> tvName.text = data?.nameEN
            }

            imgCover.loadImage(
                data?.url,
                placeholderResId = com.key.R.drawable.img_default_m
            )

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
