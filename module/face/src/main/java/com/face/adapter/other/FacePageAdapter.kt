package com.face.adapter.other

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.adapter.CommonRefreshStateAdapter
import com.face.bean.AiFaceBean
import com.face.databinding.ItemFaceItemBinding
import com.face.ui.SwapFaceListActivity
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BasePagingAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick


open class FacePageAdapter(val tag: String? = "") :
    BasePagingAdapter<AiFaceBean, ItemFaceItemBinding>(
        R.layout.item_face_item, object : DiffUtil.ItemCallback<AiFaceBean>() {
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
        holder: BaseHolder<ItemFaceItemBinding>,
        binding: ItemFaceItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
                }

//                Glide.with(imgCover.context)
//                    .load(if (mediaType == ("video")) webpUrl else imageUrl)
//                    .placeholder(com.key.R.drawable.img_default_m)
//                    .set(
//                        WebpDownsampler.USE_SYSTEM_DECODER,
//                        false
//                    )
//                    .into(imgCover)

                imgCover.loadImage(
                    if (mediaType == ("video")) webpUrl else imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )
                imgVideo.visibility =
                    if (mediaType == "video") View.VISIBLE else View.GONE

                imgPro.visibility = if (pro == 0) View.GONE else View.VISIBLE

                imgCover.singleClick {
                    val list=  (snapshot().items.drop(position) + snapshot().items.take(position)).take(50).toMutableList()
                    SwapFaceListActivity.jump(imgCover.context, id, 0, list)
                }
            }
        }
    }

//    override fun onViewRecycled(holder: BaseHolder<ItemFaceItemBinding>) {
//        super.onViewRecycled(holder)
//        val imageView = holder.binding.imgCover
//        Glide.with(imageView.context).clear(imageView)
//    }
}
