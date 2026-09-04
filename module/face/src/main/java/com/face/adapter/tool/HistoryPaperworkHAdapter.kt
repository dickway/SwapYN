package com.face.adapter.tool

import android.view.View
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ItemHistoryHItemBinding
import com.face.key.AiTaskType
import com.face.ui.ToolGeneratingActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class HistoryPaperworkHAdapter : BaseAdapter<ToolTaskBean, ItemHistoryHItemBinding>(
    R.layout.item_history_h_item, ToolTaskBean.differCallback
) {
    var onOperateClick: ((ToolTaskBean?) -> Unit)? = null
    var onLongClick: ((ToolTaskBean?) -> Unit)? = null
    override fun onBindData(
        holder: BaseHolder<ItemHistoryHItemBinding>,
        binding: ItemHistoryHItemBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                root.singleClick {
                    imgCover.context.openActivity<ToolGeneratingActivity> {
                        putString("taskType", data.type)
                        putString("taskId", data.taskId)
                    }
                }
                txtBg.setBackgroundResource(R.drawable.bg_btn_80_corners8)
                if (state == 0 || state == 1) {
                    txtBg.visibility = View.VISIBLE
                } else {
                    txtBg.visibility = View.GONE
                }
                if (data.type == AiTaskType.TEXT2IMG) {
                    txtBg.setBackgroundResource(com.key.R.mipmap.img_text_d)
                }
                imgSate.visibility = View.GONE
                imgDelete.visibility = View.GONE
                if (state == 3) {
                    imgDelete.visibility = View.VISIBLE
                    imgSate.visibility = View.VISIBLE
                }

//                Glide.with(imgCover.context)
//                    .load(recordUrl)
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .into(imgCover)
                imgCover.loadImage(
                    recordUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.setOnLongClickListener {
                    onLongClick?.invoke(data)
                    true
                }
                imgDelete.singleClick {
                    onOperateClick?.invoke(data)
                }

                imgCover.singleClick {
                    imgCover.context.openActivity<ToolGeneratingActivity> {
                        putString("taskType", data.type)
                        putString("taskId", data.taskId)
                    }
                }
            }


        }
    }
}
