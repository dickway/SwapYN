package com.face.adapter.history

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.bean.TaskBean
import com.face.databinding.ItemHotlistItemBinding
import com.face.key.AiTaskType
import com.face.ui.CompletionActivity
import com.face.ui.CompletionFourActivity
import com.face.ui.StatusActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick

class HistoryFaceAdapter : BaseAdapter<TaskBean, ItemHotlistItemBinding>(
    R.layout.item_hotlist_item, object : DiffUtil.ItemCallback<TaskBean>() {
        override fun areItemsTheSame(oldItem: TaskBean, newItem: TaskBean): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: TaskBean, newItem: TaskBean): Boolean {
            return false
        }
    }
) {
    var onLongClick: ((TaskBean?) -> Unit)? = null

    override fun onBindData(
        holder: BaseHolder<ItemHotlistItemBinding>,
        binding: ItemHotlistItemBinding,
        data: TaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                if (media.width >= media.height) {//根据宽高大小设置图片正方形 还是4：3
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
                }
                if (result.dataWater.size > 1) {
                    tvNum.visibility = View.VISIBLE
                    tvNum.text = result.dataWater.size.toString()
                } else {
                    tvNum.visibility = View.GONE
                }
                imgSate.visibility = View.GONE
                if (state == 3) {
                    imgSate.visibility = View.VISIBLE
                }
                if (isnew == 0) {
                    imgMsg.visibility = View.GONE
                } else {
                    imgMsg.visibility = View.VISIBLE
                }
                imgVideo.visibility =
                    if (media.mediaType == "video") View.VISIBLE else View.GONE

                var img = if (media.mediaType == ("video")) result.dataWebp else {
                    if (result.dataWater.isNotEmpty()) result.dataWater[0] else result.dataWebp
                }

                if (img.isEmpty()) {
                    img = media.webpUrl.ifEmpty { media.imageUrl }
                }
                imgCover.loadImage(
                    img,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

//                Glide.with(imgCover.context)
//                    .load(img)
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .skipMemoryCache(true)
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(imgCover)

                imgPro.visibility = View.GONE

                imgCover.setOnLongClickListener {
                    onLongClick?.invoke(data)
                    true
                }
                imgDelete.singleClick {
                    onLongClick?.invoke(data)
                }


                imgCover.singleClick {
                    if (state == 3) {//生成失败点击跳转
                        root.context.openActivity<StatusActivity>() {
                            putParcelable("task_data", data)
                        }
                    } else {//生成中
                        if (taskType == AiTaskType.GEN_PERSONPIC) {//自定义风格4张
                            imgCover.context.openActivity<CompletionFourActivity>() {
                                putParcelable("task_data", data)
                            }
                        } else {
                            imgCover.context.openActivity<CompletionActivity>() {
                                putString("task_id", data.id)
                                putInt("task_num", 0)
                            }
                        }
                    }
                }
            }
        }
    }
}
