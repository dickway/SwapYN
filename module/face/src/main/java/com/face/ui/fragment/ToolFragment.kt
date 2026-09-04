package com.face.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.face.BR
import com.face.R
import com.face.databinding.FragmentToolBinding
import com.face.ui.SearchActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.viewmodel.fragment.ToolViewModel
import com.google.android.material.tabs.TabLayout
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity


class ToolFragment : BaseBindingFragment<FragmentToolBinding, ToolViewModel>(
    R.layout.fragment_tool, ToolViewModel::class.java
) {


    private var tabTitles = mutableListOf<String>()
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
        if (tabTitles.isEmpty()) {
            tabTitles =
                mutableListOf(getString(R.string.frament_tool1), getString(R.string.frament_tool2))
        }
        initResult()
    }

    private fun initResult() {
        if (toolFragments.isEmpty()) {
            toolFragments.add(ToolFragment1())
            toolFragments.add(ToolFragment2())
        }
        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 2
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
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    // 获取未选中 Tab 恢复正常字体
                    val tabTextView = tab.customView?.findViewById<TextView>(R.id.tv_top_item)
                    tabTextView?.setSelected(false)
                    tabTextView?.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))//加粗
                    tabTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)//直接用setTextSize(22)也一样
                    tabTextView?.invalidate();
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    // 当 Tab 被重新选中时可以处理一些逻辑
                }
            })
        }
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Home_tool"
        VipActivity.jump(requireActivity())
    }

    fun onSearchClick() {
        openActivity<SearchActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}