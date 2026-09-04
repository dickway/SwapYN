package com.a.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.activity.AToolCompletionActivity
import com.a.activity.AToolGeneratingActivity
import com.a.databinding.ItemAhotlistItemBinding
import com.a.databinding.ItemAtoolTaskhistoryHItemBinding
import com.bumptech.glide.Glide
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class ATaskHistoryAdapter : BaseMultipleAdapter<ToolTaskBean>(
    object : DiffUtil.ItemCallback<ToolTaskBean>() {
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
    }
) {
    var onOperateClick: ((ToolTaskBean?) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return getItemData(position)?.itemType ?: 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<ViewDataBinding> {
        val holder: BaseHolder<ViewDataBinding> = innerCreateViewHolder(
            parent,
            when (viewType) {
                ToolTaskBean.ITEM_TYPE_HLIST -> R.layout.item_atool_taskhistory_h_item
                ToolTaskBean.ITEM_TYPE_LIST -> R.layout.item_ahotlist_item
                else -> R.layout.item_ahotlist_item
            }
        )
        return holder
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        when (getItemViewType(position)) {
            ToolTaskBean.ITEM_TYPE_HLIST -> {
                bindHListData(binding as ItemAtoolTaskhistoryHItemBinding, data, position)
            }

            ToolTaskBean.ITEM_TYPE_LIST -> {
                bindListData(binding as ItemAhotlistItemBinding, data, position)
            }
        }
    }

    private fun bindListData(
        binding: ItemAhotlistItemBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        binding.apply {
            data?.apply {
                tvTime.text = name
                imgCover.layoutParams.width = (getScreenWidth() - 40.dp) / 2
                imgCover.layoutParams.height = (getScreenWidth() - 40.dp) / 2 * 4 / 3

                if (data.type.equals(AiTaskType.RMBG, false) ||
                    data.type.equals(AiTaskType.IMG2CARTOON, false) ||
                    data.type.equals(AiTaskType.OLD_PHONE, false) ||
                    data.type.equals(AiTaskType.ID_CARD, false) ||
                    data.type.equals(AiTaskType.CHANGE_AGE, false) ||
                    data.type.equals(AiTaskType.DYNAMIC, false)
                ) {
                    data.recordUrl = data.getCutUrl()
                }

                Glide.with(imgCover.context)
                    .load(data.recordUrl.split(",").lastOrNull())
                    .placeholder(com.key.R.drawable.img_default_m)
                    .skipMemoryCache(true)
                    .into(imgCover)

                imgCover.singleClick {
                    imgCover.context.openActivity<AToolCompletionActivity>() {
                        putParcelable("task", data)
                        putBoolean("ist", data.type == AiTaskType.RMBG)
                    }

                }
            }
        }
    }

    private fun bindHListData(
        binding: ItemAtoolTaskhistoryHItemBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        binding.apply {
            imgCover.layoutParams.width = (getScreenWidth() - 40.dp) / 2
            imgCover.layoutParams.height = (getScreenWidth() - 40.dp) / 2 * 4 / 3
            data?.apply {

                if (state == 0 || state == 1) {
                    txtBg.visibility = View.VISIBLE
                } else {
                    txtBg.visibility = View.GONE
                }
                imgSate.visibility = View.GONE
                imgDelete.visibility = View.GONE
                if (state == 3) {
                    imgDelete.visibility = View.VISIBLE
                    imgSate.visibility = View.VISIBLE
                }

                Glide.with(imgCover.context)
                    .load(recordUrl)
                    .placeholder(com.key.R.drawable.img_default_m)
                    .into(imgCover)

                imgDelete.singleClick {
                    onOperateClick?.invoke(data)
                }
                txtBg.singleClick {
                    txtBg.context.openActivity<AToolGeneratingActivity> {
                        putString("taskId", data.taskId)
                    }
                }
                imgCover.singleClick {
                    imgCover.context.openActivity<AToolGeneratingActivity> {
                        putString("taskId", data.taskId)
                    }
                }
            }

        }
    }
}