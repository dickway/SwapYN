package com.face.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.face.ui.fragment.HistoryResultFragment
import com.face.util.GVM
import com.face.R
import com.face.viewmodel.activity.HistoryViewModel
import com.face.BR
import com.face.databinding.ActivityHistoryBinding
import com.face.util.EventUtil
import com.zzkj.structure.base.DataBindingArguments

class HistoryActivity : BaseBindingActivity<ActivityHistoryBinding, HistoryViewModel>(
    R.layout.activity_history,
    HistoryViewModel::class.java
) {
    private val tabTitles = arrayListOf(
        "All",
        "Picture",
        "Video"
    )
    private var historyFragments = mutableListOf<HistoryResultFragment>()


    //tab 更新后自动滚到开始, 因为默认会自动滚到中间
//    private var isAutoScrollTabToFirst = true
    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = historyFragments.size

            override fun getItem(position: Int): Fragment {
                return historyFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        initResult()
        EventUtil.inPage("history")
    }

    private fun initResult() {
        if (historyFragments.isEmpty()) {
            if (GVM.INSTANT.isShowTool.value != 0) {
                repeat(tabTitles.size) {
                    historyFragments.add(HistoryResultFragment.newInstance(tabTitles[it]))
                }
            }else{
                historyFragments.add(HistoryResultFragment.newInstance("All"))
                mBinding?.tabLayout?.visibility = View.GONE
                mBinding?.viewLine?.visibility = View.GONE
            }


        }
        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 3
            tabLayout.setupWithViewPager(viewPager)
//            viewPager.addOnPageChangeListener(onPageChangeListener)
        }
    }

//    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
//        override fun onPageScrolled(
//            position: Int,
//            positionOffset: Float,
//            positionOffsetPixels: Int
//        ) {
//
//        }
//
//        override fun onPageSelected(position: Int) {
//            isAutoScrollTabToFirst = false
//        }
//
//        override fun onPageScrollStateChanged(state: Int) {
//        }
//    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}