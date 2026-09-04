package com.face.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.face.BR
import com.face.R
import com.face.adapter.swap.SwapImgVideoAdapter
import com.face.bean.TaskBean
import com.face.databinding.ActivityCompletionBinding
import com.face.ui.fragment.CompletionFragment1
import com.face.ui.fragment.CompletionFragment2
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.NoticeDialog
import com.face.viewmodel.activity.ComViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity


class CompletionActivity :
    BaseBindingActivity<ActivityCompletionBinding, ComViewModel>(
        R.layout.activity_completion,
        ComViewModel::class.java
    ) {

    private var shareFragments = mutableListOf(CompletionFragment1(), CompletionFragment2())

    private val tabAdapter: FragmentStateAdapter by lazy {
        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = shareFragments.size

            override fun createFragment(position: Int): Fragment {
                return shareFragments[position]
            }
        }
    }


    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            viewpager2.adapter = tabAdapter
        }
        mModel.isSlide.observe(this) {
            if (it) {
                mBinding?.apply {
                    if (viewpager2.currentItem < (viewpager2.adapter?.itemCount?.minus(1) ?: 0)) {
                        viewpager2.setCurrentItem(viewpager2.currentItem + 1, true)
                    }
                }
            }
        }
    }

    fun onNotice() {
        NoticeDialog().showIgnoreState(this)
    }

    fun onBackHome() {
        if (GVM.INSTANT.isShowTool.value == 0) {
            val mIntent = Intent().setClassName(
                App.INSTANCE.packageName,
                SPUtils.aMainName
            )
            startActivity(mIntent)
        } else {
            openActivity<MainActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        SPUtils.isMaterial = true
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}

