package com.face.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.adapter.other.FaceAdapter
import com.face.databinding.FragmentCompletion2Binding
import com.face.util.GVM
import com.face.viewmodel.fragment.CompletionViewModel
import com.zzkj.structure.base.DataBindingArguments

class CompletionFragment2 : BaseBindingFragment<FragmentCompletion2Binding, CompletionViewModel>(
    R.layout.fragment_completion2, CompletionViewModel::class.java
) {
    val faceAdapter = FaceAdapter()

    override fun init(savedInstanceState: Bundle?) {
        mModel.getUserLikeMedia()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapter, faceAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}