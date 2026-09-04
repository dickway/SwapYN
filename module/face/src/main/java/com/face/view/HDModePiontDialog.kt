package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.databinding.DialogHdmodeBinding
import com.face.ui.VipActivity
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.openActivity


class HDModePiontDialog @JvmOverloads constructor(
    private val usePionts: Int = 0,
    private val onSeleteHd: ((Boolean, Int) -> Unit)? = null
) : BaseBindingDF<DialogHdmodeBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    var originalPionts = 0
    var useNewPionts = 0
    var strContent = ""
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        originalPionts =
            if (SPUtils.isHdFace && usePionts >= SPUtils.hdPoints && GVM.INSTANT.userInfo.value.vipLv != 5) {//还原添加过后的算力点
                usePionts - SPUtils.hdPoints
            } else {
                usePionts
            }
        initView()
        GVM.INSTANT.userInfo.observe(this, {
            if (it.vipLv == 5) {
                initView()
            }
        })
    }

    fun initView() {
        mBinding?.apply {
            //hd模式永久会员不用增加积分
            useNewPionts = if (GVM.INSTANT.userInfo.value.vipLv != 5) {
                tvPrice3.visibility = View.GONE
                originalPionts + SPUtils.hdPoints
            } else {
                if (originalPionts==0){
                    tvPrice3.visibility = View.GONE
                }else{
                    tvPrice3.visibility = View.VISIBLE
                }
                originalPionts
            }
            if (originalPionts == 0) {//普通模式不消耗算力点,积分隐藏
                imgPiont1.visibility = View.GONE
                tvPrice1.visibility = View.GONE
            }
            if (useNewPionts == 0) {//hd模式不消耗,积分隐藏
                imgPiont2.visibility = View.GONE
                tvPrice2.visibility = View.GONE
            }


            if (GVM.INSTANT.userInfo.value.vipLv != 5) {//不是永久会员显示跳转
                strContent = if (originalPionts == 0) {
                    getString(R.string.hd_mode_content2)
                } else {
                    getString(
                        R.string.hd_mode_content1,
                        originalPionts.toString()
                    )
                }
                tvTitle1.visibility = View.VISIBLE
                tvTitle2.visibility = View.GONE
            } else {
                strContent = getString(R.string.hd_mode_vip, SPUtils.hdPoints.toString())
                tvTitle1.visibility = View.GONE
                tvTitle2.visibility = View.VISIBLE
            }
            tvContent.text = strContent

            tvPrice1.text = originalPionts.toString()
            tvPrice2.text = useNewPionts.toString()
            tvPrice3.text = (originalPionts + SPUtils.hdPoints).toString()
            if (SPUtils.isHdFace) {//hd模式选中显示
                standardMode.setBackgroundResource(getColorX(R.color.colorTransparent))
                HDMode.setBackgroundResource(R.drawable.bg_rounded_btn_corners16)
                imgg1.visibility = View.INVISIBLE
                imgg2.visibility = View.VISIBLE
            } else {//普通模式选中显示
                standardMode.setBackgroundResource(R.drawable.bg_rounded_btn_corners16)
                HDMode.setBackgroundColor(getColorX(R.color.colorTransparent))
                imgg1.visibility = View.VISIBLE
                imgg2.visibility = View.INVISIBLE
            }
        }
    }

    fun onConfirmClick(ishd: Boolean) {
        dismissAllowingStateLoss()
        SPUtils.isHdFace = ishd
        val pionts = if (ishd) useNewPionts else originalPionts
        onSeleteHd?.invoke(ishd, pionts)
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Swap_HD"
        VipActivity.jump(requireActivity())
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}