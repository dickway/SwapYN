package com.a.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.databinding.ItemAvipSchemeBinding
import com.face.bean.VipSchemeBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class AVipSchemeAdapter : BaseAdapter<VipSchemeBean, ItemAvipSchemeBinding>(
    R.layout.item_avip_scheme,
    object : DiffUtil.ItemCallback<VipSchemeBean>() {
        override fun areItemsTheSame(oldItem: VipSchemeBean, newItem: VipSchemeBean): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: VipSchemeBean,
            newItem: VipSchemeBean
        ): Boolean {
            return false
        }
    }
) {
    //    var selectIndex = SPUtils.vipSelectIndex
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
        holder: BaseHolder<ItemAvipSchemeBinding>,
        binding: ItemAvipSchemeBinding,
        data: VipSchemeBean?,
        position: Int
    ) {
        data?.let {
            binding.apply {
                bean = it
                tvTitleSelect.text = it.name
                isSelected = selectIndex == position
                when (it.day) {//根据商品天数显示周期价格
                    1 -> {
//                        tvT.text ="/day"
                        tvPriceSelect.text = it.getBeanPrice()
                    }

                    7 -> {
//                        tvT.text ="/week"
                        tvPriceSelect.text = it.getBeanPrice()
                    }

                    28, 29, 30, 31, 32 -> {
//                        tvT.text ="/month"
                        tvPriceSelect.text = it.getBeanPrice()
                    }

                    87, 88, 89, 90, 91, 92, 93, 94 -> {
//                        tvT.text ="/quarter"
                        tvPriceSelect.text = it.getBeanPrice()
                    }

                    364, 365, 366, 367, 368 -> {
//                        tvT.text ="/year"
                        tvPriceSelect.text = it.getBeanPrice()
                    }

                    else -> {
//                        tvT.text ="/${it.day}days"
                        tvPriceSelect.text = it.getBeanPrice()
                    }
                }

                isShow = it.trialPriceTitle != ""//根据Google商品信息，判断是否是折扣商品
                tvUnitSelect.visibility=View.VISIBLE
                if (it.trialPriceTitle != "") {//根据Google商品信息，判断是否是折扣商品
                    tvPriceSelect.text = it.showPrice
//                    tvTime.text =parseTrialPeriod2(it.trialPriceTitle)

                    tvTitleSelect.text = parseTrialPeriod(it.trialPriceTitle)
                    if (it.showPrice.isEmpty()){
                        tvUnitSelect.visibility=View.GONE
                        tvPriceSelect.text = it.formattedPrice
//                        tvTime.text =parseTrialPeriod2(it.trialPriceTitle)
                    }
                }
            }
        }


    }

    /**
     * 商品信息以 ISO 8601 格式返回，例如 P7D 表示7天的试用期
     * */
    fun parseTrialPeriod(trialPeriod: String): String {
        return when {
            trialPeriod.endsWith("D") -> "${
                trialPeriod.removePrefix("P").removeSuffix("D")
            } Days"

            trialPeriod.endsWith("W") -> "${
                trialPeriod.removePrefix("P").removeSuffix("W")
            } Week"

            trialPeriod.endsWith("M") -> "${
                trialPeriod.removePrefix("P").removeSuffix("M")
            } Month"

            else -> ""
        }
    }

    /**
     * 商品信息以 ISO 8601 格式返回，例如 P7D 表示7天的试用期
     * */
    fun parseTrialPeriod2(trialPeriod: String): String {
        return when {
            trialPeriod.endsWith("D") -> "/${
                trialPeriod.removePrefix("P").removeSuffix("D")
            }days"

            trialPeriod.endsWith("W") -> "/${
                trialPeriod.removePrefix("P").removeSuffix("W")
            }week"

            trialPeriod.endsWith("M") -> "/${
                trialPeriod.removePrefix("P").removeSuffix("M")
            }month"

            else -> ""
        }
    }
}