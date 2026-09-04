package com.face.ui

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import com.face.util.GVM
import com.face.view.DeteleDialog
import com.face.viewmodel.activity.MyFaceViewModel
import com.face.BR
import com.face.R
import com.face.adapter.myface.MyFaceAdapter
import com.face.bean.MyFaceImgBean
import com.face.databinding.ActivityMyfaceBinding
import com.face.util.SimpleItemTouchHelperCallback
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras


class MyFaceActivity : BaseBindingActivity<ActivityMyfaceBinding, MyFaceViewModel>(
    R.layout.activity_myface,
    MyFaceViewModel::class.java
) {

    private val myFaceAdapter: MyFaceAdapter = MyFaceAdapter()

    val typeName by intentExtras("type_name", "")
    override fun init(savedInstanceState: Bundle?) {
        mModel.typeName.value = typeName
        mModel.getUserPics()

        mBinding?.apply {
            tvTitle.text = typeName
            val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(myFaceAdapter);
            ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        }

        myFaceAdapter.onMove = { currentList ->
            mModel.updateUserPicSort(currentList)
        }

        myFaceAdapter.onDeleteClick = {
            if (it != null) DeteleDialog { onDeleteData(it) }.showIgnoreState(this)
        }

    }

    private fun onDeleteData(bean: MyFaceImgBean) {
        mModel.deleteUserPic(bean)
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapter, myFaceAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}