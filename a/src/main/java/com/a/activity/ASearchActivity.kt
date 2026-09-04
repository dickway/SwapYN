package com.a.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.postDelayed
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a.databinding.ActivityAsearchBinding
import com.a.viewmodel.ASearchViewModel
import com.a.R
import com.a.BR
import com.face.bean.SearchHistoryBean
import com.face.database.AppDatabase
import com.face.ui.App
import com.face.util.EventUtil
import com.face.util.GVM
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.KeyboardUtil
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.setUpWithPaging3Adapter
import com.zzkj.structure.util.ktx.setVisibleIfNot

class ASearchActivity : BaseBindingActivity<ActivityAsearchBinding, ASearchViewModel>(
    R.layout.activity_asearch, ASearchViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("search")

        mModel.historyAdapter.onItemClick = onHistoryClick
        onBackPressedDispatcher.addCallback(this) {
            if (mModel.input.value.isNullOrBlank()) {
                super.onBackPressedDispatcher
            } else {
                onClearInputClick()
            }
        }

        mModel.input.observe(this) {
            if (it.isNullOrBlank()) {
                mModel.showIndex.setIfNot(0)
            }
        }

        mBinding?.edit?.run {
            isFocusable = true
            requestFocus()
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    mModel.getKeywords().takeIf { it.isNotBlank() }?.let {
                        if (mModel.input.value.isNullOrBlank()) {
                            autoFillAndSearch(it)
                        } else {
                            startSearch(it)
                        }
                    }
                }
                false
            }
            setOnFocusChangeListener { _, hasFocus ->
//                mModel.isFocus.value = hasFocus
            }
            WindowCompat.getInsetsController(window, this).show(WindowInsetsCompat.Type.ime())
        }

        mModel.showIndex.observe(this) {
            showIndex(it)
        }

        mBinding?.apply {
            refreshLayout.setUpWithPaging3Adapter(mModel.faceAdapter)
        }
        mModel.faceAdapter.addLoadStateListener(loadStateListener)

    }



    private val onHistoryClick: (View, SearchHistoryBean?, Int) -> Unit = { _, data, _ ->
        data?.name?.let {
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
                clearData()
            }
            clHistory.setVisibleIfNot(index == 0)
            clResult.setVisibleIfNot(index == 1)
        }
    }

    fun clearData() {
        mModel.isNoData.value = null
        if (mModel.faceAdapter.itemCount > 0) {
            mModel.faceAdapter.submitData(lifecycle, PagingData.empty())
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
        mModel.getSeatch()
        mBinding?.edit?.clearFocus()
        AppDatabase.INSTANT.searchHistoryDao()
            .insert(SearchHistoryBean(keywords, System.currentTimeMillis()))
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

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        if (it.refresh is LoadState.NotLoading) {
            mModel.isNoData.setIfNot(mModel.faceAdapter.itemCount == 0)
        }
    }


    private val historyLayoutManager = object : FlexboxLayoutManager(App.INSTANCE) {
//        override fun canScrollVertically(): Boolean {
//            return if (mModel.expandHistory.value == true) super.canScrollVertically() else false
//        }
    }.apply {
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.FLEX_START
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.historyLayoutManager, historyLayoutManager)
            .addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}