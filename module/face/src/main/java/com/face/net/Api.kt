package com.face.net

import com.face.bean.ALinkBean
import com.face.bean.AiFaceBean
import com.face.bean.BuySlotBean
import com.face.bean.CaskBean
import com.face.bean.CheckInBean
import com.face.bean.ToolTaskBean
import com.face.bean.CollectAMBean
import com.face.bean.CompleteBean
import com.face.bean.ConfigBean
import com.face.bean.Explore2Bean
import com.face.bean.FeedbackBean
import com.face.bean.FeedbackMsgWrapBean
import com.face.bean.GetUrlBean
import com.face.bean.GiftBean
import com.face.bean.MediaByBean
import com.face.bean.MovesBean
import com.face.bean.MyFaceImgBean
import com.face.bean.OrderBean
import com.face.bean.PlayTaskBean
import com.face.bean.PointInfoBean
import com.face.bean.RatingBean
import com.face.bean.RecommendBean
import com.face.bean.ShareBean
import com.face.bean.ShareZoneBean
import com.face.bean.SignCardBean
import com.face.bean.TagConfigBean
import com.face.bean.TaskBean
import com.face.bean.TaskVoiceBean
import com.face.bean.TopSearchBean
import com.face.bean.UploadFileBean
import com.face.bean.UserBean
import com.face.bean.VipSchemeBean
import com.face.key.Constants
import com.key.DiffKey
import com.key.DiffKey.BASE_ACT_AI
import com.key.DiffKey.BASE_ACT_API
import com.key.DiffKey.BASE_ACT_LOGIN
import com.key.DiffKey.BASE_ACT_PAY
import com.key.DiffKey.BASE_ACT_USER
import com.key.DiffKey.BASE_ACT_STATISTICS
import com.zzkj.structure.net.ApiResponse
import com.zzkj.structure.net.GlobalApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * @author 再战科技
 * @date 2022/2/18
 * @description
 */
interface Api {
    companion object {
        const val BASE_URL = DiffKey.API_URL
    }

    /**
     * 获取搜索榜单
     */
    @POST("${BASE_ACT_AI}getTopSearchData")
    suspend fun getTopSearchData(): GlobalApiResponse<TopSearchBean>


