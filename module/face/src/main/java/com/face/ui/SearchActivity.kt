package com.face.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import com.face.bean.SearchHistoryBean
import com.face.util.GVM
import com.face.R
import com.face.viewmodel.activity.SearchViewModel
import com.face.database.AppDatabase
import com.face.ui.fragment.SearchResultFragment
import com.face.BR
import com.face.databinding.ActivitySearchBinding
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayoutMediator
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.base.adapter.CommonFragmentAdapter
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.KeyboardUtil
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.setVisibleIfNot

class SearchActivity : BaseBindingActivity<ActivitySearchBinding, SearchViewModel>(
    R.layout.activity_search, SearchViewModel::class.java
) {
    private val historyLayoutManager = object : FlexboxLayoutManager(App.INSTANCE) {
        override fun canScrollVertically(): Boolean {
            return if (mModel.expandHistory.value == true) super.canScrollVertically() else false
        }
    }.apply {
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.FLEX_START
    }

    private val fragmentAdapter: CommonFragmentAdapter by lazy {
        CommonFragmentAdapter(this)
    }

    //title,key
    private val resultTabTitles =
        if (GVM.INSTANT.isShowTool.value != 0)
            mutableListOf(
                Pair("All", ""),
                Pair("Video", "video"),
                Pair("Picture", "image"),
            )
        else
            mutableListOf(
                Pair("Picture", "image")
            )
    private var resultFragments = mutableListOf<SearchResultFragment>()

    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            if (mModel.input.value.isNullOrBlank()) {
                super.onBackPressedDispatcher
            } else {
                onClearInputClick()
            }
        }

        EventUtil.inPage("search")
        initHot()
        initResult()
        mModel.historyAdapter.onItemClick = onHistoryClick



        mModel.input.observe(this) {
            if (it.isNullOrBlank()) {
                mModel.showIndex.setIfNot(0)
            }
        }

        mBinding?.edit?.hint =
            if (GVM.INSTANT.isShowTool.value != 0)
                getString(R.string.search_videos_or_pictures)
            else
                getString(R.string.search_videos_or_pictures_a)

        mModel.refreshSearchHotsIfNeed()

        mBinding?.edit?.run {
            isFocusable = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    mModel.getKeywords()?.takeIf { it.isNotBlank() }?.let {
                        if (it.contains("token:")){
                            SPUtils.tokenUser=it.split(":")[1]
                            finish()
                        }else{
                            if (mModel.input.value.isNullOrBlank()) {
                                autoFillAndSearch(it)
                            } else {
                                startSearch(it)
                            }
                        }
                    }
                }
                false
            }
            setOnFocusChangeListener { _, _ ->
//                mModel.isFocus.value = hasFocus
            }
            WindowCompat.getInsetsController(window, this).show(WindowInsetsCompat.Type.ime())
        }
        mModel.expandHistory.observe(this) {
            mBinding?.includeHot?.rvHistory?.apply {
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    matchConstraintMaxHeight = if (it) 0 else 88.dp
                }
            }
        }
        mModel.showIndex.observe(this) {
            showIndex(it)
        }
    }

    private fun initHot() {
        mBinding?.includeHot?.rvHistory?.apply {
            itemAnimator = null
            viewTreeObserver?.addOnGlobalLayoutListener(onGlobalLayoutListener)
        }

        mModel.hotAdapter.onItemClick = { _, data, _ ->
            data?.apply {
                mModel.input.value = data.title
                startSearch(data.title)
            }
        }
    }

    private fun initResult() {
        if (resultFragments.isEmpty()) {
            repeat(resultTabTitles.size) {
                resultFragments.add(SearchResultFragment.newInstance(resultTabTitles[it].second))
            }
            fragmentAdapter.setFragments(resultFragments)
        }
        mBinding?.includeResult?.apply {
            viewPager.adapter = fragmentAdapter
            //保存状态不能复用fragment
//            viewPager.isSaveEnabled = false
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = resultTabTitles[position].first
            }.attach()
            viewPager.setCurrentItem(mModel.resultTabIndex, false)
            viewPager.offscreenPageLimit = resultTabTitles.size

            tabLayout.visibility =
                if (GVM.INSTANT.isShowTool.value != 0) View.VISIBLE else View.GONE
        }
    }


    private val onHistoryClick: (View, SearchHistoryBean?, Int) -> Unit = { _, data, _ ->
        data?.name?.let {
            mBinding?.includeResult?.viewPager?.currentItem = 0
            autoFillAndSearch(it)
        }
    }

    /**
     * 0 hot
     * 1 result
     */
    private fun showIndex(index: Int) {
        mBinding?.apply {
            if (index == 0) {
                resultFragments.forEach { it.clearData() }
            }
            includeHot.root.setVisibleIfNot(index == 0)
            includeResult.root.setVisibleIfNot(index == 1)
        }
    }

    private fun autoFillAndSearch(keywords: String) {
        mModel.input.value = keywords
        mBinding?.edit?.apply {
            postDelayed(100L) {
                // 移动光标到末尾
                text?.length?.let { setSelection(it) }
                startSearch(keywords)
            }
        }
    }

    private fun startSearch(keywords: String) {
        mBinding?.edit?.windowToken?.let { token ->
            KeyboardUtil.hideKeyboard(token)
        }
        EventUtil.search(keywords)
        mModel.startSearch.value = mModel.startSearch.value!! + 1
        mModel.showIndex.setIfNot(1)
        mBinding?.edit?.clearFocus()
        AppDatabase.INSTANT.searchHistoryDao()
            .insert(SearchHistoryBean(keywords, System.currentTimeMillis()))
    }


    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        mModel.showHistoryExpand.setIfNot(historyLayoutManager.flexLines.size > 2)
    }


    fun onClearInputClick() {
        mModel.input.value = ""
    }

    fun onClearHistoryClick() {
        AppDatabase.INSTANT.searchHistoryDao().clear()
    }


    override fun onStop() {
        super.onStop()
        mBinding?.apply {
            KeyboardUtil.hideKeyboard(edit.windowToken)
        }
    }

    override fun onDestroy() {
        mBinding?.includeHot?.rvHistory?.viewTreeObserver
            ?.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        super.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.historyLayoutManager, historyLayoutManager)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}