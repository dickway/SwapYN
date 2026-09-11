package com.face.adapter.history

import android.graphics.PorterDuff
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.AiFaceBean
import com.face.bean.ToolTaskBean
import com.face.databinding.ItemHotlistClearItemBinding
import com.face.key.AiTaskType
import com.face.ui.ToolCompletionActivity
import com.face.ui.tclear.ClearCompletionActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick

class HistoryClearTaskAdapter : BaseAdapter<ToolTaskBean, ItemHotlistClearItemBinding>(
    R.layout.item_hotlist_clear_item, object : DiffUtil.ItemCallback<ToolTaskBean>() {
        override fun areItemsTheSame(
            oldItem: ToolTaskBean,
            newItem: ToolTaskBean
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: ToolTaskBean,
            newItem: ToolTaskBean
        ): Boolean {
            return false
        }
    }, setLongClickListener = true
) {
    var onOperateClick: ((ToolTaskBean?) -> Unit)? = null
    var onLongClick: ((ToolTaskBean?) -> Unit)? = null

    override fun onBindData(
        holder: BaseHolder<ItemHotlistClearItemBinding>,
        binding: ItemHotlistClearItemBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                imgCover.layoutParams.height = (getScreenWidth() - 40.dp) / 2 * 4 / 3

                data.recordUrl = data.getCutUrl()

                if(data.type.equals(AiTaskType.RMBG, false)){
                    imgOperate.setColorFilter(getColorX(R.color.colorBgBlack), PorterDuff.Mode.SRC_IN)
                }else{
                    imgOperate.setColorFilter(getColorX(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
                }

                imgCover.loadImage(
                    data.recordUrl.split(",").lastOrNull(),
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                tvTime.text = name
                imgOperate.singleClick {
                    onOperateClick?.invoke(data)
                }

                imgCover.singleClick {
                    if (data.type.equals(AiTaskType.LAMA_CLEANER, false)) {
                        imgCover.context.openActivity<ClearCompletionActivity>() {
                            putParcelable("task", data)
                        }
                    } else {
                        imgCover.context.openActivity<ToolCompletionActivity>() {
                            putParcelable("task", data)
                            putBoolean("ist", data.type == AiTaskType.RMBG)
                        }

                    }

                }
                imgCover.setOnLongClickListener {
                    onLongClick?.invoke(data)
                    true
                }


            }
        }
    }
}
