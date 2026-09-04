package com.face.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.adapter.other.FaceTypeAdapter
import com.face.databinding.FragmentVideoPictureBinding
import com.face.ui.SearchActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.viewmodel.fragment.VideoPictureViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.setIfNot

class VideoPictureFragment :
    BaseBindingFragment<FragmentVideoPictureBinding, VideoPictureViewModel>(
        R.layout.fragment_video_picture,
        VideoPictureViewModel::class.java
    ) {

    private val type: String by lazy {
        arguments?.getString("type") ?: ""
    }

    companion object {
        fun newInstance(type: String): VideoPictureFragment {
            val fragment = VideoPictureFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }


    override fun init(savedInstanceState: Bundle?) {
        mModel.selectedType = type
        mBinding?.apply {
            recyclerView.addOnScrollListener(onScrollListener)
        }
        mModel.getData()
    }

    // 记录滑动向下滑动距离
    private var totalDy = 0

    // 自然滑动
    private var naturalScrolling = false
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 && naturalScrolling) totalDy += dy //向下滑动距离
            if (dy < -15) {
                totalDy = 0
                mModel.showView.value = false
            }
            val topView: View? =
                recyclerView.layoutManager?.findViewByPosition(0)
            val offFourView = topView?.top
            mBinding?.apply {
                mModel.showView.value = totalDy > 80 //滑动距离300隐藏
                if (offFourView != null) {
                    if (offFourView < 5) {//滑动到顶部显示
                        totalDy = 0
                        mModel.showView.value = false
                    }
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            naturalScrolling = newState != 2//自然滚动不计算
        }
    }


    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Home_video"
        VipActivity.jump(requireActivity())
    }

    fun onSearchClick() {
        openActivity<SearchActivity>()
    }

    fun showTop() {
        totalDy = 0
        mModel.showView.value = !mModel.showView.value!!
    }

    fun onClickType1() {
        showPopupWindow(1)
    }


    fun onClickType2() {
        showPopupWindow(2)
    }

    private fun showPopupWindow(typeInt: Int) {
        //设置contentView
        val contentView: View =
            LayoutInflater.from(activity).inflate(R.layout.popuplayout_type, null)
        val mPopWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )
        mPopWindow.setContentView(contentView)
        //设置各个控件的点击响应
        val recyclerView: RecyclerView = contentView.findViewById(R.id.recyView)
        val typeAdapter = FaceTypeAdapter()
        recyclerView.adapter = typeAdapter
        val view1: View = contentView.findViewById(R.id.type1)
        val view2: View = contentView.findViewById(R.id.type2)
        val viewBottom: View = contentView.findViewById(R.id.viewBottom)
        viewBottom.setOnClickListener {
            mPopWindow.dismiss()
        }
        //显示PopupWindow
        var rootview: View? = null
        if (typeInt == 1) {
            view2.visibility = View.INVISIBLE
            rootview = mBinding?.type1!!
            mModel.isType1.value = true
            typeAdapter.selectIndex = mModel.typeList1.value.indexOf(mModel.selectedName1.value)
            typeAdapter.submitList(mModel.typeList1.value)
        } else {
            mModel.isType2.value = true
            view1.visibility = View.INVISIBLE
            rootview = mBinding?.type2!!
            typeAdapter.selectIndex = mModel.typeList2.value.indexOf(mModel.selectedName2.value)
            typeAdapter.submitList(mModel.typeList2.value)
        }

        typeAdapter.onItemClick = { _, data, _ ->
            selecteListener(data, typeInt)
            mPopWindow.dismiss()
        }
        mPopWindow.setOnDismissListener {
            mModel.isType1.value = false
            mModel.isType2.value = false
        }
        mPopWindow.showAsDropDown(rootview, 0, -6.dp, Gravity.BOTTOM);
    }

    private fun selecteListener(selected: String?, numType: Int) {
        if (selected != null) {
            when (numType) {
                1 -> mModel.selectedName1.value = selected
                2 -> mModel.selectedName2.value = selected
            }
            //类型选择后回到顶部
            val layoutManager: StaggeredGridLayoutManager =
                mBinding?.recyclerView?.layoutManager as StaggeredGridLayoutManager
            layoutManager.scrollToPositionWithOffset(0, 0)
            mModel.getData()
        }
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}