package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData

class ACreateAccountViewModel : BaseViewModel() {

    val passwordVisible= NotNullMutableLiveData(false)
    val passwordVisible2= NotNullMutableLiveData(false)
    val account = MutableLiveData("")
    val pwd = MutableLiveData("")
    val pwd2 = MutableLiveData("")
    val enableLogin = NotNullMediatorLiveData(false)

    val isErrer = MutableLiveData(false)

    private val enableEmailObserver = Observer<String?> {
        enableLogin.value = ((account.value?.length ?: 0) >= 6
                && (pwd.value?.length ?: 0) >= 6
                && (pwd2.value?.length ?: 0) >= 6)
    }

    init {
        enableLogin.addSource(account, enableEmailObserver)
        enableLogin.addSource(pwd, enableEmailObserver)
        enableLogin.addSource(pwd2, enableEmailObserver)
    }
    fun onClearInputClick() {
        pwd.value = ""
    }
    fun onPasswordVisible() {
        passwordVisible.value = !passwordVisible.value
    }
    fun onClearInputClick2() {
        pwd2.value = ""
    }
    fun onPasswordVisible2() {
        passwordVisible2.value = !passwordVisible2.value
    }
}