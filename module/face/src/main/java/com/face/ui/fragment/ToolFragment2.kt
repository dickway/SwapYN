package com.face.ui.fragment

import android.os.Bundle
import com.face.BR
import com.face.BuildConfig
import com.face.R
import com.face.bean.ToolFunzoneBean
import com.face.databinding.FragmentTool2Binding
import com.face.ui.live.LiveStartActivity
import com.face.ui.tadd.AddStartActivity
import com.face.ui.tage.AgeStartActivity
import com.face.ui.thug.HugStartActivity
import com.face.ui.tkiss.KissStartActivity
import com.face.ui.tmoves.MovesStartActivity
import com.face.ui.toutfit.OutfitStartActivity
import com.face.ui.txtai.TxtImgStartActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.viewmodel.fragment.ToolViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper

class ToolFragment2 : BaseBindingFragment<FragmentTool2Binding, ToolViewModel>(
    R.layout.fragment_tool2, ToolViewModel::class.java
) {
//    var tool2List: List<ToolFunzoneBean>? = MoshiHelper.convertJsonToList(SPUtils.funzoneList)
    var tool2List: List<ToolFunzoneBean> = emptyList()
    override fun init(savedInstanceState: Bundle?) {
        tool2List = safeParseToolList()

        mModel.tool2List.value = if (GVM.INSTANT.isShowTool.value != 0) {//筛选显示a还是b
            tool2List.filter {
                it.isAb.contains("b") &&
                        (!it.isDebug ||
                                it.showVersion.contains(BuildConfig.VERSION_CODE.toString()))&&
                        !it.putIn.contains(BuildConfig.VERSION_CODE.toString())
            }
        } else {
            tool2List.filter { it.isAb.contains("a") &&
                    (!it.isDebug ||
                            it.showVersion.contains(BuildConfig.VERSION_CODE.toString())) }
        }
        mModel.tool2ListAdapter.onItemClick = { _, bean, _ ->
            onToolClick(bean)
        }
    }

    private fun safeParseToolList(): List<ToolFunzoneBean> {
        return try {
            val json = SPUtils.funzoneList
            if (json.isBlank()) {
                emptyList()
            } else {
                MoshiHelper.convertJsonToList(json) ?: emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun onToolClick(bean: ToolFunzoneBean?) {
        when (bean?.id) {
            0 -> {
                openActivity<KissStartActivity>()
            }

            1 -> {
                openActivity<HugStartActivity>()
            }

            2 -> {
                openActivity<OutfitStartActivity>()
            }

            3 -> {
                openActivity<TxtImgStartActivity>()
            }

            4 -> {
                openActivity<AgeStartActivity>()
            }

            5 -> {
                openActivity<LiveStartActivity>()
            }
            10 -> {
                openActivity<MovesStartActivity>()
            }
            11 -> {
//                openActivity<PlayAIActivity>()
            }
            else -> {
                openActivity<AddStartActivity> {
                    putInt("tool_id", bean?.id ?: -1)
                }
            }

        }
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}