    /**
     * 搜索
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}searchAi")
    suspend fun searchAi(
        @Field("type") type: String,
        @Field("key") keywords: String,
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = Constants.pageSize,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询Banner
     */
    @POST("${BASE_ACT_AI}getBanners")
    suspend fun getBanners(
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 猜你喜欢
     */
    @POST("${BASE_ACT_AI}getUserLikeMedia")
    suspend fun getUserLikeMedia(
    ): GlobalApiResponse<List<AiFaceBean>>


    /**
     * 猜你喜欢详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getUserLikeMedia")
    suspend fun getUserLikeMedias(
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询热门
     */
    @POST("${BASE_ACT_AI}getHots")
    suspend fun getHots(
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 推荐模块
     */
    @POST("${BASE_ACT_AI}getRecommendMedia")
    suspend fun getRecommendMedia(
    ): GlobalApiResponse<List<RecommendBean>>

    /**
     * 进入专题
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getTopic")
    suspend fun getTopic(
        @Field("id") id: Int,
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询Banner详情用
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getBanners")
    suspend fun getBanners(
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询热门 详情用
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getHots")
    suspend fun getHots(
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 条件查询
     * @param isHot String
     * @param type String
     *  @param tag String
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getMediaByTag")
    suspend fun getMediaByTag(
        @Field("isHot") email: String,
        @Field("type") content: String,
        @Field("tag") params: String?,
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = Constants.pageSize,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 每日更新
     * @param date String
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getDateNewMedia")
    suspend fun getDateNewMedia(
        @Field("date") date: String?,
    ): GlobalApiResponse<List<Explore2Bean>>

    /**
     * 查询模型标签
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getTags")
    suspend fun getTags(@Field("type") type: Int): GlobalApiResponse<List<String>>

    /**
     * 素材详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getMediaByID")
    suspend fun getMediaByID(
        @Field("media_id") id: String
    ): GlobalApiResponse<MediaByBean>

    /**
     * 素材详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getMediaByGroup")
    suspend fun getMediaByGroup(
        @Field("group_id") groupId: String
    ): GlobalApiResponse<List<MediaByBean>>


    /**
     * 自定义素材
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getUserAiMedia")
    suspend fun getUserAiMedia(
        @Field("type") type: String,
        @Field("param") param: String = "",
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 自定义素材
     */
    @POST("${BASE_ACT_USER}createShareUrl")
    suspend fun createshareUrl(): GlobalApiResponse<ShareBean>

    /**
     * 剩余礼物数
     */
    @POST("${BASE_ACT_USER}giftOverNum")
    suspend fun giftOverNum(): GlobalApiResponse<Int>

    /**
     * 兑换礼物列表记录
     */
    @POST("${BASE_ACT_USER}userGifts")
    suspend fun userGifts(
    ): GlobalApiResponse<List<CaskBean>>

    /**
     * 算力点兑换礼物
     */
    @POST("${BASE_ACT_USER}saveGiftUser")
    suspend fun saveGiftUser(): GlobalApiResponse<CaskBean>

    /**
     * 兑换vip
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}exchangeUserVip")
    suspend fun exchangeUserVip(
        @Field("product_id") type: String
    ): GlobalApiResponse<*>

    /**
     * 评分任务
     */
    @POST("${BASE_ACT_USER}getRatingRewardInfo")
    suspend fun getRatingRewardInfo(): GlobalApiResponse<RatingBean>

    /**
     * 领取评分任务
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}ratingReward")
    suspend fun ratingReward(
        @Field("config_id") configId: Int
    ): GlobalApiResponse<Int>


    /**
     * 增加用户算力
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}addUserTFLOPS")
    suspend fun addUserTFLOPS(
        @Field("content") type: String
    ): GlobalApiResponse<*>

    /**
     * 领取奖品
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}signReward")
    suspend fun signReward(
        @Field("reward_id") id: Int,
    ): GlobalApiResponse<*>

    /**
     * 获取奖品列表
     */
    @POST("${BASE_ACT_USER}getSignRewardConfig")
    suspend fun getSignRewardConfig(): GlobalApiResponse<List<PointInfoBean>>

    // 保存用户配置
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveUserSetting")
    suspend fun saveUserSetting(@Field("body") json: String): GlobalApiResponse<Any>

    // 获取用户配置
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserSetting")
    suspend fun getUserSetting(@Field("type") type: String): GlobalApiResponse<List<ConfigBean>>


    /**
     * 图片转适配配置
     */
    @POST("${BASE_ACT_AI}img2VideoConfig")
    suspend fun img2VideoConfig(): GlobalApiResponse<List<MovesBean>>

    /**
     * 用户浏览记录
     */
    @POST("${BASE_ACT_AI}getUserViewHistory")
    suspend fun getUserViewHistory(): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 删除自定义素材
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}deleteUserAiMedia")
    suspend fun deleteUserAiMedia(
        @Field("media_id") type: String
    ): GlobalApiResponse<*>

    /**
     * 用户媒体数量
     */
    @POST("${BASE_ACT_USER}userMediaNum")
    suspend fun userMediaNum(): GlobalApiResponse<BuySlotBean>

    /**
     * 增加用户媒体数量
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}payUserMediaNum")
    suspend fun payUserMediaNum(
        @Field("num") num : Int? = 0,
        @Field("type") tag: String? = "",
    ): GlobalApiResponse<String>


    /**
     * 滑动浏览
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getShortMedia")
    suspend fun getShortMedia(
        @Field("media_id") mediaId: String? = "",
        @Field("tag") tag: String? = "",
        @Field("page") id: Int,
        @Field("limit") limit: Int = 20
    ): GlobalApiResponse<List<MediaByBean>>

    /**
     * 观看完整
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}overMedia")
    suspend fun overMedia(
        @Field("media_id") mediaId: String? = "",
        @Field("state") state: Int//0滑动, 2观看完
    ): GlobalApiResponse<*>


    /**
     * 获取用户人脸
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserPics")
    suspend fun getUserPics(
        @Field("type") type: String?,
    ): GlobalApiResponse<List<MyFaceImgBean>>


    /**
     *
     * 素材标记
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}mediaBlack")
    suspend fun mediaBlack(
        @Field("media_id") mediaId: String?,
        @Field("content") content: String?,
        @Field("black") black: Int = 0,
    ): GlobalApiResponse<*>

    /**
     * 发起AI算法
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}sendAiTask")
    suspend fun sendAiTask(
        @Field("type") type: String?,
        @Field("media_id") media_id: String?,
        @Field("sources") sources: String?,
    ): GlobalApiResponse<TaskBean>

    /**
     * 发起PlayAI算法
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}sendGroupAiTask")
    suspend fun sendGroupAiTask(
        @Field("type") type: String?,
        @Field("group_id") groupId: String?,
        @Field("sources") sources: String?,
    ): GlobalApiResponse<TaskBean>


    /**
     * 保存用户素材
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}saveUserAiMedia")
    suspend fun saveUserAiMedia(
        @Field("type") type: String?,
        @Field("url") url: String?,
        @Field("tags") tags: String?,
        @Field("h_w") hw: String?,
        @Field("param") param: String?,
        @Field("img_url") img_url: String?,
    ): GlobalApiResponse<*>


    /**
     * 查询生成结果
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}queryAiTask")
    suspend fun queryAiTask(
        @Field("task_id") taskId: String?,
    ): GlobalApiResponse<TaskBean>


    /**
     * 退费
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}refundAiGroupTask")
    suspend fun refundAiGroupTask(
        @Field("task_group") taskGroup: String?,
    ): GlobalApiResponse<*>



    /**
     * 查询生成结果Group
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}queryAiGroupTask")
    suspend fun queryAiGroupTask(
        @Field("task_group") taskGroup: String?,
    ): GlobalApiResponse<List<PlayTaskBean>>

    /**
     * 重置任务
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}resetAiTask")
    suspend fun resetAiTask(
        @Field("task_id") taskId: String?,
    ): GlobalApiResponse<*>


    /**
     * 取消任务
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}cancelTask")
    suspend fun cancelTask(
        @Field("task_id") taskId: String?,
    ): GlobalApiResponse<*>

    /**
     * 生成历史
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getUserMedia")
    suspend fun getUserMedia(
        @Field("media_type") mediaType: String?,
    ): GlobalApiResponse<List<TaskBean>>


    /**
     * 查询任务中
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getUserAiTask")
    suspend fun getUserAiTask(
        @Field("type") type: String?,
    ): GlobalApiResponse<List<TaskVoiceBean>>

    /**
     * 删除用户人脸
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}deleteUserPic")
    suspend fun deleteUserPic(
        @Field("ids") ids: String?,
    ): GlobalApiResponse<*>

    /**
     * 保存
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveUserGenerateRecords")
    suspend fun saveUserGenerateRecords(
        @Field("id") id: String?,
        @Field("type") type: String?,
        @Field("name") name: String?,
        @Field("url") url: String?,
    ): GlobalApiResponse<ToolTaskBean>

    /**
     * 查询添加结果
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserRecordById")
    suspend fun getUserGenerate(
        @Field("id") id: String?,
    ): GlobalApiResponse<ToolTaskBean>


    /**
     * 查询记录
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserRecordPage")
    suspend fun getUserRecordPage(
        @Field("type") type: String?,
    ): GlobalApiResponse<List<ToolTaskBean>>


    /**
     * 清空用户浏览记录
     */
    @POST("${BASE_ACT_AI}removeUserView")
    suspend fun removeUserView(): GlobalApiResponse<*>

    /**
     * 删除生成历史
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}deleteUserMedia")
    suspend fun deleteUserMedia(
        @Field("task_id") task_id: String?,
        @Field("type") type: String?,
    ): GlobalApiResponse<*>

    /**
     * 删除消除历史
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}deleteUserRecord")
    suspend fun deleteUserRecord(
        @Field("id") id: String?,
    ): GlobalApiResponse<*>


    /**
     * 用户人脸排序
     */
    @POST("${BASE_ACT_USER}updateUserPicSort")
    suspend fun updateUserPicSort(
        @Body projectName: RequestBody,
    ): GlobalApiResponse<*>


    /**
     * 设置AI头像类型
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}setHeaderType")
    suspend fun setHeaderType(
        @Field("pic") pic: String,
        @Field("type") type: String,
    ): GlobalApiResponse<*>


    /**
     * 获取收藏
     */
    @POST("${BASE_ACT_USER}getUserCollect")
    suspend fun getUserCollect(): GlobalApiResponse<List<CollectAMBean>>


    /**
     * 保存收藏
     *@param type media|task 素材|成果
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveCollect")
    suspend fun saveCollect(
        @Field("media_id") id: String,
        @Field("type") type: String,
        @Field("num") num: Int = 0,
    ): GlobalApiResponse<String>

    /**
     * 删除收藏
     *@param type media|task 素材|成果
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}removeUserCollect")
    suspend fun removeUserCollect(
        @Field("id") id: String
    ): GlobalApiResponse<String>

    /**
     * 链接url 是否收藏
     *@param type media|task 素材|成果
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserCollectUrl")
    suspend fun getUserCollectUrl(
        @Field("url") url: String?
    ): GlobalApiResponse<Int>


    /**
     * 获取素材是否收藏
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserCollectMedia")
    suspend fun getUserCollectMedia(
        @Field("media_id") mediaId: String?
    ): GlobalApiResponse<Int>


    /**
     * 查询用户补签卡
     * @return GlobalApiResponse<Any>
     */
    @POST("${BASE_ACT_USER}getUserSignRewards")
    suspend fun getUserSignRewards(): GlobalApiResponse<SignCardBean>

    /**
     *  签到/补签
     * type 0正常，1补签
     * from_type  1 普通，2 VIP
     * day 签到日期
     * @return GlobalApiResponse<Any>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}signLog")
    suspend fun signLog(
        @Field("type") type: Int,
        @Field("from_type") fromType: Int,
        @Field("day") day: Int
    ): GlobalApiResponse<*>


    /**
     * 获取用户签到记录
     * @return GlobalApiResponse<Any>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserSignInLog")
    suspend fun getUserSignInLog(
        @Field("month") month: Int
    ): GlobalApiResponse<List<CheckInBean>>

    /**
     * 反馈
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}saveFeedback")
    suspend fun saveFeedback(
        @Field("email") email: String, @Field("content") content: String,
        // 快速反馈用
        @Field("params") params: String?
    ): GlobalApiResponse<Int>

    /**
     * 回复反馈消息
     * @param feedbackId Int
     * @param content String
     * @return GlobalApiResponse<Int>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}saveFeedbackRe")
    suspend fun sendFeedbackMsg(
        @Field("feed_id") feedbackId: Int, @Field("content") content: String
    ): GlobalApiResponse<*>

    /**
     * 获取反馈列表
     * @return GlobalApiResponse<List<>>
     */
    @POST("${BASE_ACT_API}getFeedback")
    suspend fun getFeedback(): GlobalApiResponse<List<FeedbackBean>>


    /**
     * 获取设备渠道
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}getDeviceCampaign")
    suspend fun getDeviceCampaign(
        @Field("android_id") androidId: String,
        @Field("info") info: String,
        @Field("user_id") userId: Int
    ): GlobalApiResponse<Int>


    /**
     * 获取反馈的消息
     * @return GlobalApiResponse<List<>>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}getFeedbackRes")
    suspend fun getFeedbackMsg(
        @Field("feed_id") feedbackId: Int
    ): GlobalApiResponse<List<FeedbackMsgWrapBean>>

    /**
     * 分享评分
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}ratingShare")
    suspend fun ratingShare(
        @Field("task_id") task_id: String,
        @Field("media_id") media_id: String,
        @Field("score") number: Float
    ): GlobalApiResponse<*>

    /**
     * 查询配置信息
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}getSysConfig")
    suspend fun getSysConfig(
        @Field("type") type: String
    ): GlobalApiResponse<List<TagConfigBean>>

    /**
     * 查询礼物信息
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}getSysConfig")
    suspend fun getSysConfig2(
        @Field("type") type: String
    ): GlobalApiResponse<List<GiftBean>>

    /**
     * Vip方案列表
     * @return ApiResponse<List<VipSchemeBean>>
     */
    @POST("${BASE_ACT_API}getVipConfigs")
    suspend fun getVipScheme(
    ): GlobalApiResponse<List<VipSchemeBean>>

    /**
     * 获取算力服务列表
     */
    @POST("${BASE_ACT_API}getNewTFLOPConfigs")
    suspend fun getTFLOPConfigs(): GlobalApiResponse<List<VipSchemeBean>>

    /**
     * VIDEO素材保存图片
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}saveVideoImg")
    suspend fun saveVideoImg(
        @Field("media_id") media_id: String? = "",
        @Field("img_url") img_url: String = ""
    ): GlobalApiResponse<*>

    /**
     * 使用用户剩余算力
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}subUserTFLOPS")
    suspend fun subUserTFLOPS(
        @Field("num") id: Int = 1,
        @Field("content") content: String = ""
    ): GlobalApiResponse<*>

    /**
     *
     * 第三方登陆
     * @param type  String
     * @param third_id  String
     * @param authToken  String
     * @return GlobalApiResponse<UserBean>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}thirdLogin")
    suspend fun googleLogin(
        @Field("type") type: String = "google",
        @Field("third_id") id: String,
        @Field("authToken") token: String
    ): GlobalApiResponse<UserBean>

    /**
     *
     * 账号登录
     * @param username   String
     * @param password   String
     * @return GlobalApiResponse<UserBean>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}login")
    suspend fun accountLogin(
        @Field("username") username: String,
        @Field("password") password: String,
    ): GlobalApiResponse<UserBean>

    /**
     *
     * 注册登录
     * @param username   String
     * @param password   String
     * @return GlobalApiResponse<UserBean>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}register")
    suspend fun accountRegister(
        @Field("username") username: String,
        @Field("password") password: String,
    ): GlobalApiResponse<UserBean>

    /**
     *
     * 修改密码
     * @param username   String
     * @param password   String
     * @return GlobalApiResponse<UserBean>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}updatePassword")
    suspend fun updatePassword(
        @Field("password") username: String,
        @Field("new_password") password: String,
    ): GlobalApiResponse<UserBean>

    /**
     * 删除账号
     */
    @POST("${BASE_ACT_LOGIN}deleteUserInfo")
    suspend fun deleteUserInfo(): GlobalApiResponse<*>

    /**
     *
     * 第三方登录绑定游客接口
     * @param third_id  String
     * @param authToken  String
     * @return GlobalApiResponse<UserBean>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}bindGuestUser")
    suspend fun bindGuestUser(
        @Field("third_id") id: String,
        @Field("authToken") token: String
    ): GlobalApiResponse<UserBean>

    /**
     * 刷新用户
     * @return GlobalApiResponse<Any>
     */
    @POST("${BASE_ACT_LOGIN}getUserInfo")
    suspend fun getUserInfo(): GlobalApiResponse<UserBean>

    /**
     * 保存推送token
     * @param push_token   String
     * @return GlobalApiResponse<Any>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}savePushToken")
    suspend fun savePushToken(
        @Field("push_token") pushToken: String
    ): GlobalApiResponse<Any>


    /**
     * 创建Google订单
     * @param schemeId String
     * @return GlobalApiResponse<OrderBean>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_PAY}createNoPaymentOrder")
    suspend fun createNoPaymentOrder(
        @Field("query_id") schemeId: String
    ): GlobalApiResponse<OrderBean>


    /**
     * 保存事件
     * @param eventName String
     * @param parameter String
     * @return ApiResponse<Any>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_STATISTICS}saveEvent")
    suspend fun saveEvent(
        @Field("event") eventName: String,
        @Field("info") parameter: String
    ): GlobalApiResponse<Any>

    /**
     * 获取上传链接
     * @param fileName String
     * @return ApiResponse<Any>
     */
    @FormUrlEncoded
    @POST("/getPresignedUrl")
    suspend fun getPresignedUrl(
        @Field("fileName") fileName: String,
    ): GlobalApiResponse<GetUrlBean>

    /**
     * 上传
     * @return ApiResponse<Any>
     */
    @Multipart
    @POST("/uploadFile2S3")
    suspend fun uploadFile(@Part file: MultipartBody.Part): GlobalApiResponse<UploadFileBean>


    /**
     * 合并切片
     */
    @POST("/completeUpload")
    suspend fun completeUpload(
        @Body jsonName: RequestBody,
    ): GlobalApiResponse<CompleteBean>


    /**
     * 断点分片
     * @return ApiResponse<Any>
     */
    @FormUrlEncoded
    @POST("/getPresignedUrlPart")
    suspend fun getPresignedUrlPart(
        @Field("fileName") fileName: String,
        @Field("num") num: Int
    ): GlobalApiResponse<ALinkBean>


    /**
     * 校验Google订单
     * @param orderNo String
     * @param orderToken String
     * @return GlobalApiResponse<String>
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_PAY}googlePayCallback")
    suspend fun checkOrder(
        @Field("orderNo") orderNo: String, @Field("orderToken") orderToken: String
    ): GlobalApiResponse<String>

    @Streaming
    @GET
    fun downloadWithUrl(@Url fileUrl: String): Call<ResponseBody>

    /**
     * 上传
     * @return ApiResponse<Any>
     */
    @PUT
    suspend fun uploadFile2(
        @Url url: String,
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    /**
     * share 热门分享列表
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}shareHotMedia")
    suspend fun shareHotMedia(
        @Field("page") page: Int = 1,
        @Field("video_type") video_type: String = "",
        @Field("media_type") media_type: String = "",
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * share 列表
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}shareMedia")
    suspend fun getShareZoneList(
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = 20,
        @Field("video_type") video_type: String = "",
        @Field("media_type") media_type: String = "",
        @Field("is_owner") is_owner: Int = 0
    ): GlobalApiResponse<List<AiFaceBean>>

    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveMediaShare")
    suspend fun saveMediaShare(
        @Field("media_id") mediaId: String,
    ): GlobalApiResponse<AiFaceBean>

    @FormUrlEncoded
    @POST("${BASE_ACT_USER}deleteMediaShare")
    suspend fun deleteMediaShare(
        @Field("media_id") mediaId: String,
    ): GlobalApiResponse<AiFaceBean>

    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserItemLog")
    suspend fun getUserItemLog(
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<MutableList<ShareZoneBean>>
}