package com.face.adapter.other

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.face.bean.VipSchemeBean
import com.face.R
import com.face.bean.MyFaceImgBean
import com.face.databinding.ItemVipSchemeBinding
import com.face.util.GVM
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.singleClick

class VipSchemeAdapter : BaseAdapter<VipSchemeBean, ItemVipSchemeBinding>(
    R.layout.item_vip_scheme,
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

    var onLifetimeClick: ((VipSchemeBean?) -> Unit)? = null

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
        holder: BaseHolder<ItemVipSchemeBinding>,
        binding: ItemVipSchemeBinding,
        data: VipSchemeBean?,
        position: Int
    ) {
        data?.apply {


        binding.apply {
            bean = data
            isSelected = selectIndex == position
            tvHint.visibility = View.GONE
            tvUnitSelect.visibility = View.VISIBLE

            tvTitleSelect.text = data.name

            val isShowUnit = GVM.INSTANT.isShowTool.value == 0
            val price = data.getBeanPrice()

            var hintPoint = tvHint.context.getString(R.string.cumulative_gift2)

            // ---------- 1️⃣ 周期价格 ----------
            tvPriceSelect.text = when (data.day) {
                0 -> {
                    tvHint.visibility = View.VISIBLE
                    hintPoint = tvHint.context.getString(R.string.cumulative_gift3)
                    price
                }

                1 -> price?.withUnit(isShowUnit, "Day")
                7 -> price?.withUnit(isShowUnit, "Week")
                in 28..32 -> price?.withUnit(isShowUnit, "Month")
                in 87..94 -> price?.withUnit(isShowUnit, "Quarter")

                in 364..368 -> {
                    if (data.trialPriceTitle.isNotEmpty()) {
                        hintPoint = tvHint.context.getString(R.string.cumulative_gift365)
                    }
                    price?.withUnit(isShowUnit, "Year")
                }

                else -> price?.withUnit(isShowUnit, "${data.day} Days")
            }

            tvHintPoint1.text = hintPoint

            // ---------- 2️⃣ 累积提示 ----------
            tvHintPoint.text = tvHintPoint.context.getString(
                R.string.cumulative,
                data.remark.split("|").firstOrNull().orEmpty()
            )

            // ---------- 3️⃣ 试用 / 折扣 ----------
            isShow = data.trialPriceTitle.isNotEmpty()

            if (isShow ?: false) {
                val period = parseTrialPeriod(data.trialPriceTitle)

                tvTitleSelect.text = period
                tvPriceSelect.text =
                    if (data.showPrice.isNotEmpty()) {
                        data.showPrice.withUnit(isShowUnit, period)
                    } else {
                        tvUnitSelect.visibility = View.GONE
                        "${data.formattedPrice}/$period"
                    }
            }

            // ---------- 4️⃣ 点击 ----------
            tvHint.singleClick { onLifetimeClick?.invoke(data) }

            tvTitleSelect.singleClick {
                if (data.day == 0) {
                    onLifetimeClick?.invoke(data)
                } else {
                    onItemClick?.invoke(tvTitleSelect, data, position)
                }
            }
        }

        }
    }

    private fun String.withUnit(show: Boolean, unit: String): String =
        if (show) "$this/$unit" else this

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
}