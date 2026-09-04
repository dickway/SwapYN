package com.face.adapter.other

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.face.R
import com.face.bean.CheckInBean
import com.face.databinding.ItemCheckInBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.getColorX

class PointDateAdapter(val today: Int,val maxDay: Int) : BaseAdapter<CheckInBean, ItemCheckInBinding>(
    R.layout.item_check_in, CheckInBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemCheckInBinding>,
        binding: ItemCheckInBinding,
        data: CheckInBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                bean = data
                if (data.dayOfMonth<=maxDay) tvDay.text = data.dayOfMonth.toString()
                if (data.type == null) {
                    tvDay.setTextColor(Color.WHITE)
                } else if(data.dayOfMonth == today){
                    tvDay.setTextColor(Color.parseColor("#F39828"))
                }else {
                    tvDay.setTextColor(Color.parseColor("#29FFFFFF"))
                }
                imgbg.setBackgroundResource(
                    when {
                        data.dayOfMonth == today -> R.drawable.bg_check_in_today
                        data.type != null -> R.drawable.bg_check_in_checked
                        else -> R.drawable.bg_check_in_not_checked
                    }
                )
            }
        }
    }
}
