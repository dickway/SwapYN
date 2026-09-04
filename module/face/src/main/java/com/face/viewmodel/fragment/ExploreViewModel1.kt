package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.BuildConfig
import com.face.net.Repository
import com.face.adapter.explore.ExploreListAdapter
import com.face.bean.AiFaceBean
import com.face.bean.AiFaceBean.Companion.ITEM_TYPE_FACE
import com.face.bean.RecommendBean
import com.face.bean.RecommendBean.RecommendX
import com.face.bean.ToolTypeBean
import com.face.key.Constants
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.GlobalApiResponse
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.singleToast
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/**
 * @author 再战科技
 * @date 2023/10/16
 * @description
 */
class ExploreViewModel1 : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()
    val finishLoadMore = MutableLiveData<Boolean>()

    //数据加载完
    private var dataAll = false
    private var homePage = 1

    val explores = NotNullMutableLiveData<List<AiFaceBean>>(listOf())
    var exploreListAdapter: ExploreListAdapter = ExploreListAdapter()

    //选中文字
    var selectedName1 = NotNullMutableLiveData("Latest")
    var selectedName2 =
        NotNullMutableLiveData(if (GVM.INSTANT.isShowTool.value != 0) "All" else "image")
    var selectedName3 =
        NotNullMutableLiveData(
            if (GVM.INSTANT.isShowTool.value != 0) {
                "All"
            } else {
                if (SPUtils.commonAuditTags.isNotEmpty()) {
                    SPUtils.commonAuditTags[(0 until SPUtils.commonAuditTags.size).random()]
                } else {
                    ""
                }
            }
        )

    //分类数据
    private var tpyeBean: AiFaceBean = AiFaceBean().apply {
        itemType = AiFaceBean.ITEM_TYPE_TYPE
    }

    var liketitle = ""
    var hottxt = ""
    var seeall = ""

    var toolList1: List<ToolTypeBean>? = MoshiHelper.convertJsonToList(SPUtils.exploreListTools1)
    var toolList2: List<ToolTypeBean>? = MoshiHelper.convertJsonToList(SPUtils.exploreListTools2)

    //同时请求 合并数据
    fun getData() {
        getDeviceRefresh()
        launch(Dispatchers.IO) {
            if (GVM.INSTANT.isShowTool.value != 0) {//渠道进入数据
//                val bannerData = async { Repository.getBanners() }
                val likeData = async { Repository.getUserLikeMedia() }
                val hotsData = async { Repository.getHots() }
                val recommendData = async { Repository.getRecommendMedia() }
                val aifaceData = async {
                    Repository.getMediaByTagHome(
                        selectedName1.value, selectedName2.value, selectedName3.value, 1
                    )
                }
                if (
//                    bannerData.await().mIsSuccess &&
                    hotsData.await().mIsSuccess &&
                    recommendData.await().mIsSuccess &&
                    aifaceData.await().mIsSuccess
                ) {
//                    SPUtils.cacheHomeTime = System.currentTimeMillis()
//                    SPUtils.bannerCacheData = bannerData.await().mData ?: mutableListOf()
//                    SPUtils.hotsCacheData = hotsData.await().mData ?: mutableListOf()
//                    SPUtils.recommendCacheData = recommendData.await().mData ?: mutableListOf()
//                    SPUtils.likeCacheData = likeData.await().mData ?: mutableListOf()
//                    SPUtils.aifaceCacheData = aifaceData.await().mData ?: mutableListOf()
                    mergeData(
                        mutableListOf(),
                        likeData.await().mData,
                        hotsData.await().mData,
                        recommendData.await().mData,
                        aifaceData.await().mData
                    )
                    homeRefreshing.postValue(false)
                    loadFailed.postIfNot(false)
                } else {
                    homeRefreshing.postValue(false)
                    loadFailed.postIfNot(true)
                }
            } else {
                val bannerData = async { Repository.getBanners() }
                val recommendData1 = async {
                    if (SPUtils.commonAuditTags.isNotEmpty()) {
                        Repository.getMediaHomeATag(SPUtils.commonAuditTags[0])
                    } else {
                        GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
                    }
                }
                val recommendData2 = async {
                    if (SPUtils.commonAuditTags.size > 1)
                        Repository.getMediaHomeATag(SPUtils.commonAuditTags[1])
                    else
                        GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
                }
                val recommendData3 = async {
                    if (SPUtils.commonAuditTags.size > 2)
                        Repository.getMediaHomeATag(SPUtils.commonAuditTags[2])
                    else
                        GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
                }
                val recommendData4 = async {
                    if (SPUtils.commonAuditTags.size > 3)
                        Repository.getMediaHomeATag(SPUtils.commonAuditTags[3])
                    else
                        GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
                }
                val aifaceData = async {
                    Repository.getMediaByTagHome(
                        selectedName1.value, selectedName2.value, selectedName3.value, 1
                    )
                }
                if (bannerData.await().mIsSuccess &&
                    recommendData1.await().mIsSuccess &&
                    recommendData2.await().mIsSuccess &&
                    recommendData3.await().mIsSuccess &&
                    recommendData4.await().mIsSuccess &&
                    aifaceData.await().mIsSuccess
                ) {
                    val recommendData: List<RecommendBean> = mutableListOf<RecommendBean>().apply {
                        val recommend1 = RecommendBean(
                            RecommendX(
                                "Type",
                                name = SPUtils.commonAuditTags[0]
                            ), recommendData1.await().mData
                        )
                        add(recommend1)
                        if (recommendData2.await().mData != null) {
                            val recommend2 = RecommendBean(
                                RecommendX(
                                    "Type",
                                    name = SPUtils.commonAuditTags[1]
                                ), recommendData2.await().mData
                            )
                            add(recommend2)
                        }
                        if (recommendData3.await().mData != null) {
                            val recommend3 = RecommendBean(
                                RecommendX(
                                    "Type",
                                    name = SPUtils.commonAuditTags[2]
                                ), recommendData3.await().mData
                            )
                            add(recommend3)
                        }
                        if (recommendData4.await().mData != null) {
                            val recommend4 = RecommendBean(
                                RecommendX(
                                    "Type",
                                    name = SPUtils.commonAuditTags[3]
                                ), recommendData4.await().mData
                            )
                            add(recommend4)
                        }
                    }

                    mergeData(
                        bannerData.await().mData,
                        mutableListOf(),
                        mutableListOf(),
                        recommendData,
                        aifaceData.await().mData
                    )
                    homeRefreshing.postValue(false)
                    loadFailed.postIfNot(false)
                } else {
                    homeRefreshing.postValue(false)
                    loadFailed.postIfNot(true)
                }
            }
        }
    }

    fun mergeData(
        bannerData: List<AiFaceBean>?,
        likeData: List<AiFaceBean>?,
        hotData: List<AiFaceBean>?,
        recommendData: List<RecommendBean>?,
        aifaceData: List<AiFaceBean>?
    ) {
        val mergeList = mutableListOf<AiFaceBean>()
        if (!bannerData.isNullOrEmpty()) {
            val bannerBean = AiFaceBean().apply {
                itemType = AiFaceBean.ITEM_TYPE_BANNER
                this.subList = bannerData.toMutableList()
            }
            mergeList.add(bannerBean)
        }
//        if (SPUtils.isPlayAI) {
//            val hotBean = AiFaceBean().apply {
//                itemType = AiFaceBean.ITEM_TYPE_PLAY
//            }
//            mergeList.add(hotBean)
//        }


        if (!hotData.isNullOrEmpty()) {
            val hotBean = AiFaceBean(
                title = hottxt,
            ).apply {
                itemType = AiFaceBean.ITEM_TYPE_HOT
                val newList = hotData.toMutableList()
                if (!SPUtils.isShare) newList.removeIf { SPUtils.materialId.contains(it.id) }
                this.subList = newList

            }
            mergeList.add(hotBean)
        }

        if (bannerData?.isEmpty() == true) {
            val bannerBean = AiFaceBean().apply {
                itemType = AiFaceBean.ITEM_TYPE_B
                this.subList = mutableListOf()
                toolTypeList = toolList1?.filter {
                    (!it.isDebug ||
                            it.showVersion.contains(BuildConfig.VERSION_CODE.toString())) &&
                            !it.putIn.contains(BuildConfig.VERSION_CODE.toString())
                }

                toolTypeList2 = toolList2?.filter {
                    (!it.isDebug ||
                            it.showVersion.contains(BuildConfig.VERSION_CODE.toString()))&&
                            !it.putIn.contains(BuildConfig.VERSION_CODE.toString())
                }
            }
            mergeList.add(bannerBean)
        }

        recommendData?.forEach { bean ->
            if (bean.listAiFace != null && (bean.listAiFace?.size ?: 0) > 0) {
                val recommendBean =
                    AiFaceBean(
                        title = bean.recommend.name,
                        more = seeall
                    ).apply {
                        itemType = AiFaceBean.ITEM_TYPE_LIKE
                        recommendBean = bean.recommend
                        subList = bean.listAiFace?.toMutableList()
                    }
                mergeList.add(recommendBean)
            }
        }

        if (!likeData.isNullOrEmpty()) {
            val likeBean =
                AiFaceBean(title = liketitle, more = seeall).apply {
                    itemType = AiFaceBean.ITEM_TYPE_LIKE
                    recommendBean = RecommendX("Like")
                    this.subList = likeData.toMutableList()
                }

            if (mergeList.size > 5) {
                mergeList.add(5, likeBean)
            } else {
                mergeList.add(likeBean)
            }
        }

        tpyeBean = AiFaceBean().apply {
            itemType = AiFaceBean.ITEM_TYPE_TYPE
        }
        mergeList.add(tpyeBean)

        val aiListBean = aifaceData?.toMutableList()
        if (aiListBean != null) {
            mergeList.addAll(aiListBean)
        }
        explores.postValue(mergeList)
    }

    /**
     * 数据
     * */
    @JvmOverloads
    fun getAiFace(page: Int = homePage + 1, refreshing: Boolean = false) {

        if (refreshing) {//重新加载数据
            dataAll = false
            homePage = 1
        }
        launchRequestOnIO({
            Repository.getMediaByTagHome(
                selectedName1.value, selectedName2.value, selectedName3.value, page
            )
        }) {
            onStart = {
                if (refreshing) {
                    showLoading()
                }
            }

            onSuccess = { it ->
                explores.value = explores.value.toMutableList().apply {
                    //重新加载时重置数据
                    if (page == 1) {
                        removeAll { removeIt -> removeIt.itemType == ITEM_TYPE_FACE }
                    }
                    if (it != null && !dataAll) {
                        addAll(it)
                    }
                }

                if (!it.isNullOrEmpty() && !dataAll) {
                    dataAll = it.size < Constants.pageSize//当前页不满分页条数表示加载完
                } else {
                    toast("no more data")
                }

                homePage = page
            }

            onFailed = { _, _, m ->
                singleToast(m)
            }

            onComplete = {
                dismissLoading()
                finishLoadMore.value = true
            }
        }
    }

    fun getDeviceRefresh() {//刷新工具界面
        if (GVM.INSTANT.isShowTool.value == 0) {
            launchRequestOnIO({ Repository.getDeviceCampaign() }) {
                onSuccess = { bean ->
                    if (GVM.INSTANT.isShowTool.value != bean) {//切换账号后不一致就重启app
                        GVM.INSTANT.isEventA.postValue(-1)
                    }
                    GVM.INSTANT.isShowTool.postValue(bean ?: 0)
                    SPbaseUtils.loadAB = bean ?: 0
                }
            }
        }
    }
}