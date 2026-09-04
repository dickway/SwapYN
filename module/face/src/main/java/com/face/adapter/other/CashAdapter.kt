package com.face.adapter.other

import com.face.R
import com.face.bean.CaskBean
import com.face.databinding.ItemCashItemBinding
import com.face.ui.gift.CashDetailsActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


open class CashAdapter() : BaseAdapter<CaskBean, ItemCashItemBinding>(
    R.layout.item_cash_item, CaskBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemCashItemBinding>,
        binding: ItemCashItemBinding,
        data: CaskBean?,
        position: Int
    ) {
        binding.apply {
            txtId.text = "Card: ${data?.cardNo}"
            txtTime.text = TimeUtil.getFormatDate(data?.useTime ?: 0, "dd/MM/yyyy HH:mm:ss")
            root.singleClick {
                root.context.openActivity<CashDetailsActivity>() {
                    putParcelable("gift", data)
                }
            }
        }
    }

}
