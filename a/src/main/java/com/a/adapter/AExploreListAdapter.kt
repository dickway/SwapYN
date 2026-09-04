package com.a.adapter

import android.graphics.Rect
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a.R
import com.a.activity.AMoreActivity
import com.a.activity.ASwapActivity
import com.a.activity.tool.ACartoonStartActivity
import com.a.activity.tool.AChangeActivity
import com.a.activity.tool.AChangeStartActivity
import com.a.activity.tool.AClearStartActivity
import com.a.activity.tool.APaperworkStartActivity
import com.a.activity.tool.ARemovalStartActivity
import com.a.databinding.ItemAexploreBannerBinding
import com.a.databinding.ItemAexploreHotBinding
import com.a.databinding.ItemAexploreToolBinding
import com.a.databinding.ItemAfaceItemBinding
import com.face.bean.AiFaceBean
import com.face.bean.RecommendBean
import com.face.ui.SwapFaceNewActivity
import com.face.util.CepatSnapHelper
import com.face.util.DisInterceptTouchListener
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.indicator.RoundLinesIndicator
import com.youth.banner.listener.OnPageChangeListener
import com.youth.banner.transformer.AlphaPageTransformer
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.px2dp
import com.zzkj.structure.util.ktx.singleClick


class AExploreListAdapter :
    BaseMultipleAdapter<AiFaceBean>(AiFaceBean.differCallback) {

    //RecyclerView 使用 LinearSnapHelper(快速滑动) PagerSnapHelper一页一页滑动
    val linearSnapHelper = CepatSnapHelper()

    var onMoreClick: ((RecommendBean.RecommendX?) -> Unit)? = null

    private val bannerAdapter: AExploreBannerAdapter by lazy { AExploreBannerAdapter() }


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
                AiFaceBean.ITEM_TYPE_BANNER -> R.layout.item_aexplore_banner
                AiFaceBean.ITEM_TYPE_TOOL -> R.layout.item_aexplore_tool
                AiFaceBean.ITEM_TYPE_LIKE -> R.layout.item_aexplore_hot
                AiFaceBean.ITEM_TYPE_FACE -> R.layout.item_aface_item
                else -> R.layout.item_aface_item
            },
        )
        return holder
    }

    fun setItemOffsets(outRect: Rect, position: Int) {
        getItemViewType(position).let {
//            if (
//                it == AiFaceBean.ITEM_TYPE_BANNER ||
//                it == AiFaceBean.ITEM_TYPE_LIKE
//            ) {
//                outRect.set((-6).dp, 0, (-6).dp, 0)
//            } else {
//                outRect.set(0, 0, 0, 0)
//            }
        }
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        val lp = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.isFullSpan = true

        when (getItemViewType(position)) {
            AiFaceBean.ITEM_TYPE_BANNER -> {
                bindBannerData(binding as ItemAexploreBannerBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_TOOL -> {
                bindToolData(binding as ItemAexploreToolBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_LIKE -> {
                bindHotData(binding as ItemAexploreHotBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_FACE -> {
                lp.isFullSpan = false
                bindFaceData(binding as ItemAfaceItemBinding, data, position)
            }
        }
    }

    private fun bindBannerData(
        binding: ItemAexploreBannerBinding,
        data: AiFaceBean?,
        position: Int
    ) {

        binding.banner.apply {
            if (adapter != bannerAdapter) {
                adapter = bannerAdapter
            }
//            indicator = RectangleIndicator(context)
//            setScrollTime(4000)
            // 核心：纯渐变，无位移
//            setPageTransformer { page, position ->
//                page.apply {
//                    // 取消左右移动
//                    translationX = -position * width
//
//                    // 透明渐变
//                    alpha = when {
//                        position <= -1f || position >= 1f -> 0f
//                        position == 0f -> 1f
//                        else -> 1 - kotlin.math.abs(position)
//                    }
//                }
//            }
            setDatas(data?.subList)
                .setIndicatorHeight(0)
                .setIndicatorWidth(0, 0)
//                .setBannerGalleryEffect(0, 10,0.8f)
                .setBannerGalleryEffect(28.dp, 10, 0.9f)
                .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
        }
    }

    private fun bindToolData(
        binding: ItemAexploreToolBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        binding.removal.singleClick {
            binding.removal.context.openActivity<AClearStartActivity>()
        }
        binding.idphoto.singleClick {
            binding.idphoto.context.openActivity<APaperworkStartActivity>()
        }
        binding.cartoon.singleClick {
            binding.cartoon.context.openActivity<ACartoonStartActivity>()
        }
        binding.backgroun.singleClick {
            binding.backgroun.context.openActivity<ARemovalStartActivity>()
        }
        binding.age.singleClick {
            binding.age.context.openActivity<AChangeStartActivity>()
        }


    }

    private fun bindHotData(
        binding: ItemAexploreHotBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        binding.title = data?.title
//        if (position == 1) {
//            binding.hotImg.setImageResource(R.drawable.a_icon_most)
//        } else {
//            binding.hotImg.setImageResource(R.drawable.a_icon_halloween)
//        }
        binding.tvMore.singleClick {
            data?.recommendBean?.apply {
                binding.tvMore.context.openActivity<AMoreActivity> {
                    putString("title", name)
                    putString("type", type)
                    putInt("openValue", open_value.toInt())
                }
            }
        }
        if (binding.recyclerView.layoutManager == null) {
            binding.recyclerView.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        val likeAdapter = AExploreLikeAdapter()
        binding.recyclerView.adapter = likeAdapter
        likeAdapter.submitList(data?.subList)
    }

    private fun bindFaceData(
        binding: ItemAfaceItemBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        binding.apply {
            data?.apply {
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 42.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 42.dp) / 2 * 222 / 166
                }
                imgCover.loadImage(
                    imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.singleClick {
                    if (mediaType != "video") {
                        ASwapActivity.jump(imgCover.context, id)
                    } else {
                        SwapFaceNewActivity.jump(imgCover.context, id)
                    }
                }
            }
        }

    }

}