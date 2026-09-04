package com.face.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.face.ui.fragment.CollectResultFragment
import com.face.util.GVM
import com.face.R
import com.face.viewmodel.activity.CollectViewModel
import com.face.BR
import com.face.databinding.ActivityCollectBinding
import com.face.util.EventUtil
import com.zzkj.structure.base.DataBindingArguments

class CollectActivity : BaseBindingActivity<ActivityCollectBinding, CollectViewModel>(
    R.layout.activity_collect,
    CollectViewModel::class.java
) {
    private val tabTitles = arrayListOf(
        "Results",
        "Templates"
    )
    private var collectFragments = mutableListOf<CollectResultFragment>()

    //tab 更新后自动滚到开始, 因为默认会自动滚到中间
    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = collectFragments.size

            override fun getItem(position: Int): Fragment {
                return collectFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }
    }


    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("collection")
        initResult()
    }

    private fun initResult() {
        if (collectFragments.isEmpty()) {
            repeat(tabTitles.size) {
                collectFragments.add(CollectResultFragment.newInstance(tabTitles[it]))
            }
        }
        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 2
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}