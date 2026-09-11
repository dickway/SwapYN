package com.face.ui.share.frg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.FragmentTemplatesListBinding
import com.face.ui.fragment.CustomVideoTypeFragment
import com.face.util.GVM
import com.face.view.BaseHintDialog
import com.face.viewmodel.activity.CustomVideoViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.face.ui.fragment.BaseBindingFragment
import com.face.viewmodel.activity.ShareMeModel
import com.face.viewmodel.activity.SwapNewViewModel
import com.zzkj.structure.base.DataBindingArguments

class TemplatesListFragment :
    BaseBindingFragment<FragmentTemplatesListBinding, CustomVideoViewModel>(
        R.layout.fragment_templates_list,
        CustomVideoViewModel::class.java
    ) {

    companion object {
        @JvmStatic
        fun newInstance() =
            TemplatesListFragment()
    }

    //Activity的ViewModel
    val mModelActivity: ShareMeModel by lazy {
        getViewModel(ShareMeModel::class.java, requireActivity())
    }

    private val tabTitles = arrayListOf(
        "1min",
        "3mins",
        "5mins",
        "Picture"
    )
    private var customFragments = mutableListOf<CustomVideoTypeFragment>()
    val tabAdapter: FragmentStateAdapter by lazy {
        object : FragmentStateAdapter(
            requireActivity()
        ) {
            override fun getItemCount(): Int {
                return tabTitles.size
            }

            override fun createFragment(position: Int): Fragment {
                return customFragments[position]
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (customFragments.isEmpty()) {
            repeat(tabTitles.size) {
                customFragments.add(CustomVideoTypeFragment().apply {
                    arguments = Bundle().apply {
                        putString("param", tabTitles[it])  // 传递参数
                        putBoolean("isShare", true)  // 传递参数
                    }
                })
            }
        }
        mModelActivity.isShareRefresh.observe(this, Observer {
            if (it == 1) {
                onGetUserAiMedia()
            }
        })

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
            viewPager.offscreenPageLimit = 3
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
                    val fragment =
                        requireActivity().supportFragmentManager.findFragmentByTag("f$position") as? CustomVideoTypeFragment
                    val size = fragment?.mModel?.customFaceList?.value?.size ?: 0
                    GVM.INSTANT.showListSize.value = size
                    GVM.INSTANT.showDelete.value = false
                }
            })

        }

    }

    override fun onResume() {
        super.onResume()
        if (GVM.INSTANT.isShareActivity.value) {
            GVM.INSTANT.isShareActivity.value = false
            BaseHintDialog(
                getString(R.string.update_s),
                getString(R.string.share_now),
                getString(R.string.ok)
            ).showIgnoreState(requireActivity())
        }
    }

    fun onGetUserAiMedia() {
        val fragment =
            requireActivity().supportFragmentManager.findFragmentByTag("f${mBinding?.viewPager?.currentItem ?: 0}") as? CustomVideoTypeFragment
        fragment?.mModel?.getUserAiMedia()
    }

    /** 选中时更新背景与文字颜色 */
    private fun selectTab(position: Int) {
        mBinding?.tabLayout?.getTabAt(position)?.customView?.let { view ->
            val tabText = view.findViewById<TextView>(R.id.tvTab)
            tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))
            tabText.setBackgroundResource(R.drawable.tab_selected_bg)
        }
    }

    /** 取消选中时恢复未选中样式 */
    private fun unselectTab(position: Int) {
        mBinding?.tabLayout?.getTabAt(position)?.customView?.let { view ->
            val tabText = view.findViewById<TextView>(R.id.tvTab)
            tabText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorB2))
            tabText.setBackgroundResource(R.drawable.tab_unselected_bg)
        }
    }

    fun onSelectTab(position: Int) {
        LogUtils.e(">>>>>>$position")
        mBinding?.viewPager?.currentItem = position
    }


    fun onShowDelete() {
        if ((GVM.INSTANT.showListSize.value ?: 0) > 1) {
            GVM.INSTANT.showDelete.value = !GVM.INSTANT.showDelete.value
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}