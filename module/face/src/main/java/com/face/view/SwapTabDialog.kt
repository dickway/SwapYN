package com.face.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.core.view.postDelayed
import androidx.lifecycle.Observer
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.adapter.swap.SwapChooseAdapter
import com.face.adapter.swap.SwapTabAdapter
import com.face.databinding.DialogSwaptabBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMediatorLiveData


class SwapTabDialog @JvmOverloads constructor(
    mediaType: String? = "",
    private val onToAct: (() -> Unit)? = null,
    private val onToPhoto: (() -> Unit)? = null
) : BaseBindingDF<DialogSwaptabBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    dimAmount = 0.1F,
    isBottomAnimation = false
) {
    val isHdModel = NotNullMediatorLiveData(SPUtils.isHdFace && mediaType == "video")

    private val swapTabAdapter: SwapTabAdapter = SwapTabAdapter()
    private val swapChooseAdapter: SwapChooseAdapter = SwapChooseAdapter()

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        GVM.INSTANT.selectNum.observe(viewLifecycleOwner, Observer { it ->
            val tabList = GVM.INSTANT.swapTabList.value
            if (!tabList.isNullOrEmpty() && it < tabList.size) {
                swapTabAdapter.selectIndex = it
                mBinding?.rlTab?.scrollToPosition(it)
                val numSel = tabList[it].isSelectBean
//                GVM.INSTANT.swapSelectList.value?.find { it.pic == numSel?.pic }?.let {
//                    LogUtils.e(">>>>>>>>>>select:$it")
//                    swapChooseAdapter.selectIndex =
//                        GVM.INSTANT.swapSelectList.value?.indexOf(it) ?: 0
//                }
                swapChooseAdapter.selectIndex = GVM.INSTANT.swapSelectList.value
                    ?.indexOfFirst { it.pic == numSel?.pic }
                    ?: 0 // 找不到返回 -1

                if (GVM.INSTANT.swapSelectList.value?.isNotEmpty() == true && GVM.INSTANT.swapSelectList.value!!.size > swapChooseAdapter.selectIndex) {
                    mBinding?.rlChoose?.postDelayed({
                        mBinding?.rlChoose?.scrollToPosition(swapChooseAdapter.selectIndex)
                    }, 200)
                }
                GVM.INSTANT.selectBean.value = numSel
            }
        })
        GVM.INSTANT.swapSelectList.observe(viewLifecycleOwner, Observer { list_it ->
            if (list_it.isNotEmpty()) {
                swapChooseAdapter.selectIndex =
                    list_it?.indexOfFirst { it.pic == GVM.INSTANT.selectBean.value?.pic }
                        ?: 0 // 找不到返回
//                swapChooseAdapter.selectIndex = list_it.indexOf(GVM.INSTANT.selectBean.value)
                swapTabAdapter.notifyDataSetChanged()
            }
        })

        swapTabAdapter.onItemClick = { _, bean, position ->
            if (bean?.isSelectBean == null) {
                swapChooseAdapter.selectIndex = -1
            } else {
                swapChooseAdapter.selectIndex =
                    GVM.INSTANT.swapSelectList.value?.indexOf(bean.isSelectBean)!!
                mBinding?.rlChoose?.post {
                    mBinding?.rlChoose?.scrollToPosition(swapChooseAdapter.selectIndex)
                }
            }
            GVM.INSTANT.selectBean.value = bean?.isSelectBean
            GVM.INSTANT.selectNum.value = position
        }

        swapChooseAdapter.onItemClick = { _, bean, _ ->
            val tabList = GVM.INSTANT.swapTabList.value
            val index = swapTabAdapter.selectIndex
            GVM.INSTANT.selectBean.value = bean
            if (!tabList.isNullOrEmpty() && index in tabList.indices) {
                if (GVM.INSTANT.swapTabList.value?.get(index)?.isSelectBean?.pic == bean?.pic) {
                    GVM.INSTANT.swapTabList.value?.get(index)?.isSelectBean = null
                } else {
                    GVM.INSTANT.swapTabList.value?.get(index)?.isSelectBean = bean
                }
                swapTabAdapter.notifyItemChanged(index)
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        GVM.INSTANT.isSwapR.value = true
        GVM.INSTANT.swapTabList.value = GVM.INSTANT.swapTabList.value?.toMutableList()
    }

    fun onProductionClick() {
        onToAct?.invoke()
    }


    fun onUploadClick() {
        //需要跨界面控制界面销毁的标识
        SPUtils.isMaterial = false
        if (SPUtils.privacyHint) {
            PrivacyDialog {
                if (SPUtils.openFace) {
                    selectorPhoto()
                } else {
                    SPUtils.openFace = true
                    SwapHintDialog(::selectorPhoto).showIgnoreState(activity)
                }
            }.showIgnoreState(this)
        } else {
            if (SPUtils.openFace) {
                selectorPhoto()
            } else {
                SPUtils.openFace = true
                SwapHintDialog(::selectorPhoto).showIgnoreState(activity)
            }
        }
    }

    fun selectorPhoto() {
        onToPhoto?.invoke()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapterTab, swapTabAdapter)
            .addArgument(BR.adapter, swapChooseAdapter)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}