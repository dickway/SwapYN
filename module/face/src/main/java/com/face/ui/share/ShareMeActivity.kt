package com.face.ui.share

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.face.BR
import com.face.R
import com.face.databinding.ActivityShareMeBinding
import com.face.ui.share.frg.TemplatesListFragment
import com.face.util.GVM
import com.face.viewmodel.activity.ShareMeModel
import com.face.ui.BaseBindingActivity
import com.face.ui.share.frg.SharingListFragment
import com.face.util.SPUtils
import com.face.view.WarningDialog
import com.zzkj.structure.base.DataBindingArguments

class ShareMeActivity : BaseBindingActivity<ActivityShareMeBinding, ShareMeModel>(
    R.layout.activity_share_me,
    ShareMeModel::class.java
) {
    private var tabTitles = mutableListOf<String>()
    private var shareFragments = mutableListOf<Fragment>()
    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = shareFragments.size

            override fun getItem(position: Int): Fragment {
                return shareFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (!SPUtils.faceWarning) WarningDialog().showIgnoreState(mActivity)
        if (tabTitles.isEmpty()) {
            tabTitles = mutableListOf(
                getString(R.string.reward_sharing),
                getString(R.string.reward_templates)
            )
        }
        shareFragments.add(SharingListFragment.newInstance())
        shareFragments.add(TemplatesListFragment.newInstance())
//        mModel.isShareRefresh.observe(this) {
//            if (it == 0) {
//                (shareFragments[0] as ShareListFragment).mModel.getShareData(isOwner = 1)
//            } else if (it == 1) {
//                (shareFragments[1] as TemplatesListFragment).onGetUserAiMedia()
//            }
//        }
        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 2
            tabLayout.setupWithViewPager(viewPager)
            viewPager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    mModel.isShowEdit.value = position != 0
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
        }

        GVM.INSTANT.showListSize.observe(this,Observer {
            if (it<2){
                mBinding?.tvEdit?.text = getString(R.string.edit)
            }
        })
    }

    fun onDelete() {
        (shareFragments[1] as TemplatesListFragment).onShowDelete()
    }

    fun onSelectTab() {
        mBinding?.viewPager?.currentItem = 1
    }

    fun onSelectPicTab(position: Int) {
        (shareFragments[1] as TemplatesListFragment).onSelectTab(position)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}