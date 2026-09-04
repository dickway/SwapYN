package com.face.adapter.collect

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.apkfuns.logutils.LogUtils
import com.face.R
import com.face.bean.CollectAMBean
import com.face.bean.TaskBean
import com.face.databinding.ItemCollectBinding
import com.face.databinding.ItemHotlistItemBinding
import com.face.ui.CompletionActivity
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick

/**
 * @author 再战科技
 * @date 2022/3/24
 * @description
 */
class CollectFragmentAdapter() :
    BaseMultipleAdapter<CollectAMBean>(CollectAMBean.Companion.differCallback) {

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
                CollectAMBean.Companion.ITEM_TYPE_TIME -> R.layout.item_collect
                CollectAMBean.Companion.ITEM_TYPE_BEAN -> R.layout.item_hotlist_item
                else -> R.layout.item_hotlist_item
            },
        )
        return holder
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: CollectAMBean?,
        position: Int
    ) {
        val lp = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.isFullSpan = true

        when (getItemViewType(position)) {

            CollectAMBean.Companion.ITEM_TYPE_TIME -> {
                bindTimeData(binding as ItemCollectBinding, data, position)
            }

            CollectAMBean.Companion.ITEM_TYPE_BEAN -> {
                lp.isFullSpan = false
                bindBeanData(binding as ItemHotlistItemBinding, data, position,holder)
            }
        }
    }


    private fun bindTimeData(
        binding: ItemCollectBinding,
        data: CollectAMBean?,
        position: Int
    ) {
        binding.tvTime.text = data?.timeDay
    }

    private fun bindBeanData(
        binding: ItemHotlistItemBinding,
        data: CollectAMBean?,
        position: Int,
        holder: BaseHolder<ViewDataBinding>
    ) {
        binding.apply {
            data?.apply {

                imgDelete.visibility = View.GONE

                if (contentBean.width >= contentBean.height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
                }

                val loadUrl = if (contentBean.mediaType == ("video")) {
                    contentBean.webpUrl.ifEmpty {
                        contentBean.imageUrl
                    }
                } else {
                    contentBean.imageWater.ifEmpty {
                        contentBean.imageUrl
                    }
                }
                imgVideo.visibility =
                    if (contentBean.mediaType == "video") View.VISIBLE else View.GONE
                imgCover.loadImage(
                    loadUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                if (contentBean.pro == 0) {
                    imgPro.visibility = View.GONE
                } else {
                    imgPro.visibility = View.VISIBLE
                }
                imgCover.singleClick {
                    if (data.type == "media") {//素材类型跳转
                        SwapFaceNewActivity.Companion.jump(imgCover.context, contentBean.mediaId)
                    } else {//生成结果类型跳转
                        val collectBean = TaskBean()
                        val newWater = mutableListOf<String>().apply {
                            repeat(data.contentBean.index + 1) {
                                if (contentBean.mediaType == "video") {
                                    add(contentBean.videoWater)
                                } else {
                                    add(contentBean.imageWater)
                                }
                            }
                        }
                        val newJ = mutableListOf<String>().apply {
                            repeat(data.contentBean.index + 1) {
                                if (contentBean.mediaType == "video") {
                                    add(contentBean.videoUrl)
                                } else {
                                    add(contentBean.imageUrl)
                                }
                            }
                        }
                        collectBean.id = "1111"//默认任务id防止为空拦截
                        collectBean.result.dataWebp = contentBean.webpUrl
                        collectBean.result.dataWater = newWater
                        collectBean.result.mData = newJ
                        collectBean.media.mediaId = contentBean.mediaId
                        collectBean.media.mediaType = contentBean.mediaType
                        collectBean.media.width = contentBean.width
                        collectBean.media.height = contentBean.height
                        collectBean.content=contentBean.taskContent
                        imgCover.context.openActivity<CompletionActivity> {
                            putString("task_id", data.contentBean.taskId)
                            putBoolean("collect", true)
                            putParcelable("collectBean", collectBean)
                            putInt("task_num", data.contentBean.index)
                        }
                    }

                }
            }
        }

    }

}