package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemAfaceItemBinding
import com.face.adapter.CommonRefreshStateAdapter
import com.face.bean.AiFaceBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BasePagingAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick


open class AFacePageAdapter : BasePagingAdapter<AiFaceBean, ItemAfaceItemBinding>(
    R.layout.item_aface_item, object : DiffUtil.ItemCallback<AiFaceBean>() {
        override fun areItemsTheSame(
            oldItem: AiFaceBean,
            newItem: AiFaceBean
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: AiFaceBean,
            newItem: AiFaceBean
        ): Boolean {
            return false
        }
    }
) {

    val concatAdapter by lazy {
        withLoadStateFooter(CommonRefreshStateAdapter {
            retry()
        })
    }


    override fun onBindData(
        holder: BaseHolder<ItemAfaceItemBinding>,
        binding: ItemAfaceItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 40.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 40.dp) / 2 * 4 / 3
                }

                imgCover.loadImage(
                    if (mediaType == ("video")) webpUrl else imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )
                imgCover.singleClick {
                    if (mediaType!="video"){
                        ASwapActivity.jump(imgCover.context, id)
                    }else{
                        SwapFaceNewActivity.jump(imgCover.context, id)
                    }
                }
            }
        }
    }
}
