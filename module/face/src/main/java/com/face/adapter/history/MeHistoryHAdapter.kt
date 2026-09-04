package com.face.adapter.history

import android.view.View
import com.face.R
import com.face.bean.TaskBean
import com.face.databinding.ItemMineHBinding
import com.face.key.AiTaskType
import com.face.ui.CompletionActivity
import com.face.ui.CompletionFourActivity
import com.face.ui.ProductionActivity
import com.face.ui.StatusActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class MeHistoryHAdapter : BaseAdapter<TaskBean, ItemMineHBinding>(
    R.layout.item_mine_h, TaskBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemMineHBinding>,
        binding: ItemMineHBinding,
        data: TaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                root.singleClick {
                    if (state == 3) {//生成失败点击跳转
                        root.context.openActivity<StatusActivity>() {
                            putParcelable("task_data", data)
                        }
                    } else if (state == 2) {
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
                    } else {//生成中
                        ProductionActivity.jump(
                            root.context,
                            if (data.taskType == AiTaskType.GEN_PERSONPIC) 1 else 0,
                            id,
                            true,
                            media.webpUrl.ifEmpty {
                                 media.imageUrl
                            }
                        )
                    }
                }
                if (state == 0 || state == 1) {
                    txtBg.visibility = View.VISIBLE
                } else {
                    txtBg.visibility = View.GONE
                }
                imgSate.visibility = View.GONE
                if (state == 3) {
                    imgSate.visibility = View.VISIBLE
                }

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
//                    .load(loadUrl)
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 磁盘缓存
//                    .skipMemoryCache(true) // 使用内存缓存
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(imgCover)

            }


        }
    }
}
