package com.face.adapter.other

import android.graphics.Color
import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.LogUtils
import com.face.R
import com.face.bean.SizeBean
import com.face.databinding.ItemAipaperworkSizeBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth


class SizeAdapter : BaseAdapter<SizeBean, ItemAipaperworkSizeBinding>(
    R.layout.item_aipaperwork_size, object : DiffUtil.ItemCallback<SizeBean>() {
        override fun areItemsTheSame(oldItem: SizeBean, newItem: SizeBean): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: SizeBean,
            newItem: SizeBean
        ): Boolean {
            return false
        }
    }
) {
    var selectIndex = -1
        set(value) {
            if (value != field) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemAipaperworkSizeBinding>,
        binding: ItemAipaperworkSizeBinding,
        data: SizeBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            tvName.text = data?.size
            if (selectIndex != -1) {
                imgCover.alpha = 1f
            } else {
                imgCover.alpha = 0.5f
            }

            tvBl.text = "${data?.proportionW}:${data?.proportionH}"

            imgCover.setImageResource(data?.img ?: com.key.R.mipmap.img_icon_size_w)

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
