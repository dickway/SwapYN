package com.face.ui.tcustom

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.ActivityVideoCustomBinding
import com.face.ui.fragment.CustomVideoTypeFragment
import com.face.util.GVM
import com.face.viewmodel.activity.CustomVideoViewModel
import com.face.ui.BaseBindingActivity
import com.face.util.SPUtils
import com.face.view.WarningDialog
import com.zzkj.structure.base.DataBindingArguments


class CustomVideoActivity : BaseBindingActivity<ActivityVideoCustomBinding, CustomVideoViewModel>(
    R.layout.activity_video_custom,
    CustomVideoViewModel::class.java
) {
    private val tabTitles = arrayListOf(
        "1min",
        "3mins",
        "5mins"
    )

    private var customFragments = mutableListOf<CustomVideoTypeFragment>()
    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = customFragments.size

            override fun getItem(position: Int): Fragment {
                return customFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (!SPUtils.faceWarning) WarningDialog().showIgnoreState(mActivity)

        if (customFragments.isEmpty()) {
            repeat(tabTitles.size) {
                customFragments.add(CustomVideoTypeFragment().apply {
                    arguments = Bundle().apply {
                        putString("param", tabTitles[it])  // 传递参数
                    }
                })
            }
        }
        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 3
            tabLayout.setupWithViewPager(viewPager)
            viewPager.addOnPageChangeListener(onPageChangeListener)
        }

        GVM.INSTANT.showListSize.observe(this,Observer {
            if (it<2){
                mBinding?.editTxt?.text = getString(R.string.edit)
            }
        })
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            mBinding?.editTxt?.text = getString(R.string.edit)
            GVM.INSTANT.showDelete.value = false
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }

    fun onShowDelete() {
        if ((GVM.INSTANT.showListSize.value ?: 0) > 1) {
            GVM.INSTANT.showDelete.value = !GVM.INSTANT.showDelete.value
            mBinding?.editTxt?.text =
                if (GVM.INSTANT.showDelete.value) getString(R.string.cancel) else getString(R.string.edit)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}