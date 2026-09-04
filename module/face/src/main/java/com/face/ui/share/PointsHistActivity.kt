package com.face.ui.share

import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.databinding.ActivityPointsHistBinding
import com.face.util.GVM
import com.face.viewmodel.activity.PointsHistModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments

class PointsHistActivity : BaseBindingActivity<ActivityPointsHistBinding, PointsHistModel>(
    R.layout.activity_points_hist,
    PointsHistModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        mModel.getUserItemLog()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}