package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.R
import com.blankj.utilcode.util.LogUtils
import com.a.adapter.AExploreListAdapter
import com.face.bean.AiFaceBean
import com.face.bean.AiFaceBean.Companion.ITEM_TYPE_FACE
import com.face.bean.RecommendBean
import com.face.bean.RecommendBean.RecommendX
import com.face.bean.VipBannerBean
import com.face.key.Constants
import com.a.net.ARepository
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.GlobalApiResponse
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.singleToast
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class AExploreViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()
    val finishLoadMore = MutableLiveData<Boolean>()

    //数据加载完
    private var dataAll = false
    private var homePage = 1

    val explores = NotNullMutableLiveData<List<AiFaceBean>>(listOf())
    var exploreListAdapter: AExploreListAdapter = AExploreListAdapter()

    //选中文字
    var selectedName1 = "Latest"
    var selectedName2 = "image"
    var selectedName3 = if (SPUtils.commonAuditTags.isNotEmpty()) {
        SPUtils.commonAuditTags[(0 until SPUtils.commonAuditTags.size).random()]
    } else {
        ""
    }

    //分类数据
    private var tpyeBean: AiFaceBean = AiFaceBean().apply {
        itemType = AiFaceBean.ITEM_TYPE_TYPE
    }

    fun retryData() {
        homeRefreshing.value = true
    }

    val bannerList = listOf(
        VipBannerBean(
            0,
            R.mipmap.a_img_cartoon,
            "Image to cartoon",
            "Generate cartoon style from photo"
        ), VipBannerBean(
            1,
            R.mipmap.a_img_imageremoval,
            "AI image removal",
            "Easily remove clutter with just one tep"
        ), VipBannerBean(
            2,
            R.mipmap.a_img_age,
            "Change age",
            "Change age, age magic"
        ), VipBannerBean(
            3,
            R.mipmap.a_img_idphoto,
            "ID photo generation",
            "Generate common ID photo"
        ), VipBannerBean(
            4,
            R.mipmap.a_img_backfround,
            "Background Removal",
            "Smart select the image you want to cut ..."
        )
    )

    //同时请求 合并数据
    fun getData() {
        getDeviceRefresh()
        launch(Dispatchers.IO) {
            val bannerData = async { ARepository.getBanners() }
            val recommendData1 = async {
                if (SPUtils.commonAuditTags.isNotEmpty()) {
                    ARepository.getMediaHomeATag(SPUtils.commonAuditTags[0])
                } else {
                    GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
                }
            }
            val recommendData2 = async {
                if (SPUtils.commonAuditTags.size > 1)
                    ARepository.getMediaHomeATag(SPUtils.commonAuditTags[1])
                else
                    GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
            }
            val recommendData3 = async {
                if (SPUtils.commonAuditTags.size > 2)
                    ARepository.getMediaHomeATag(SPUtils.commonAuditTags[2])
                else
                    GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
            }
            val recommendData4 = async {
                if (SPUtils.commonAuditTags.size > 3)
                    ARepository.getMediaHomeATag(SPUtils.commonAuditTags[3])
                else
                    GlobalApiResponse<List<AiFaceBean>>().apply { mCode = 0 }
            }
//            val aifaceData = async {
//                ARepository.getMediaByTagHome(
//                    selectedName1, selectedName2, selectedName3, 1
//                )
//            }
            if (bannerData.await().mIsSuccess &&
                recommendData1.await().mIsSuccess &&
                recommendData2.await().mIsSuccess &&
                recommendData3.await().mIsSuccess &&
                recommendData4.await().mIsSuccess
//                && aifaceData.await().mIsSuccess
            ) {
                homeRefreshing.postValue(false)
                loadFailed.postIfNot(false)
                val recommendData: List<RecommendBean> = mutableListOf<RecommendBean>().apply {
                    if ((recommendData1.await().mData?.size ?: 0) > 0) {
                        val recommend1 = RecommendBean(
                            RecommendX(
                                "Type",
                                name = SPUtils.commonAuditTags[0]
                            ), recommendData1.await().mData
                        )
                        add(recommend1)
                    }
                    if ((recommendData2.await().mData?.size ?: 0) > 0) {
                        if (recommendData2.await().mData != null) {
                            val recommend2 = RecommendBean(
                                RecommendX(
                                    "Type",
                                    name = SPUtils.commonAuditTags[1]
                                ), recommendData2.await().mData
                            )
                            add(recommend2)
                        }
                    }

                    if ((recommendData3.await().mData?.size ?: 0) > 0) {
                        val recommend3 = RecommendBean(
                            RecommendX(
                                "Type",
                                name = SPUtils.commonAuditTags[2]
                            ), recommendData3.await().mData
                        )
                        add(recommend3)
                    }
                    if ((recommendData4.await().mData?.size ?: 0) > 0) {
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
                    recommendData,
                    null
                )
            } else {
                homeRefreshing.postValue(false)
                loadFailed.postIfNot(true)
            }
        }
    }

    private fun mergeData(
        bannerData: List<AiFaceBean>?,
        recommendData: List<RecommendBean>?,
        aifaceData: List<AiFaceBean>?
    ) {
        val mergeList = mutableListOf<AiFaceBean>()
        if (bannerData != null) {
            val bannerBean = AiFaceBean().apply {
                itemType = AiFaceBean.ITEM_TYPE_BANNER
                this.subList = bannerData.toMutableList()
            }
            mergeList.add(bannerBean)
        }

        val toolBean = AiFaceBean().apply {
            itemType = AiFaceBean.ITEM_TYPE_TOOL
        }
        mergeList.add(toolBean)

        recommendData?.forEach { bean ->
            if ((bean.listAiFace?.size ?: 0) > 0) {
                val recommendBean =
                    AiFaceBean(
                        title = bean.recommend.name,
                        more = "More"
                    ).apply {
                        itemType = AiFaceBean.ITEM_TYPE_LIKE
                        recommendBean = bean.recommend
                        subList = bean.listAiFace?.toMutableList()
                    }
                mergeList.add(recommendBean)
            }
        }

//        tpyeBean = AiFaceBean().apply {
//            itemType = AiFaceBean.ITEM_TYPE_TYPE
//        }
//        mergeList.add(tpyeBean)

//        val aiListBean = aifaceData?.toMutableList()
//        if (aiListBean != null) {
//            mergeList.addAll(aiListBean)
//        }
        exploreListAdapter = AExploreListAdapter()
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
            ARepository.getMediaByTagHome(
                selectedName1, selectedName2, selectedName3, page
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
        launchRequestOnIO({ ARepository.getDeviceCampaign() }) {
            onSuccess = { bean ->
                GVM.INSTANT.isShowTool.postValue(bean)
                GVM.INSTANT.aIsAB.postValue(bean)
            }
        }
    }
}
