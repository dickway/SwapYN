package com.a.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import coil.load
import com.a.R
import com.a.activity.ASwapActivity
import com.a.databinding.ItemApictureItemBinding
import com.face.adapter.CommonRefreshStateAdapter
import com.face.bean.AiFaceBean
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BasePagingAdapter
import com.zzkj.structure.util.ImgLoader
import com.zzkj.structure.util.ktx.singleClick

class APictureAdapter : BasePagingAdapter<AiFaceBean, ItemApictureItemBinding>(
    R.layout.item_apicture_item, AiFaceBean.differCallback
) {
    val concatAdapter by lazy {
        withLoadStateFooter(CommonRefreshStateAdapter { retry() })
    }

    // Rebind the shape when a refresh moves an existing image into or out of first place.
    override fun getItemViewType(position: Int): Int = if (position == 0) 1 else 0

    override fun onBindData(
        holder: BaseHolder<ItemApictureItemBinding>,
        binding: ItemApictureItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        val hasImageSize = data != null && data.width > 1 && data.height > 1
        val ratio = when {
            position == 0 -> "H,1:1"
            hasImageSize -> "H,${data!!.width}:${data.height}"
            else -> "H,3:4"
        }
        binding.imgCover.updateLayoutParams<ConstraintLayout.LayoutParams> {
            dimensionRatio = ratio
        }

        val imageUrl = data?.let {
            if (it.mediaType == "video") it.webpUrl else it.imageUrl
        }
        binding.imgCover.load(imageUrl, ImgLoader.imageLoader) {
            placeholder(com.key.R.drawable.img_default_m)
            error(com.key.R.drawable.img_default_m)
            if (data != null && !hasImageSize && position != 0) {
                listener(onSuccess = { _, result ->
                    val currentPosition = holder.bindingAdapterPosition
                    val width = result.drawable.intrinsicWidth
                    val height = result.drawable.intrinsicHeight
                    if (currentPosition > 0 && peek(currentPosition) === data &&
                        width > 0 && height > 0
                    ) {
                        binding.imgCover.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            dimensionRatio = "H,$width:$height"
                        }
                    }
                })
            }
        }

        if (data == null) {
            binding.imgCover.setOnClickListener(null)
        } else {
            binding.imgCover.singleClick {
                if (data.mediaType == "video") {
                    SwapFaceNewActivity.jump(binding.imgCover.context, data.id)
                } else {
                    ASwapActivity.jump(binding.imgCover.context, data.id)
                }
            }
        }
    }
}
