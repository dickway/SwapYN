package com.face.adapter.other

import com.face.R
import com.face.bean.GiftBean
import com.face.databinding.ItemGiftItemBinding
import com.face.ui.gift.CashActivity
import com.face.ui.gift.CashDetailsActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


open class GiftAdapter() : BaseAdapter<GiftBean, ItemGiftItemBinding>(
    R.layout.item_gift_item, GiftBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemGiftItemBinding>,
        binding: ItemGiftItemBinding,
        data: GiftBean?,
        position: Int
    ) {
        binding.apply {
            data?.apply {
                tvName.text= getName()
                tvPointZ.text= infoBean.aipoints.toString()
                img.loadImage(
                    infoBean.img,
                    placeholderResId = com.key.R.drawable.img_default_m
                )
                root.singleClick {
                    root.context.openActivity<CashActivity>() {
                        putParcelable("gift", infoBean)
                    }
                }
            }
        }
    }

}
