package com.face.adapter.other

import android.view.View
import com.face.bean.VipSchemeBean
import com.face.R
import com.face.databinding.ItemBuypointBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class BuyAdapter : BaseAdapter<VipSchemeBean, ItemBuypointBinding>(
    R.layout.item_buypoint, VipSchemeBean.differCallback
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
        holder: BaseHolder<ItemBuypointBinding>,
        binding: ItemBuypointBinding,
        data: VipSchemeBean?,
        position: Int
    ) {
        data?.let {
            binding.apply {
                isSelected = selectIndex == position
                if (it.remark.isEmpty()){//为空表示不显示折扣
                    tvPrice.visibility= View.GONE
                }else{
                    tvPrice.visibility= View.VISIBLE
                }
                if (it.type==2){//不是折扣商品
                    tvPrice.text ="${tvPrice.context.getString(R.string.only_price)} ${it.showCode} ${it.getBeanPrice()}"
                    tv3.text ="${it.showCode} ${it.oldShowPrice}"
                }else{
                    tvPrice.text ="${tvPrice.context.getString(R.string.only_price)} ${it.showCode} ${it.oldShowPrice}"
                    tv3.text ="${it.showCode} ${it.getBeanPrice()}"
                }
                tvPointNum.text = it.day.toString()
            }
        }
    }

}