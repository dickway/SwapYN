package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.adapter.other.BuyAdapter
import com.face.bean.GooglePriceBean
import com.face.bean.VipSchemeBean
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.collections.filter

class BuypointViewModel : BaseViewModel() {

    val buyAdapter = BuyAdapter()
    val selectedScheme = MutableLiveData<VipSchemeBean>()//选择的价格
    val buyList = NotNullMutableLiveData(
        SPUtils.buySchemes.filter {
            it.remark.isEmpty() || it.type == if (GVM.INSTANT.isVip.value) 2 else 1
        }
    )

    var schemesNew = listOf<VipSchemeBean>()

    //历史积分
    var oldPoint = GVM.INSTANT.userInfo.value.tflops
    var isDialog = false

    fun getTFLOPConfigs() {
        if (buyList.value.isNotEmpty())
            selectedScheme.value = buyList.value[0]
        launchRequestOnIO({ Repository.getTFLOPConfigs() }) {
            onSuccess = {
                if (it != null) {
                    GooglePriceBean(it)
                }
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }


    private fun GooglePriceBean(list: List<VipSchemeBean>) {
        schemesNew = list
        launch(Dispatchers.IO) {

            val subProductIds =
                list.filter { !it.getProductId() }.map { it.productId }.toTypedArray()
            val coinProductIds =
                list.filter { it.getProductId() }.map { it.productId }.toTypedArray()
            //查询商品原价
            val oldProductIds =
                list.filter { it.getProductId() && it.remark.isNotEmpty() }.map { it.remark }
                    .toTypedArray()

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
            if (oldProductIds.isNotEmpty()) {//通过google查询商品
                val oldAsync = async {
                    GooglePayUtil.queryDetails(
                        BillingClient.ProductType.INAPP,
                        *oldProductIds
                    )
                }
                val (_, oldDetails) = oldAsync.await()
                GooglePriceBean.toBeans(oldDetails)?.let {
                    composePrice(it)
                }
            }

            SPUtils.buySchemes=schemesNew
            val listNew = if (GVM.INSTANT.isVip.value) {
                schemesNew.filter { it.remark.isEmpty() || it.type == 2 }
            } else {
                schemesNew.filter { it.remark.isEmpty() || it.type == 1 }
            }
            buyList.postValue(listNew)//刷新数据

//            if (schemesNew.isNotEmpty())
//                selectedScheme.value=schemesNew[0]//默认选中第一个
        }
    }

    private fun updateGooglePrice(list: List<GooglePriceBean>) {
        list.forEach { bean ->//根据google返回信息更新数据
            schemesNew.find { bean.productId == it.productId }?.let {
                it.showPrice = bean.price
                it.showUnit = bean.unit
                it.showCode = bean.currencyCode
                it.oldGooglePrice = bean.oldGooglePrice
                it.trialPriceTitle = bean.trialTitle
            }
        }
    }


    private fun composePrice(list: List<GooglePriceBean>) {
        list.forEach { bean ->//根据google返回信息更新数据
            schemesNew.find {
                bean.productId == it.remark
            }?.let {
                it.oldShowPrice = bean.price
            }
        }
    }
}