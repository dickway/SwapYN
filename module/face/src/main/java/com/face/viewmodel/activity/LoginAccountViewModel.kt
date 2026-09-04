package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData

class LoginAccountViewModel : BaseViewModel() {


    val passwordVisible= NotNullMutableLiveData(false)
    val account = MutableLiveData("")
    val pwd = MutableLiveData("")
    val enableLogin = NotNullMediatorLiveData(false)

    private val enableEmailObserver = Observer<String?> {
        enableLogin.value = ((account.value?.length ?: 0) >= 6
                && (pwd.value?.length ?: 0) >= 6)
    }

    init {
        enableLogin.addSource(account, enableEmailObserver)
        enableLogin.addSource(pwd, enableEmailObserver)
    }
    fun onClearInputClick() {
        pwd.value = ""
    }
    fun onPasswordVisible() {
        passwordVisible.value = !passwordVisible.value
    }
}