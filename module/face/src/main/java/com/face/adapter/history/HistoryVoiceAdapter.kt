package com.face.adapter.history

import android.view.View
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ItemVoiceHistoryBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.singleClick


class HistoryVoiceAdapter : BaseAdapter<ToolTaskBean, ItemVoiceHistoryBinding>(
    R.layout.item_voice_history, ToolTaskBean.differCallback,setLongClickListener=true
) {
    var onSingOperateClick: ((ToolTaskBean?) -> Unit)? = null
    var onSingPlayClick: ((ToolTaskBean?, Int) -> Unit)? = null
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
        holder: BaseHolder<ItemVoiceHistoryBinding>,
        binding: ItemVoiceHistoryBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                isSelected = selectIndex == position

                tvName.text = data.name
                imgPlay.visibility = View.VISIBLE
                if (data.state == 1 || data.state == 0) {
                    tvName.text= TimeUtil.getFormatDate(
                        data.createTime,
                        TimeUtil.DEFAULT_DATE_TIME_DATA
                    )
                    tvTime.text = data.name
                    imgPlay.visibility = View.GONE
                    imgCover.setImageResource(com.key.R.drawable.img_icon_audiofile4)
                }else if (data.state == 3) {
                    tvName.text=TimeUtil.getFormatDate(
                        data.createTime,
                        TimeUtil.DEFAULT_DATE_TIME_DATA
                    )
                    imgPlay.visibility = View.GONE
                    tvTime.text = "Task failed"
                    imgCover.setImageResource(com.key.R.drawable.img_icon_audiofile3)
                }else{
                    tvTime.text = data.getUrlTime()
                    imgCover.setImageResource(com.key.R.drawable.img_icon_audiofile2)
                }

                imgPlay.singleClick {
                    onSingPlayClick?.invoke(data, position)
                }

                imgOperate.singleClick {
                    onSingOperateClick?.invoke(data)
                }
            }
        }
    }
}
