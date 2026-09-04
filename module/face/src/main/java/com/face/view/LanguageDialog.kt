package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogLanguageBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.singleClick


class LanguageDialog @JvmOverloads constructor(
    private val languageListener: ((Int) -> Unit)? = null
) : BaseBindingDF<DialogLanguageBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    maxHeight= (getScreenHeight()/4*3),
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true

) {
    var selectedNum = NotNullMutableLiveData(1)
    var oldNum = 1
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        val languageStr = SPbaseUtils.spLanguage
        if (languageStr == "en") {
            selectedNum.value = 1
            oldNum=1
        } else if (languageStr == "zh") {
            selectedNum.value = 2
            oldNum=2
        } else if (languageStr == "es") {
            selectedNum.value = 3
            oldNum=3
        }else if (languageStr == "de") {
            selectedNum.value = 4
            oldNum=4
        }else if (languageStr == "fr") {
            selectedNum.value = 5
            oldNum=5
        }else if (languageStr == "sv") {
            selectedNum.value = 6
            oldNum=6
        }else if (languageStr == "ar") {
            selectedNum.value = 7
            oldNum=7
        }else if (languageStr == "ko") {
            selectedNum.value = 8
            oldNum=8
        }else if (languageStr == "ja") {
            selectedNum.value = 9
            oldNum=9
        }else if (languageStr == "ku") {
            selectedNum.value = 10
            oldNum=10
        }else if (languageStr == "iw") {
            selectedNum.value = 11
            oldNum=11
        }else if (languageStr == "fa") {
            selectedNum.value = 12
            oldNum=12
        }else if (languageStr == "tr") {
            selectedNum.value = 13
            oldNum=13
        }else if (languageStr == "pt-br") {
            selectedNum.value = 14
            oldNum=14
        }else if (languageStr == "pt-pt") {
            selectedNum.value = 15
            oldNum=15
        }else if (languageStr == "zh-tw") {
            selectedNum.value = 16
            oldNum=16
        }else if (languageStr == "it") {
            selectedNum.value = 17
            oldNum=17
        }

        mBinding?.apply {
            linEn.singleClick {
                selectedNum.value = 1
            }
            linZh.singleClick {
                selectedNum.value = 2
            }
            linEs.singleClick {
                selectedNum.value = 3
            }
            linDe.singleClick {
                selectedNum.value = 4
            }
            linFr.singleClick {
                selectedNum.value = 5
            }
            linSv.singleClick {
                selectedNum.value = 6
            }
            linAr.singleClick {
                selectedNum.value = 7
            }
            linKo.singleClick {
                selectedNum.value = 8
            }
            linJa.singleClick {
                selectedNum.value = 9
            }
            linKu.singleClick {
                selectedNum.value = 10
            }
            linIw.singleClick {
                selectedNum.value = 11
            }
            linFa.singleClick {
                selectedNum.value = 12
            }
            linTr.singleClick {
                selectedNum.value = 13
            }
            linPtBr.singleClick {
                selectedNum.value = 14
            }
            linPt.singleClick {
                selectedNum.value = 15
            }
            linZhTW.singleClick {
                selectedNum.value = 16
            }
            linIt.singleClick {
                selectedNum.value = 17
            }
        }
    }


    fun onConfirmClick() {
        if (oldNum != selectedNum.value) {
            languageListener?.invoke(selectedNum.value)
        }
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}