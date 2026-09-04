package com.face.ui.share.frg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.FragmentSharingListBinding
import com.face.databinding.FragmentTemplatesListBinding
import com.face.ui.fragment.CustomVideoTypeFragment
import com.face.util.GVM
import com.face.view.BaseHintDialog
import com.face.viewmodel.activity.CustomVideoViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.face.ui.fragment.BaseBindingFragment
import com.face.viewmodel.activity.NullViewModel
import com.face.viewmodel.activity.ShareMeModel
import com.face.viewmodel.activity.SwapNewViewModel
import com.zzkj.structure.base.DataBindingArguments

class SharingListFragment :
    BaseBindingFragment<FragmentSharingListBinding, NullViewModel>(
        R.layout.fragment_sharing_list,
        NullViewModel::class.java
    ) {

    companion object {
        @JvmStatic
        fun newInstance() =
            SharingListFragment()
    }

    private val tabTitles = arrayListOf(
        "Video",
        "Picture"
    )

    private var sharingFragments = mutableListOf<ShareListFragment>()
    val tabAdapter: FragmentStateAdapter by lazy {
        object : FragmentStateAdapter(
            requireActivity()
        ) {
            override fun getItemCount(): Int {
                return tabTitles.size
            }

            override fun createFragment(position: Int): Fragment {
                return sharingFragments[position]
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (sharingFragments.isEmpty()) {
            repeat(tabTitles.size) {
                sharingFragments.add(ShareListFragment().apply {
                    arguments = Bundle().apply {
                        putString("type", tabTitles[it])  // 传递参数
                    }
                })
            }
        }

        mBinding?.apply {
            viewPager.adapter = tabAdapter  // 先设置 Adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabTitles[position]
//                // 为每个 Tab 设置自定义 View
//                val custom = layoutInflater.inflate(R.layout.share_tab_view, null)
//                val tvTab = custom.findViewById<TextView>(R.id.tvTab)
//                tvTab.text = tabTitles[position]
//                tab.customView = custom
            }.attach()
            viewPager.offscreenPageLimit = 2
//            selectTab(0)
            // 3. 监听选中和取消选中，动态更新背景和文字样式
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    val pos = tab.position
//                    viewPager.currentItem = pos
////                    selectTab(pos)
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab) {
////                    unselectTab(tab.position)
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab) {
//                    // 可选：处理重复点击
//                }
//            })
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    val fragment =
//                        requireActivity().supportFragmentManager.findFragmentByTag("f$position") as? CustomVideoTypeFragment
//                    val size = fragment?.mModel?.customFaceList?.value?.size ?: 0
//                    GVM.INSTANT.showDelete.value = false
                }
            })

        }

    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}