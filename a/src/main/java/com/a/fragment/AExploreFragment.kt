package com.a.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a.R
import com.a.databinding.FragmentAexploreBinding
import com.a.viewmodel.AExploreViewModel
import com.face.bean.AiFaceBean
import com.face.util.GVM

import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.a.BR
import com.a.activity.ASearchActivity
import com.a.activity.AVipActivity
import com.a.dialog.ACustomDialog
import com.a.dialog.AMainToolDialog
import com.face.ui.fragment.BaseBindingFragment
import com.face.ui.tcustom.CustomStartActivity
import com.face.util.SPUtils
import com.face.view.CustomDialog
import com.face.view.MainToolDialog
import com.zzkj.structure.util.ktx.openActivity

class AExploreFragment : BaseBindingFragment<FragmentAexploreBinding, AExploreViewModel>(
    R.layout.fragment_aexplore,
    AExploreViewModel::class.java
) {

    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
            mModel.exploreListAdapter.setItemOffsets(outRect, itemPosition)
        }
    }


    override fun init(savedInstanceState: Bundle?) {
//        bar { transparent() }
        if (!SPUtils.customShowUse)mBinding?.customLin?.visibility = View.GONE
        mBinding?.apply {
            recyclerView.addItemDecoration(itemDecoration)
            recyclerView.recycledViewPool.setMaxRecycledViews(AiFaceBean.ITEM_TYPE_BANNER, 2)
            recyclerView.recycledViewPool.setMaxRecycledViews(AiFaceBean.ITEM_TYPE_LIKE, 10)
            recyclerView.addOnScrollListener(onScrollListener)
        }
        mModel.homeRefreshing.postValue(true)
    }


    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (SPUtils.customShowUse) {
                when (newState) {
                    SCROLL_STATE_IDLE -> {//不滚动时的状态，通常会在滚动停止时监听到此状态
                        mBinding?.customLin?.visibility = View.VISIBLE
                    }

                    SCROLL_STATE_TOUCH_SCROLL -> {//正在滚动的状态
                        mBinding?.customLin?.visibility = View.GONE
                    }
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

            //类型布局距离顶部的距离
//            mBinding?.apply {
//                //标题显示
//                if (firstItemPosition < 1) {
//                    conTitle.visibility = View.VISIBLE
//                }
//
//                //上滑显示标题
//                if (dy < -30) {
//                    conTitle.visibility = View.VISIBLE
//                }
//
//                //下滑显示标题
//                if (dy > 30 && firstItemPosition > 0) {
//                    conTitle.visibility = View.GONE
//                }
//            }

        }
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "AExplore"
        openActivity<AVipActivity>()
    }

    fun onSearchClick() {
        openActivity<ASearchActivity>()
    }

    fun onCustom() {
        ACustomDialog(
            onPhotoAct = { onHint("image") },
            onVideoAct = { onHint("video") }
        ).showIgnoreState(activity)
    }

    fun onHint(type: String) {
        if (type == "image") {
            if (SPUtils.toolPicHint) {
                AMainToolDialog(type).showIgnoreState(activity)
            } else {
                CustomStartActivity.jump(mActivity, type)
            }
        } else {
            if (SPUtils.toolVideoHint) {
                AMainToolDialog(type).showIgnoreState(activity)
            } else {
                CustomStartActivity.jump(mActivity, type)
            }
        }
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