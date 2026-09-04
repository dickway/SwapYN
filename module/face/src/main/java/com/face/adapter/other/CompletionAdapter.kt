package com.face.adapter.other

import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.bean.TaskBean
import com.face.databinding.ItemFaceItemBinding
import com.face.ui.CompletionActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.openActivity

class CompletionAdapter(private var heightImg: Int, var taskBean: TaskBean) :
    BaseAdapter<String, ItemFaceItemBinding>(
        R.layout.item_face_item, object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                TODO("Not yet implemented")
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                TODO("Not yet implemented")
            }
        }
    ) {
    override fun onBindData(
        holder: BaseHolder<ItemFaceItemBinding>,
        binding: ItemFaceItemBinding,
        data: String?,
        position: Int
    ) {
        holder.binding.apply {
            imgCover.layoutParams.height =heightImg
            imgCover.loadImage(
                data,
                placeholderResId = com.key.R.drawable.img_default_m
            )
            root.setOnClickListener {
                imgCover.context.openActivity<CompletionActivity>() {
                    putString("task_id", taskBean.id)
                    putInt("task_num", position)
                }
            }
        }
    }
}
