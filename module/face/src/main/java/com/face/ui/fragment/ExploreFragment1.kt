package com.face.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.BR
import com.face.R
import com.face.adapter.explore.ExploreListAdapter
import com.face.bean.AiFaceBean
import com.face.bean.RecommendBean
import com.face.databinding.FragmentExplore1Binding
import com.face.ui.DetailsActivity
import com.face.ui.MainActivity
import com.face.ui.SearchActivity
import com.face.ui.VipActivity
import com.face.ui.tmoves.MovesStartActivity
import com.face.ui.tadd.AddStartActivity
import com.face.ui.tcustom.CustomStartActivity
import com.face.ui.thug.HugStartActivity
import com.face.ui.tkiss.KissStartActivity
import com.face.ui.toutfit.OutfitStartActivity
import com.face.ui.txtai.TxtImgStartActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.MainToolDialog
import com.face.viewmodel.fragment.ExploreViewModel1
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity


class ExploreFragment1 : BaseBindingFragment<FragmentExplore1Binding, ExploreViewModel1>(
    R.layout.fragment_explore1,
    ExploreViewModel1::class.java
) {


    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
            mModel.exploreListAdapter.setItemOffsets(outRect, position)
        }
    }


    override fun init(savedInstanceState: Bundle?) {
        bar { transparent() }
        GVM.INSTANT.isVisibleShare.observe(this, Observer {
            mModel.getData()
        })
        mBinding?.apply {
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.recycledViewPool.setMaxRecycledViews(AiFaceBean.ITEM_TYPE_LIKE, 10)
//            recyclerView.setItemViewCacheSize(20)
            recyclerView.addOnScrollListener(onScrollListener)
//            ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
//                val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
//                refreshLayout.setFooterInsetStartPx(bottom + 48.dp)
//                insets
//            }
//            if (ViewCompat.isLaidOut(root)) {
//                ViewCompat.requestApplyInsets(root)
//            }
        }

        mModel.homeRefreshing.observe(this) {
            if (!it) {
                mModel.exploreListAdapter = ExploreListAdapter().apply {
                    onMoreClick = this@ExploreFragment1.onMoreClick
                    onToolClick = this@ExploreFragment1.onToolClick
                }
                mModel.exploreListAdapter.onMoreToolClick = {
                    (activity as? MainActivity)?.setShowIndex(2)
                }
                mModel.exploreListAdapter.notifyItemChanged(0)
            }
        }

        mModel.liketitle = getStringX(R.string.frament_explore_liketitle)
        mModel.hottxt = getStringX(R.string.hot_txt)
        mModel.seeall = getStringX(R.string.see_all)

        mModel.homeRefreshing.value = true
//        mModel.getData()
    }


    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                SCROLL_STATE_IDLE -> {//不滚动时的状态，通常会在滚动停止时监听到此状态
                    GVM.INSTANT.viewShowBtn.value = true
                }

                SCROLL_STATE_TOUCH_SCROLL -> {//正在滚动的状态
                    GVM.INSTANT.viewShowBtn.value = false
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

//            //获取屏幕第一个item是多少
//            val layoutManager: StaggeredGridLayoutManager =
//                recyclerView.layoutManager as StaggeredGridLayoutManager
//            var mFirstVisibleItems: IntArray? = null
//            mFirstVisibleItems = layoutManager.findFirstVisibleItemPositions(mFirstVisibleItems)
//            val firstItemPosition = mFirstVisibleItems[0]
//
//            if (firstItemPosition-scolnum<-3){
//                scolnum=firstItemPosition
//                GVM.INSTANT.upShowBtn.postValue(true)
//            }
        }
    }


    private val onToolClick: (Int?) -> Unit = {
        when (it) {

            0 -> {
                onHint("video")
            }

            1 -> {
                onHint("image")
            }

            2 -> {
                openActivity<KissStartActivity>()
            }

            3 -> {
                openActivity<HugStartActivity>()
            }

            4 -> {
                openActivity<OutfitStartActivity>()
            }

            5 -> {
                openActivity<TxtImgStartActivity>()
            }

            10 -> {
                openActivity<MovesStartActivity>()
            }
            11 -> {
//                openActivity<PlayAIActivity>()
            }

            6 -> {
                (activity as? MainActivity)?.setShowIndex(2)
            }

            else -> {
                openActivity<AddStartActivity> {
                    putInt("tool_id", it ?: -1)
                }
            }

        }
    }

    fun onHint(type: String) {
        if (type == "image") {
            if (SPUtils.toolPicHint) {
                MainToolDialog(type).showIgnoreState(activity)
            } else {
                CustomStartActivity.jump(activity, type)
            }
        } else {
            if (SPUtils.toolVideoHint) {
                MainToolDialog(type).showIgnoreState(activity)
            } else {
                CustomStartActivity.jump(activity, type)
            }
        }
    }

    val onMoreClick: (RecommendBean.RecommendX?) -> Unit = {
        openActivity<DetailsActivity>() {
            it?.apply {
                putString("title", it.name)
                putString("type", it.type)
                putInt("openValue", it.open_value.toInt())
            }
        }
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Home_explore"
        VipActivity.jump(requireActivity())
    }

    fun onSearchClick() {
        openActivity<SearchActivity>()
    }

    override fun onDestroyView() {
        mBinding?.refreshLayout?.apply {
            setOnRefreshListener(null)
            setOnLoadMoreListener(null)
        }
        mBinding?.recyclerView?.apply {
            layoutManager = null
            adapter = null
        }
        super.onDestroyView()
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
            .addArgument(BR.layoutManager, layoutManager)
    }
}