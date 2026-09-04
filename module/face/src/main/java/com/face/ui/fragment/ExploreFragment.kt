package com.face.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.face.BR
import com.face.R
import com.face.databinding.FragmentExploreBinding
import com.face.ui.SearchActivity
import com.face.ui.SigninActivity
import com.face.ui.VipActivity
import com.face.ui.tcustom.CustomStartActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.CustomDialog
import com.face.view.MainToolDialog
import com.face.viewmodel.fragment.ExploreViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity


class ExploreFragment : BaseBindingFragment<FragmentExploreBinding, ExploreViewModel>(
    R.layout.fragment_explore,
    ExploreViewModel::class.java
) {

    private val tabTitles = arrayListOf(
        getStringX(R.string.frament_explore1),
        getStringX(R.string.frament_explore2)
    )
    private var toolFragments = mutableListOf<Fragment>()

    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            childFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = toolFragments.size

            override fun getItem(position: Int): Fragment {
                return toolFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }
    }


    override fun init(savedInstanceState: Bundle?) {
        if (toolFragments.isEmpty()) {
            toolFragments.add(ExploreFragment1())
            if (GVM.INSTANT.isShowTool.value != 0) {
                toolFragments.add(ExploreFragment2())
            } else {
                mBinding?.tabLayout?.visibility = View.GONE
                mBinding?.views1?.visibility = View.GONE
                mBinding?.collapsingToolbarLayout?.apply {
                    // 获取 AppBarLayout 的 LayoutParams
                    val params = layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = 0;  // 没有滚动效果
                    this.setLayoutParams(params)
                }
                mBinding?.tabLayout?.visibility = View.GONE
                mBinding?.views1?.visibility = View.GONE
                mBinding?.collapsingToolbarLayout?.apply {
                    // 获取 AppBarLayout 的 LayoutParams
                    val params = layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = 0;  // 没有滚动效果
                    this.setLayoutParams(params)
                }
            }
        }
        if (GVM.INSTANT.isShowTool.value==0) mBinding?.customLin?.visibility = View.GONE
        GVM.INSTANT.viewShowBtn.observe(this) {
            if (GVM.INSTANT.isShowTool.value!=0) {
                if (it) {//GVM.INSTANT.isShowTool.value != 0 &&
                    mBinding?.customLin?.visibility = View.VISIBLE
                } else {
                    mBinding?.customLin?.visibility = View.GONE
                }
            }
        }


        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 2
            viewPager.addOnPageChangeListener(onPageChangeListener)
            tabLayout.setupWithViewPager(viewPager)
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    // 获取选中 Tab 设置加粗字体
//                    val tabTextView = (tab.view as LinearLayout).getChildAt(1) as TextView
//                    tabTextView.setTypeface(null, Typeface.BOLD)
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab) {
//                    // 获取未选中 Tab 恢复正常字体
//                    val tabTextView = (tab.view as LinearLayout).getChildAt(1) as TextView
//                    tabTextView.setTypeface(null, Typeface.NORMAL)
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab) {
//                    // 当 Tab 被重新选中时可以处理一些逻辑
//                }
//            })
        }
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            GVM.INSTANT.viewShowBtn.value = position == 0
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Home_explore"
        VipActivity.jump(requireActivity())
    }

    fun onSearchClick() {
        openActivity<SearchActivity>()
    }

    fun onFeedback() {
        openActivity<SigninActivity>()
    }

    fun onCustom() {
        CustomDialog(
            onPhotoAct = { onHint("image") },
            onVideoAct = { onHint("video") }
        ).showIgnoreState(activity)
    }

    fun onHint(type: String) {
        if (type == "image") {
            if (SPUtils.toolPicHint) {
                MainToolDialog(type).showIgnoreState(activity)
            } else {
                CustomStartActivity.jump(mActivity, type)
            }
        } else {
            if (SPUtils.toolVideoHint) {
                MainToolDialog(type).showIgnoreState(activity)
            } else {
                CustomStartActivity.jump(mActivity, type)
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}