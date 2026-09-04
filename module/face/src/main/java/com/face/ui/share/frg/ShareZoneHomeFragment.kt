package com.face.ui.share.frg

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.FragmentSharezoneHomeBinding
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.viewmodel.fragment.ShareZoneModel
import com.face.ui.fragment.BaseBindingFragment
import com.face.util.SPUtils
import com.face.view.BaseRedDialog
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.arguments
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import kotlinx.coroutines.delay
import kotlin.getValue

class ShareZoneHomeFragment() :
    BaseBindingFragment<FragmentSharezoneHomeBinding, ShareZoneModel>(
        R.layout.fragment_sharezone_home,
        ShareZoneModel::class.java
    ) {

    private val isHot: Boolean by arguments("isHot",false)

    val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun init(savedInstanceState: Bundle?) {
        mModel.isHotMedia = isHot
        mModel.isLoading.value = true
//        mModel.getShareRefresh(false)
        mModel.shareZeList.observe(this, Observer {
            if (mModel.page == 1 && it.size > 1) {
                mBinding?.recyclerView?.postDelayed({
                    val layoutManager =
                        mBinding?.recyclerView?.layoutManager as? StaggeredGridLayoutManager
                    layoutManager?.invalidateSpanAssignments()
                    mBinding?.recyclerView?.scrollToPosition(0)
                }, 200)
            }
        })

        mModel.isHintVip.observe(this, Observer{
            if (it) {
                BaseRedDialog(
                    getString(R.string.dialog_sz_title),
                    getString(R.string.dialog_sz_content),
                    getString(R.string.dialog_sz_ok),
                    getString(R.string.dialog_sz_cancal),
                    unClick = "1",
                    onBtnOK = {
                        GVM.INSTANT.payPage.value = "Home_ShareZone_List"
                        VipActivity.jump( requireActivity())
                    }
                ).showIgnoreState(mActivity)
            }
        })
        mModel.isLoading.observe(this, Observer {
            if (!it) {
                launch {
                    delay(500)
                    mBinding?.refreshLayout?.finishRefresh()
                }
            }
        })

        GVM.INSTANT.isVip.observe(this, Observer {
            if (it != mModel.isVipNew) {
                mModel.isVipNew = it
                mModel.getShareRefresh()
            }
        })
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}