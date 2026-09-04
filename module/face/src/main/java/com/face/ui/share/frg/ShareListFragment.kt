package com.face.ui.share.frg

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.BR
import com.face.R
import com.face.adapter.share.ShareMeAdapter
import com.face.databinding.FragmentShareListBinding
import com.face.ui.share.ShareMeActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.sharez.SharePointsDialog
import com.face.view.sharez.ShareStopDialog
import com.face.view.sharez.SharingStop1Dialog
import com.face.viewmodel.fragment.ShareListModel
import com.face.ui.fragment.BaseBindingFragment
import com.face.viewmodel.activity.ShareMeModel
import com.inmobi.media.S
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.arguments
import kotlin.getValue


class ShareListFragment : BaseBindingFragment<FragmentShareListBinding, ShareListModel>(
    R.layout.fragment_share_list,
    ShareListModel::class.java
) {

    private val type: String by arguments("type", "")

    //Activity的ViewModel
    val mModelActivity: ShareMeModel by lazy {
        getViewModel(ShareMeModel::class.java, requireActivity())
    }
    val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }
    var shareMeAdapter: ShareMeAdapter = ShareMeAdapter(onSonClick = { view, po, bean ->
        SharingStop1Dialog(onStopData1 = {
            ShareStopDialog(onStopData = {
                mModel.deleteMediaShare(bean) {
                    (requireActivity() as ShareMeActivity).mModel.isShareRefresh.value = 1
                }
            }).showIgnoreState(this)
        }).showIgnoreState(this)
    })

    companion object {
        @JvmStatic
        fun newInstance() = ShareListFragment()
    }

    override fun init(savedInstanceState: Bundle?) {

        mModel.videoType = if (type == "Video") "video" else "image"

        mModelActivity.isShareRefresh.observe(this, Observer {
            if (it == 0) {
                mModel.getShareData(isOwner = 1)
            }
        })

        mModel.getShareData(isOwner = 1)
        if (!SPUtils.isEnterFirstShare) {
            SPUtils.isEnterFirstShare = true
            SharePointsDialog().showIgnoreState(this)
        }
    }

    fun onShowPoints() {
        SharePointsDialog().showIgnoreState(this)
    }

    fun onShareFirst() {
        if (type == "Video") {
            (requireActivity() as ShareMeActivity).onSelectPicTab(0)
        } else {
            (requireActivity() as ShareMeActivity).onSelectPicTab(3)
        }
        (requireActivity() as ShareMeActivity).onSelectTab()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
    }
}