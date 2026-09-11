package com.face.ui.share

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.adapter.other.FaceTypeAdapter
import com.face.databinding.FragmentShareZoneBinding
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.sharez.ShareTipsDialog
import com.face.viewmodel.fragment.ShareZoneModel
import com.face.ui.fragment.BaseBindingFragment
import com.face.view.BaseHintDialog
import com.face.view.BaseRedDialog
import com.face.viewmodel.InitViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import kotlinx.coroutines.delay

class ShareZoneFragment : BaseBindingFragment<FragmentShareZoneBinding, ShareZoneModel>(
    R.layout.fragment_share_zone,
    ShareZoneModel::class.java
) {


    //Activity的ViewModel
    val mModelActivity: InitViewModel by lazy {
        getViewModel(InitViewModel::class.java, requireActivity())
    }

    val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShareZoneFragment()
    }

    override fun init(savedInstanceState: Bundle?) {

        mModelActivity.isPermissionEnd.observe(this){
            showMyDialog()
        }

        mModel.typeList1 = listOf(getString(R.string.share_popular), getString(R.string.share_default))
        mModel.typeList2 = listOf(
            getString(R.string.share_all),
            getString(R.string.share_1min),
            getString(R.string.share_3min),
            getString(R.string.share_5min),
            getString(R.string.share_picture)
        )

        mModel.selectedName1.value = getString(R.string.share_popular)
        mModel.selectedName2.value = getString(R.string.share_all)
        mModel.isLoading.value = true

//        mModel.getShareRefresh()
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

        mModel.isHintVip.observe(this, Observer {
            if (it) {
                BaseRedDialog(
                    getString(R.string.dialog_sz_title),
                    getString(R.string.dialog_sz_content),
                    getString(R.string.dialog_sz_ok),
                    getString(R.string.dialog_sz_cancal),
                    unClick = "1",
                    onBtnOK = {
                        GVM.INSTANT.payPage.value = "Home_ShareZone_List"
                        VipActivity.jump(requireActivity())
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


    fun onShareTips1() {
        if (!GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = "Share_Popular_Btn"
            VipActivity.jump(requireActivity())
        } else {
            if (!SPUtils.isFirstClickShare) {
                ShareTipsDialog(true).showIgnoreState(this)
                SPUtils.isFirstClickShare = true
            } else {
                openActivity<ShareMeActivity>()
            }
        }
    }

    private fun showMyDialog() {
        if (!SPUtils.isFirstShare) {
            SPUtils.isFirstShare = true
            BaseHintDialog(
                getString(R.string.prompt),
                getString(R.string.share_tips_txt),
                getString(R.string.confirm)
            ).showIgnoreState(requireActivity())
        }
    }

    fun onShareTips2() {
        BaseHintDialog(
            getString(R.string.prompt),
            getString(R.string.share_tips_txt),
            getString(R.string.confirm)
        ).showIgnoreState(requireActivity())
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
            typeAdapter.selectIndex = mModel.typeList1.indexOf(mModel.selectedName1.value)
            typeAdapter.submitList(mModel.typeList1)
        } else {
            mModel.isType2.value = true
            view1.visibility = View.INVISIBLE
            rootview = mBinding?.type2!!
            typeAdapter.selectIndex = mModel.typeList2.indexOf(mModel.selectedName2.value)
            typeAdapter.submitList(mModel.typeList2)
        }

        typeAdapter.onItemClick = { _, data, _ ->
            if (!GVM.INSTANT.isVip.value && typeInt == 2) {
                GVM.INSTANT.payPage.value = "Share_Popular_Mins"
                VipActivity.jump(requireActivity())
            } else {
                if (typeInt == 1) mModel.isHotMedia = data == getString(R.string.share_popular)
                selecteListener(data, typeInt)
                mPopWindow.dismiss()
            }
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
            mModel.getShareRefresh()
        }
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
            .addArgument(BR.vm, mModel)
    }
}