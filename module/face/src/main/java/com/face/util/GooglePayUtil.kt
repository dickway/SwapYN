package com.face.util

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.ui.App
import com.face.R
import com.face.bean.VipSchemeBean
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
object GooglePayUtil : PurchasesUpdatedListener,
    BillingClientStateListener, PurchasesResponseListener {

    private var isUnavailable = false
    private var launchData: LaunchData? = null
    private var schemeBean: VipSchemeBean? = null

    //校验时因网络问题重试次数
    private var retryTimes = 0

    //正在连接中
    private var inConnecting = false

    //连接成功后自动发起支付
    var autoLaunchBillingAfterConnect = false

    //连接成功后检查历史订单
    private var autoQueryPurchaseAfterConnect = true

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(App.INSTANCE)
            .setListener(this)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .enableAutoServiceReconnection() // Add this line to enable reconnection
            .build()
    }

    fun init() {
        if (!billingClient.isReady && !inConnecting) {
            startConnection()
        }
    }

    fun destroy() {
        launchData = null
        billingClient.endConnection()
    }

    fun launchPay(bean: VipSchemeBean, listener: (Int, String) -> Unit) {
        retryTimes = 0
        schemeBean = bean

        val orderNo = launchData?.orderId ?: ""
        EventUtil.pay(bean, "commit", orderNo = orderNo)

        if (getCurActivity() == null) {
            payFail(listener, getStringX(R.string.fail))
            return
        }
        getCurActivity()?.showLoading()
        createOrder(bean.id, bean.productId, listener)
    }


    suspend fun queryDetails(type: String, vararg productIds: String) =
        withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(
                QueryProductDetailsParams
                    .newBuilder()
                    .setProductList(
                        List(productIds.size) {
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(productIds[it])
                                .setProductType(type)
                                .build()
                        }
                    ).build()
            )
        }


    private fun createOrder(
        schemeId: Int,
        sku: String,
        listener: (Int, String) -> Unit
    ) {
        getCurActivity()?.launchRequestOnIO({
            Repository.createNoPaymentOrder(schemeId.toString())
        }) {
            onSuccess = {
                if (it == null) {
                    payFail(listener, "Failed to create order")
                } else {
                    launchBilling(sku, it.orderId, listener)
                }
            }
            onFailed = { _, code, _ ->
                payFail(listener, "Failed to create order", code ?: -1)
            }
        }
    }

    private fun payFail(listener: ((Int, String) -> Unit)?, msg: String?, errorCode: Int = -1) {
        schemeBean?.let {
            val orderNo = launchData?.orderId ?: ""
            EventUtil.pay(
                it,
                "fail",
                msg ?: getStringX(R.string.fail),
                orderNo = orderNo
            )
        }
        listener?.invoke(errorCode, msg ?: getStringX(R.string.fail))
        schemeBean = null
        launchData = null
        getCurActivity()?.dismissLoading()
    }

    private fun startConnection() {
        inConnecting = true
        billingClient.startConnection(this)
    }

    private fun launchBilling(
        sku: String,
        orderId: String,
        listener: (Int, String) -> Unit
    ) {
        launchData = LaunchData(schemeBean?.subscription ?: 1, sku, orderId, listener)
        if (!billingClient.isReady) {
            autoLaunchBillingAfterConnect = true
            if (!inConnecting) {
                startConnection()
            }
        } else {
            innerLaunchBilling(launchData!!)
        }
    }

    private fun innerLaunchBilling(launchData: LaunchData) {
//        val orderNo = GooglePayUtil.launchData?.orderId ?: ""
//        val sku = GooglePayUtil.launchData?.sku ?: "forever"
//        startPay(orderNo,sku)
        val type: String =
            if (launchData.type == 0) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS

        autoLaunchBillingAfterConnect = false
        val curActivity = getCurActivity()
        if (curActivity == null) {
            payFail(GooglePayUtil.launchData?.listener, getStringX(R.string.fail))
            LogUtils.e("fail, activity is null")
            return
        }
        curActivity.launch {
            val (result, list) = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(
                    QueryProductDetailsParams
                        .newBuilder()
                        .setProductList(
                            listOf(
                                QueryProductDetailsParams.Product.newBuilder()
                                    .setProductId(launchData.sku)
                                    .setProductType(type)
                                    .build()
                            )
                        ).build()
                )
            }

            val offerToken =
                list?.getOrNull(0)?.subscriptionOfferDetails?.getOrNull(0)?.offerToken ?: ""
            if (result.responseCode != BillingClient.BillingResponseCode.OK
                || list.isNullOrEmpty()
            ) {
                payFail(GooglePayUtil.launchData?.listener, "Failed to query goods")
                LogUtils.e("fail, code:${result.responseCode},msg:${result.debugMessage}")
                return@launch
            }

            val billingResult = billingClient.launchBillingFlow(
                curActivity,
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            ProductDetailsParams.newBuilder().apply {
                                if (offerToken != "")
                                    setOfferToken(offerToken)
                            }.setProductDetails(list[0]).build()
                        )
                    )
                    .setObfuscatedAccountId(
                        "${
                            GVM.INSTANT.userInfo.value.userId
                                .takeIf { it > 0 }?.toString() ?: "not_login"
                        }|${if (schemeBean?.trialPriceTitle != "") 1 else 0}"

                    )
                    .setObfuscatedProfileId(launchData.orderId)
                    .build()
            )
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                payFail(
                    GooglePayUtil.launchData?.listener,
                    getStringX(R.string.fail)
                )
                LogUtils.e(billingResult.debugMessage)
            }
        }
    }

    private fun checkUpdate(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val purchaseToken = purchase.purchaseToken
            val orderId = purchase.accountIdentifiers?.obfuscatedProfileId
            if (orderId.isNullOrBlank()) {
                return
            }
            if (getCurActivity() == null) {
                payFail(
                    launchData?.listener,
                    "Verification failed, please try to refresh manually."
                )
                return
            }

            getCurActivity()?.launchRequestOnIO({
                Repository.checkOrder(
                    orderId,
                    purchaseToken
                )
            }) {
                onSuccess = {
                    val purchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(
                        purchaseParams
                    ) { billingResult: BillingResult ->
                        LogUtils.i("acknowledgePurchase:%s", billingResult)
                    }
                    schemeBean?.let {
                        val orderNo = launchData?.orderId ?: ""
                        EventUtil.pay(it, "success", orderNo = orderNo)
                        EventUtil.payPage(it, orderNo = orderNo)
//                        (id=12, day=365, zkday=0, name=1 Yearly, oldPrice=39.99, price=39.99, productId=1_year_discount, type=1, remark=1460, description=Cancel anytime in 'Google Play -> Payments & Subscriptions'., showPrice=0.32, oldGooglePrice=39.99, formattedPrice=US$0.32, subscription=1, trialPriceTitle=P3D, showUnit=US$, showCode=USD)
//                        val showPrice = it.showPrice.toDoubleOrNull() ?: 0.0
//                        EventUtil.logGooglePayEvent(purchase, showPrice, it.showCode)
                    }
                    val consumeParams =
                        ConsumeParams.newBuilder()
                            .setPurchaseToken(purchaseToken)
                            .build()

                    billingClient.consumeAsync(consumeParams) { _, s ->
                        LogUtils.i("consumeAsync:%s", s)
                    }

                    launchData?.listener?.invoke(
                        BillingClient.BillingResponseCode.OK,
                        getStringX(R.string.success)
                    )
                    schemeBean = null
                    launchData = null
                }
                onFailed = { _, t, _ ->
                    // 重试两次
                    if (retryTimes < 2 && t != null) {
                        retryTimes++
                        checkUpdate(purchase)
                    } else {
                        payFail(launchData?.listener, getStringX(R.string.fail))
                    }
                }
            }
        }
    }
    private fun queryPurchase() {
//        val type: String =
//            if (launchData?.type == 0) BillingClient.ProductType.INAPP else BillingClient.ProductType.SUBS
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            this
        )
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            this
        )
    }

    private fun getCurActivity(): BaseBindingActivity<*, *>? {
        var find: Activity?
        for (i in AppManager.allActivities.lastIndex downTo 0) {
            find = AppManager.allActivities[i]
            if (find is BaseBindingActivity<*, *>) {
                return find
            }
        }
        return null
    }

    //断开连接
    override fun onBillingServiceDisconnected() {
        inConnecting = false
    }

    //连接完成
    override fun onBillingSetupFinished(result: BillingResult) {
        inConnecting = false
        LogUtils.d(result)
        isUnavailable = when (result.responseCode) {

            BillingClient.BillingResponseCode.OK -> {
                if (autoQueryPurchaseAfterConnect) {
                    queryPurchase()
                }
                if (autoLaunchBillingAfterConnect && launchData != null) {
                    innerLaunchBilling(launchData!!)
                }
                false
            }

            else -> {
                payFail(
                    launchData?.listener,
                    getStringX(R.string.fail),
                    result.responseCode
                )
                true
            }
        }
    }

    override fun onQueryPurchasesResponse(result: BillingResult, list: List<Purchase?>) {
        LogUtils.d(list)
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            autoQueryPurchaseAfterConnect = false
            list.filterNotNull().forEach { checkUpdate(it) }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase?>?) {
        LogUtils.i("onPurchasesUpdated $result")
        if (result.responseCode == BillingClient.BillingResponseCode.OK
            && !purchases.isNullOrEmpty()
        ) {
            for (purchase in purchases) {
                if (purchase != null && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    checkUpdate(purchase)
                }
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            schemeBean?.let {
                val orderNo = launchData?.orderId ?: ""
                EventUtil.pay(it, "cancel", "pay_cancel", orderNo = orderNo)
            }
            launchData?.listener?.invoke(result.responseCode, getStringX(R.string.cancel))
            schemeBean = null
            launchData = null
            getCurActivity()?.dismissLoading()
        } else {
            payFail(
                launchData?.listener,
                getStringX(R.string.fail),
                result.responseCode
            )
        }
    }

    private data class LaunchData(
        var type: Int,
        var sku: String,
        var orderId: String,
        var listener: (Int, String) -> Unit
    )
}