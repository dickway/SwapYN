package com.a.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.a.R
import com.a.adapter.AMyAllFaceAdapter
import com.a.databinding.ActivityAmyfaceallBinding
import com.a.dialog.ASwapHintDialog
import com.a.viewmodel.AMyAllFaceViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.a.BR
import com.a.dialog.ARemoveFaceDialog
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast


class AMyFaceAllActivity : BaseBindingActivity<ActivityAmyfaceallBinding, AMyAllFaceViewModel>(
    R.layout.activity_amyfaceall,
    AMyAllFaceViewModel::class.java
) {
    private var myAllFaceAdapter = AMyAllFaceAdapter()

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            refreshLayout.setOnChildScrollUpCallback { _, _ -> recyclerView.canScrollVertically(-1) }
        }
        myAllFaceAdapter.onAddClick = {
            onAddClick()
        }
        myAllFaceAdapter.onDeleteClick = { data ->

            ARemoveFaceDialog { mModel.deleteUserPic(data) }
                .showIgnoreState(this)
        }
        mModel.myFaceList.observe(this) { list ->
            val hasFaces = list.any { !it.isSelect }
            mModel.isSelect.value = hasFaces
            if (!hasFaces) {
                mBinding?.editTxt?.setText(R.string.a_settings_edit)
                mModel.isShowDete = false
            }
            myAllFaceAdapter.showDelect(mModel.isShowDete)
        }
    }

    fun onShowDelete() {
        if (mModel.faceList.size > 0) {
            mModel.isShowDete = !mModel.isShowDete
            myAllFaceAdapter.showDelect(mModel.isShowDete)
            if (mModel.isShowDete) {
                mBinding?.editTxt?.setText(R.string.a_settings_done)
            } else {
                mBinding?.editTxt?.setText(R.string.a_settings_edit)
            }
        }
    }

    fun onAddClick() {
        //需要跨界面控制界面销毁的标识
        SPUtils.isMaterial = false
        if (SPUtils.openFace) {
            toAddFace()
        } else {
            SPUtils.openFace = true
            ASwapHintDialog(::toAddFace).showIgnoreState(this)
        }
    }

    fun toAddFace() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setLanguage(LanguageConfig.ENGLISH)
            .setMaxSelectNum(1)
            .isWebp(false)
            .isGif(false)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>,frament: Fragment) {
                    if (result.isNotEmpty()) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                frament.openActivity<AUploadFaceActivity> {
                                    putString("img_url", sandboxPath ?: availablePath)
                                }
                            } else {
                                toast("Image loading failed, please choose another image")
                            }
                        }
                    } else {
                        toast("Image loading failed, please choose another image")
                    }
                }

                override fun onCancel() {

                }

                override fun onCamera() {
                    openActivity<ACameraActivity>()
                }

                override fun onHint(hoACT: FragmentActivity) {
                    ASwapHintDialog().showIgnoreState(hoACT)
                }
            })
    }


    override fun onResume() {
        super.onResume()
        mBinding?.editTxt?.setText(R.string.a_settings_edit)
        mModel.isShowDete = false
        mModel.getUserPics()

    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapter, myAllFaceAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
