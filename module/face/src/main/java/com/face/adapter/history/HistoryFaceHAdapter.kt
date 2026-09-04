package com.face.adapter.history

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDownsampler
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.face.R
import com.face.bean.TaskBean
import com.face.databinding.ItemHistoryHItemBinding
import com.face.key.AiTaskType
import com.face.ui.ProductionActivity
import com.face.ui.StatusActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getDrawableX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class HistoryFaceHAdapter : BaseAdapter<TaskBean, ItemHistoryHItemBinding>(
    R.layout.item_history_h_item, TaskBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemHistoryHItemBinding>,
        binding: ItemHistoryHItemBinding,
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

                imgVideo.visibility =
                    if (media.mediaType == "video") View.VISIBLE else View.GONE


                imgSate.visibility = View.GONE
                if (state == 3) {
                    imgSate.visibility = View.VISIBLE
                }

                val loadUrl = media.webpUrl.ifEmpty {
                    media.imageUrl
                }
                imgCover.loadImage(
                    loadUrl,
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
//                    ) // disable system decoder for each request
//                    .into(imgCover)

            }


        }
    }
}
