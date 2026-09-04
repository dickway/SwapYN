package com.face.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.face.BR
import com.face.R
import com.face.databinding.FragmentTemplateBinding
import com.face.ui.SearchActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.viewmodel.activity.NullViewModel
import com.google.android.material.tabs.TabLayout
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity


class TemplateFragment : BaseBindingFragment<FragmentTemplateBinding, NullViewModel>(
    R.layout.fragment_template,
    NullViewModel::class.java
) {

    private val tabTitles = arrayListOf(
        "All",
        "Video",
        "Picture"
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
            repeat(tabTitles.size) {
                toolFragments.add(VideoPictureFragment.newInstance(tabTitles[it]))
            }
        }

        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 3

            tabLayout.setupWithViewPager(viewPager)

            tabLayout.removeAllTabs()
            tabTitles.forEach {
                val tab = tabLayout.newTab()
                val view = LayoutInflater.from(context).inflate(R.layout.tab_template, null)
                view.findViewById<TextView>(R.id.tv_top_item).text = it
                tab.customView = view
                tabLayout.addTab(tab)
            }

            val defaultTab =
                tabLayout.getTabAt(tabLayout.selectedTabPosition)?.customView?.findViewById<TextView>(
                    R.id.tv_top_item
                )
            defaultTab?.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            defaultTab?.setSelected(true)
            defaultTab?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val tabTextView = tab.customView?.findViewById<TextView>(R.id.tv_top_item)
                    tabTextView?.setSelected(true)
                    tabTextView?.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))//加粗
                    tabTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)//直接用setTextSize(22)也一样
                    tabTextView?.invalidate();

                    // 获取选中 Tab 设置加粗字体
//                    val tabTextView = (tab.view as LinearLayout).getChildAt(1) as TextView
//                    tabTextView.setSelected(true)
//                    tabTextView.setTypeface(null, Typeface.BOLD)
//                    tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)

                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val tabTextView = tab.customView?.findViewById<TextView>(R.id.tv_top_item)
                    tabTextView?.setSelected(false)
                    tabTextView?.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))//加粗
                    tabTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)//直接用setTextSize(22)也一样
                    tabTextView?.invalidate();
                    // 获取未选中 Tab 恢复正常字体
//                    val tabTextView = (tab.view as LinearLayout).getChildAt(1) as TextView
//                    tabTextView.setTypeface(null, Typeface.NORMAL)
//                    tabTextView.setSelected(false)
//                    tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    // 当 Tab 被重新选中时可以处理一些逻辑
                }
            })
        }
    }


    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Home_template"
        VipActivity.jump(requireActivity())
    }

    fun onSearchClick() {
        openActivity<SearchActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}