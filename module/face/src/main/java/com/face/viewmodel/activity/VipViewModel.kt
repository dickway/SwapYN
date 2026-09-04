package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.face.net.Repository
import com.face.R
import com.face.adapter.other.VipBannerAdapter
import com.face.adapter.other.VipSchemeAdapter
import com.face.bean.GooglePriceBean
import com.face.bean.VipBannerBean
import com.face.bean.VipSchemeBean
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class VipViewModel : BaseViewModel() {
    var isClickWeek=false//是否点击了周订阅
    var isTime=false//倒计时是否结束
    var isOkClose = false//是否打开优惠界面

    val refreshing = MutableLiveData<Boolean>()
    val selectedScheme = MutableLiveData<VipSchemeBean>()//选择的会员类型
    val vipSchemeAdapter = VipSchemeAdapter()
    val schemes = NotNullMutableLiveData(SPUtils.vipSchemes)//带缓存的会员列表
    var schemesNew = listOf<VipSchemeBean>()
    fun refresh() {
        refreshSchemes()
    }

    private fun refreshSchemes() {
        if (selectedScheme.value == null && schemes.value.isNotEmpty()) {
            selectedScheme.postValue(schemes.value[0])
        }
        launchRequestOnIO({ Repository.getVipScheme() }) {
            onSuccess = {
                if (!it.isNullOrEmpty()) {
                    val listGoogle = it.toMutableList()
                    if (SPUtils.vipLifetime.isNotEmpty()) {
                        val vipBean =
                            MoshiHelper.adapter(VipSchemeBean::class.java)
                                .fromJson(SPUtils.vipLifetime)
                                ?: VipSchemeBean()
                        listGoogle.add(vipBean)
                    }

//                   val list=it.toMutableList().apply {
//                        add(VipSchemeBean(
//                            id=7,
//                            name="1 Month Test",
//                            day=365,
//                            remark="1460",
//                            productId="1_year_discount_free"
//                        ))
//                    }
                    GooglePriceBean(listGoogle)
                }
            }
            onStart = { refreshing.value = true }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
            onComplete = { refreshing.value = false }
        }
    }

    fun discountBean() {
        val discountProductId = SPUtils.vipDiscount.split("|").getOrNull(1) ?: ""
        val discountQueryId = SPUtils.vipDiscount.split("|").getOrNull(2)
        if (discountProductId.isNotEmpty()) {
            val discountBean = VipSchemeBean()
            discountBean.productId = discountProductId
            discountBean.id = discountQueryId.toIntOrZero()
            launch(Dispatchers.IO) {
                val vipAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.SUBS,
                        discountProductId
                    )
                }
                val (_, vipDetails) = vipAsync.await()
                GooglePriceBean.toBeans(vipDetails)?.let { list ->
                    list.forEach { bean ->//根据google返回信息更新数据
                        if (discountBean.productId == bean.productId) {
                            discountBean.formattedPrice = bean.formattedPrice
                            discountBean.showPrice = bean.price
                            discountBean.showUnit = bean.unit
                            discountBean.showCode = bean.currencyCode
                            discountBean.oldGooglePrice = bean.oldGooglePrice
                            discountBean.trialPriceTitle = bean.trialTitle
                        }
                    }
                }
                SPUtils.discountSchemes = discountBean//缓存数据，下次进入没有空白界面
            }
        }

    }


    private fun GooglePriceBean(list: List<VipSchemeBean>) {
        schemesNew = list//保存原始数据
        launch(Dispatchers.IO) {
            val subProductIds =
                list.filter { !it.getProductId() }.map { it.productId }.toTypedArray()
            val coinProductIds =
                list.filter { it.getProductId() }.map { it.productId }.toTypedArray()
            if (subProductIds.isNotEmpty()) {//通过google查询订阅
                val vipAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.SUBS,
                        *subProductIds
                    )
                }
                val (_, vipDetails) = vipAsync.await()
                GooglePriceBean.toBeans(vipDetails)?.let {
                    updateGooglePrice(it)
                }
            }
            if (coinProductIds.isNotEmpty()) {//通过google查询商品
                val coinAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.INAPP,
                        *coinProductIds
                    )
                }
                val (_, coinDetails) = coinAsync.await()
                GooglePriceBean.toBeans(coinDetails)?.let {
                    updateGooglePrice(it)
                }
            }
            SPUtils.vipSchemes = schemesNew//缓存数据，下次进入没有空白界面
            schemes.postValue(schemesNew)//刷新数据改变界面

            selectedScheme.postValue(schemesNew[0])//默认选中第一个
        }
    }

    private fun updateGooglePrice(list: List<GooglePriceBean>) {
        list.forEach { bean ->//根据google返回信息更新数据
            schemesNew.find { bean.productId == it.productId }?.let {
                it.formattedPrice = bean.formattedPrice
                it.showPrice = bean.price
                it.showUnit = bean.unit
                it.showCode = bean.currencyCode
                it.oldGooglePrice = bean.oldGooglePrice
                it.trialPriceTitle = bean.trialTitle
            }
        }
    }

}