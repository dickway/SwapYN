package com.face.adapter.explore

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.R
import com.face.bean.AiFaceBean
import com.face.bean.RecommendBean
import com.face.databinding.ItemExploreBBinding
import com.face.databinding.ItemExploreBannerBinding
import com.face.databinding.ItemExploreHotBinding
import com.face.databinding.ItemExploreLikeBinding
import com.face.databinding.ItemExploreTypeBinding
import com.face.databinding.ItemFaceItemBinding
import com.face.ui.SwapFaceListActivity
import com.face.ui.tmoves.MovesStartActivity
import com.face.util.CepatSnapHelper
import com.face.util.DisInterceptTouchListener
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


/**
 * @author 再战科技
 * @date 2022/3/24
 * @description
 */
class ExploreListAdapter() :
    BaseMultipleAdapter<AiFaceBean>(AiFaceBean.differCallback) {

    //RecyclerView 使用 LinearSnapHelper(快速滑动) PagerSnapHelper一页一页滑动
    val linearSnapHelper = CepatSnapHelper()

    var onMoreClick: ((RecommendBean.RecommendX?) -> Unit)? = null

    var onMoreToolClick: ((String) -> Unit)? = null

    var onToolClick: ((Int) -> Unit)? = null


    private val bannerAdapter: ExploreBannerAdapter by lazy { ExploreBannerAdapter() }

    private val bannerTypeAdapter: BannerTypeAdapter by lazy { BannerTypeAdapter() }

    private val exploreFaceAdapter: ExploreLikeAdapter by lazy { ExploreLikeAdapter() }


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
                AiFaceBean.ITEM_TYPE_B -> R.layout.item_explore_b
                AiFaceBean.ITEM_TYPE_BANNER -> R.layout.item_explore_banner
                AiFaceBean.ITEM_TYPE_LIKE -> R.layout.item_explore_like
                AiFaceBean.ITEM_TYPE_HOT -> R.layout.item_explore_hot
                AiFaceBean.ITEM_TYPE_TYPE -> R.layout.item_explore_type
                AiFaceBean.ITEM_TYPE_FACE -> R.layout.item_face_item
                else -> R.layout.item_face_item
            },
        )
        return holder
    }

    fun setItemOffsets(outRect: Rect, position: Int) {
        getItemViewType(position).let {
            if (
                it == AiFaceBean.ITEM_TYPE_B ||
                it == AiFaceBean.ITEM_TYPE_BANNER ||
                it == AiFaceBean.ITEM_TYPE_LIKE ||
                it == AiFaceBean.ITEM_TYPE_HOT ||
                it == AiFaceBean.ITEM_TYPE_TYPE
            ) {
                outRect.set((-7).dp, 0, (-7).dp, 0)
            } else {
                outRect.set(0, 0, 0, 0)
            }
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
                bindBannerData(binding as ItemExploreBannerBinding, data, position)
            }


            AiFaceBean.ITEM_TYPE_B -> {
                bindBData(binding as ItemExploreBBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_LIKE -> {
                bindLikeData(binding as ItemExploreLikeBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_HOT -> {
                bindHotData(binding as ItemExploreHotBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_TYPE -> {
                bindTypeData(binding as ItemExploreTypeBinding, data, position)
            }

            AiFaceBean.ITEM_TYPE_FACE -> {
                lp.isFullSpan = false
                bindFaceData(binding as ItemFaceItemBinding, data, position, holder)
            }
        }
    }

    private fun bindBData(
        binding: ItemExploreBBinding,
        data: AiFaceBean?,
        position: Int
    ) {

        if (binding.toolRecycler.layoutManager == null) {
            binding.toolRecycler.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        val toolAdapter = ToolTypeAdapter()
        if (binding.toolRecycler.adapter != toolAdapter) {
            binding.toolRecycler.adapter = toolAdapter
        }
        toolAdapter.submitList(data?.toolTypeList)
        toolAdapter.onItemClick = { _, bean, _ ->
            onToolClick?.invoke(bean?.id ?: 0)
        }

        if (binding.toolRecycler2.layoutManager == null) {
            binding.toolRecycler2.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        val tool2Adapter = ToolType2Adapter()
        if (binding.toolRecycler2.adapter != tool2Adapter) {
            binding.toolRecycler2.adapter = tool2Adapter
        }

        tool2Adapter.submitList(data?.toolTypeList2)
        tool2Adapter.onItemClick = { _, bean, _ ->
            onToolClick?.invoke(bean?.id ?: 0)
        }


        if (binding.typeRecycler.layoutManager == null) {
            binding.typeRecycler.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        if (binding.typeRecycler.adapter != bannerTypeAdapter) {
            binding.typeRecycler.adapter = bannerTypeAdapter
        }
        bannerTypeAdapter.submitList(SPUtils.typeTags)
        bannerTypeAdapter.onItemClick = { _, bean, _ ->
            onMoreClick?.invoke(
                RecommendBean.RecommendX("Type", name = bean ?: "")
            )
        }
        binding.tvToolMore.singleClick {
            onMoreToolClick?.invoke("ToolMore")
        }
    }


    private fun bindBannerData(
        binding: ItemExploreBannerBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        if (GVM.INSTANT.isShowTool.value != 0) {
            binding.typeRecycler.visibility = View.VISIBLE
            binding.toolRecycler.visibility = View.VISIBLE
        } else {
            binding.typeRecycler.visibility = View.GONE
            binding.toolRecycler.visibility = View.GONE
        }
        if (binding.toolRecycler.layoutManager == null) {
            binding.toolRecycler.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        val likeAdapter = ToolTypeAdapter()
        if (binding.toolRecycler.adapter != likeAdapter) {
            binding.toolRecycler.adapter = likeAdapter
        }
        likeAdapter.submitList(data?.toolTypeList)
        likeAdapter.onItemClick = { _, bean, _ ->
            onToolClick?.invoke(bean?.id ?: 0)
        }

        if (binding.typeRecycler.layoutManager == null) {
            binding.typeRecycler.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        if (binding.typeRecycler.adapter != bannerTypeAdapter) {
            binding.typeRecycler.adapter = bannerTypeAdapter
        }
        bannerTypeAdapter.submitList(SPUtils.typeTags)
        bannerTypeAdapter.onItemClick = { _, bean, _ ->
            onMoreClick?.invoke(
                RecommendBean.RecommendX("Type", name = bean ?: "")
            )
        }

        binding.tvMore.singleClick {
            onMoreClick?.invoke(
                RecommendBean.RecommendX(
                    "Banner",
                    name = getStringX(R.string.frament_explore_banner)
                )
            )
        }


        binding.banner.apply {
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(
                    null, LinearLayoutManager.HORIZONTAL, false
                )
            }
            layoutParams.height = (getScreenWidth() * 0.50).toInt()
            // 滑动后Snap
            linearSnapHelper.attachToRecyclerView(this)
            setOnTouchListener(DisInterceptTouchListener())

//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        if (recyclerView.childCount > 0) {
//                            // 获取具体位置
//                            val layoutParamsR =
//                                linearSnapHelper.findSnapView(recyclerView.layoutManager)?.layoutParams as RecyclerView.LayoutParams
//                            val showPosition = layoutParamsR.absoluteAdapterPosition;
//                            bannerAdapter.selectIndex = showPosition
//                        }
//                    }
//                }
//            })
            if (adapter != bannerAdapter) {
                adapter = bannerAdapter
            }
            bannerAdapter.submitList(data?.subList)
//            bannerAdapter.selectIndex = 0
        }
    }


    private fun bindLikeData(
        binding: ItemExploreLikeBinding,
        data: AiFaceBean?,
        position: Int
    ) {

        binding.more = data?.more
        binding.tvName.text = data?.title
        binding.showMore = data?.more?.isNotEmpty()

        binding.tvMore.singleClick {
            onMoreClick?.invoke(data?.recommendBean)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(
            null, LinearLayoutManager.HORIZONTAL, false
        )
        val likeAdapter = ExploreLikeAdapter(data?.title ?: "")
        binding.recyclerView.adapter = likeAdapter
        likeAdapter.submitList(data?.subList)
    }

    private fun bindHotData(
        binding: ItemExploreHotBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        binding.title = data?.title
        binding.tvMore.singleClick {
            onMoreClick?.invoke(
                RecommendBean.RecommendX(
                    "Hot",
                    name = getStringX(R.string.hot_txt)
                )
            )
        }
        if (binding.recyclerView.layoutManager == null) {
            binding.recyclerView.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        if (binding.recyclerView.adapter != exploreFaceAdapter) {
            binding.recyclerView.adapter = exploreFaceAdapter
        }
        exploreFaceAdapter.submitList(data?.subList)
    }

    private fun bindTypeData(
        binding: ItemExploreTypeBinding,
        data: AiFaceBean?,
        position: Int
    ) {
    }

    private fun bindFaceData(
        binding: ItemFaceItemBinding,
        data: AiFaceBean?,
        position: Int,
        holder: BaseHolder<ViewDataBinding>
    ) {
        binding.apply {
            data?.apply {

                val hImg: Int
                if (width >= height) {
                    hImg = (getScreenWidth() - 42.dp) / 2
                    imgCover.layoutParams.height = hImg
                } else {
                    hImg = (getScreenWidth() - 42.dp) / 2 * 222 / 166
                    imgCover.layoutParams.height = hImg
                }

                imgCover.loadImage(
                    if (mediaType == ("video")) webpUrl else imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgVideo.visibility =
                    if (mediaType == "video") View.VISIBLE else View.GONE

                if (pro == 0) {
                    imgPro.visibility = View.GONE
                } else {
                    imgPro.visibility = View.VISIBLE
                }
                imgCover.singleClick {
                    val pos = holder.bindingAdapterPosition
                    if (pos !in currentList.indices) return@singleClick
                    val list =
                        (currentList.drop(pos) + currentList.take(pos)).take(50).toMutableList()
                    SwapFaceListActivity.jump(imgCover.context, id, 0, list)
                }
            }
        }

    }

}