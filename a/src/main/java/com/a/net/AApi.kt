package com.a.net

import com.face.bean.ALinkBean
import com.face.bean.AiFaceBean
import com.face.bean.BuySlotBean
import com.face.bean.CaskBean
import com.face.bean.CheckInBean
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
import com.face.bean.ToolTaskBean
import com.face.bean.TopSearchBean
import com.face.bean.UploadFileBean
import com.face.bean.UserBean
import com.face.bean.VipSchemeBean
import com.face.key.Constants
import com.zzkj.structure.net.GlobalApiResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * a 模块接口，文档：https://www.vantasyx.com/swagger-ui.html
 *
 * 使用 Swagger 中的直接路径；同一路径支持 GET/POST 时，优先提供 POST，站点首页使用 GET。
 * 同名接口的请求方式及参数编码与 Api.kt 保持一致；表单 POST 使用 @FormUrlEncoded 和 @Field。
 * 新增接口按 Swagger 声明使用 Query、Path 或 Body；JSON 请求体保留 @Body。
 * token、packageName、设备信息等公共请求头由 com.face.net.HttpInterceptor 统一添加；
 * 创建 Retrofit 服务时需配置该拦截器或提供等效请求头。
 *
 * Swagger 未定义 object 的内部结构。已有业务接口沿用 Api.kt 的响应实体；
 * 尚无实体的接口返回完整 JSON Map，文本及验证码返回 ResponseBody。
 */
interface AApi {
    companion object {
        const val BASE_URL = "https://www.vantasyx.com/"

        /** 登录与账户 */
        const val BASE_ACT_LOGIN = "/login/"

        /** AI 素材与生成 */
        const val BASE_ACT_AI = "/ai/"

        /** 用户、收藏与算力 */
        const val BASE_ACT_USER = "/user/"

        /** 配置与反馈 */
        const val BASE_ACT_API = "/api/"

        /** 支付 */
        const val BASE_ACT_PAY = "/pay/"

        /** 统计 */
        const val BASE_ACT_STATISTICS = "/statistics/"

        /** Grok */
        const val BASE_ACT_GROK = "/grok/"

        /** AI 任务节点 */
        const val BASE_ACT_AI_TASK = "/aitask/"

        /** 站点 */
        const val BASE_ACT_ROOT = "/"
    }

    // 与 Api.kt 同名的接口，按其声明顺序排列

    /**
     * 获取搜索榜单
     */
    @POST("${BASE_ACT_AI}getTopSearchData")
    suspend fun getTopSearchData(): GlobalApiResponse<TopSearchBean>

    /**
     * 搜索
     * @param type type: image|video
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}searchAi")
    suspend fun searchAi(
        @Field("type") type: String,
        @Field("key") key: String,
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = Constants.pageSize,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询Banner
     */
    @POST("${BASE_ACT_AI}getBanners")
    suspend fun getBanners(): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 猜你喜欢
     */
    @POST("${BASE_ACT_AI}getUserLikeMedia")
    suspend fun getUserLikeMedia(): GlobalApiResponse<List<AiFaceBean>>


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
    suspend fun getHots(): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 推荐模块
     */
    @POST("${BASE_ACT_AI}getRecommendMedia")
    suspend fun getRecommendMedia(): GlobalApiResponse<List<RecommendBean>>

    /**
     * 进入专题
     * @param id 主题id， 对应open_value
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getTopic")
    suspend fun getTopic(
        @Field("id") id: Int,
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 50,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询 Banner 详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getBanners")
    suspend fun getBanners(
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 查询热门详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getHots")
    suspend fun getHots(
        @Field("page") page: Int = 0,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 条件查询
     * @param isHot isHot: hot|new
     * @param type type: image|video, 如果套组素材，用group_video
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getMediaByTag")
    suspend fun getMediaByTag(
        @Field("isHot") isHot: String,
        @Field("type") type: String,
        @Field("tag") tag: String? = null,
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = Constants.pageSize,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 每日更新
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getDateNewMedia")
    suspend fun getDateNewMedia(
        @Field("date") date: String?,
    ): GlobalApiResponse<List<Explore2Bean>>

    /**
     * 查询模型标签
     * @param type type:1,2,3
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getTags")
    suspend fun getTags(
        @Field("type") type: Int,
    ): GlobalApiResponse<List<String>>

    /**
     * 素材详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getMediaByID")
    suspend fun getMediaByID(
        @Field("media_id") mediaId: String,
    ): GlobalApiResponse<MediaByBean>

    /**
     * 分组素材详情
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getMediaByGroup")
    suspend fun getMediaByGroup(
        @Field("group_id") groupId: String,
    ): GlobalApiResponse<List<MediaByBean>>

    /**
     * 查询用户素材
     * @param type image|video
     * @param param 自定义json {video_type:20}
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getUserAiMedia")
    suspend fun getUserAiMedia(
        @Field("type") type: String,
        @Field("param") param: String = "",
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 创建分享短链接
     */
    @POST("${BASE_ACT_USER}createShareUrl")
    suspend fun createshareUrl(): GlobalApiResponse<ShareBean>

    /**
     * 剩余礼物数
     */
    @POST("${BASE_ACT_USER}giftOverNum")
    suspend fun giftOverNum(): GlobalApiResponse<Int>

    /**
     * 兑换礼物列表
     */
    @POST("${BASE_ACT_USER}userGifts")
    suspend fun userGifts(): GlobalApiResponse<List<CaskBean>>

    /**
     * 算力点兑换礼物
     */
    @POST("${BASE_ACT_USER}saveGiftUser")
    suspend fun saveGiftUser(): GlobalApiResponse<CaskBean>

    /**
     * 兑换vip
     * @param productId vip商品ID
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}exchangeUserVip")
    suspend fun exchangeUserVip(
        @Field("product_id") productId: String,
    ): GlobalApiResponse<Any?>

    /**
     * 分享用户奖励方案和评分数量
     */
    @POST("${BASE_ACT_USER}getRatingRewardInfo")
    suspend fun getRatingRewardInfo(): GlobalApiResponse<RatingBean>

    /**
     * 领取评分奖励
     * @param configId 领取的奖品id
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}ratingReward")
    suspend fun ratingReward(
        @Field("config_id") configId: Int,
    ): GlobalApiResponse<Int>

    /**
     * 增加用户剩余算力
     * @param content content: json
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}addUserTFLOPS")
    suspend fun addUserTFLOPS(
        @Field("content") content: String,
    ): GlobalApiResponse<Any?>

    /**
     * 领取奖品
     * @param rewardId 奖品ID
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}signReward")
    suspend fun signReward(
        @Field("reward_id") rewardId: Int,
    ): GlobalApiResponse<Any?>

    /**
     * 获取奖品列表
     */
    @POST("${BASE_ACT_USER}getSignRewardConfig")
    suspend fun getSignRewardConfig(): GlobalApiResponse<List<PointInfoBean>>

    /**
     * 保存用户参数
     * @param body json: type, key, value
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveUserSetting")
    suspend fun saveUserSetting(
        @Field("body") body: String,
    ): GlobalApiResponse<Any>

    /**
     * 获取用户参数
     * @param type 根据保存获取
     * @param key 根据保存获取
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserSetting")
    suspend fun getUserSetting(
        @Field("type") type: String,
        @Field("key") key: String? = null,
    ): GlobalApiResponse<List<ConfigBean>>

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
     * 删除用户素材
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}deleteUserAiMedia")
    suspend fun deleteUserAiMedia(
        @Field("media_id") mediaId: String,
    ): GlobalApiResponse<Any?>

    /**
     * 用户媒体数量
     */
    @POST("${BASE_ACT_USER}userMediaNum")
    suspend fun userMediaNum(): GlobalApiResponse<BuySlotBean>

    /**
     * 增加用户媒体数量
     * @param num 数量
     * @param type image，20,180,300
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}payUserMediaNum")
    suspend fun payUserMediaNum(
        @Field("num") num: Int?,
        @Field("type") type: String?,
    ): GlobalApiResponse<String>

    /**
     * 滑动浏览
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getShortMedia")
    suspend fun getShortMedia(
        @Field("media_id") mediaId: String? = null,
        @Field("tag") tag: String? = null,
        @Field("page") page: Int,
        @Field("limit") limit: Int = 20,
    ): GlobalApiResponse<List<MediaByBean>>

    /**
     * 观看短视频
     * @param state 0滑动, 2观看完
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}overMedia")
    suspend fun overMedia(
        @Field("media_id") mediaId: String? = "",
        @Field("state") state: Int,
    ): GlobalApiResponse<Any?>

    /**
     * 获取用户人脸
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserPics")
    suspend fun getUserPics(
        @Field("type") type: String? = null,
    ): GlobalApiResponse<List<MyFaceImgBean>>

    /**
     * 素材标记
     * @param black black, 0 , 1
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}mediaBlack")
    suspend fun mediaBlack(
        @Field("media_id") mediaId: String?,
        @Field("content") content: String?,
        @Field("black") black: Int,
    ): GlobalApiResponse<Any?>

    /**
     * 发起AI算法
     * @param type aiTaskType
     * @param mediaId media_id, 图片和视频换脸需要穿
     * @param sources 用户上传的图片链接，逗号分开，如果是多人，需要注意位置。
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}sendAiTask")
    suspend fun sendAiTask(
        @Field("type") type: String?,
        @Field("media_id") mediaId: String? = null,
        @Field("sources") sources: String? = null,
    ): GlobalApiResponse<TaskBean>

    /**
     * 发起AIGroup算法
     * @param type aiTaskType: group_video_swap
     * @param groupId group_id, 套组ID
     * @param sources 用户上传的图片链接，逗号分开，如果是多人，需要注意位置。
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}sendGroupAiTask")
    suspend fun sendGroupAiTask(
        @Field("type") type: String?,
        @Field("group_id") groupId: String?,
        @Field("sources") sources: String? = null,
    ): GlobalApiResponse<TaskBean>

    /**
     * 保存用户素材
     * @param type image|video
     * @param url 原始地址
     * @param hW width,height
     * @param param 自定义json {video_type:20}
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}saveUserAiMedia")
    suspend fun saveUserAiMedia(
        @Field("type") type: String?,
        @Field("url") url: String?,
        @Field("tags") tags: String?,
        @Field("h_w") hW: String?,
        @Field("param") param: String? = null,
        @Field("img_url") imgUrl: String? = null,
    ): GlobalApiResponse<Any?>

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
     * @param taskGroup 任务组
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}refundAiGroupTask")
    suspend fun refundAiGroupTask(
        @Field("task_group") taskGroup: String?,
    ): GlobalApiResponse<Any?>

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
    ): GlobalApiResponse<Any?>

    /**
     * 取消生成任务
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}cancelTask")
    suspend fun cancelTask(
        @Field("task_id") taskId: String?,
    ): GlobalApiResponse<Any?>

    /**
     * 生成历史
     * @param mediaType media_type | 查分组group_video_swap
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}getUserMedia")
    suspend fun getUserMedia(
        @Field("media_type") mediaType: String? = null,
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
     * @param ids 照片ID, 逗号分隔
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}deleteUserPic")
    suspend fun deleteUserPic(
        @Field("ids") ids: String?,
    ): GlobalApiResponse<Any?>

    /**
     * 保存用户记录
     * Swagger 将 url 标为 integer；按现有 Api.kt 的图片 URL 契约保留 String。
     * @param type type任务类型
     * @param name 记录名称
     * @param url 记录链接
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveUserGenerateRecords")
    suspend fun saveUserGenerateRecords(
        @Field("id") id: String? = null,
        @Field("type") type: String? = null,
        @Field("name") name: String? = null,
        @Field("url") url: String? = null,
    ): GlobalApiResponse<ToolTaskBean>

    /**
     * 获取用户记录
     * @param id record_id
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserRecordById")
    suspend fun getUserGenerate(
        @Field("id") id: String?,
    ): GlobalApiResponse<ToolTaskBean>

    /**
     * 获取用户记录
     * @param type type任务类型
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserRecordPage")
    suspend fun getUserRecordPage(
        @Field("type") type: String? = null,
        @Field("page") page: Int? = null,
        @Field("limit") limit: Int? = null,
    ): GlobalApiResponse<List<ToolTaskBean>>

    /**
     * 清空用户浏览记录
     */
    @POST("${BASE_ACT_AI}removeUserView")
    suspend fun removeUserView(): GlobalApiResponse<Any?>

    /**
     * 删除历史
     * @param type task|group_video_swap
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}deleteUserMedia")
    suspend fun deleteUserMedia(
        @Field("task_id") taskId: String?,
        @Field("type") type: String? = null,
    ): GlobalApiResponse<Any?>

    /**
     * 删除用户记录
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}deleteUserRecord")
    suspend fun deleteUserRecord(
        @Field("id") id: String? = null,
    ): GlobalApiResponse<Any?>

    /**
     * 用户人脸排序
     * 请求体由调用方构造 RequestBody，JSON 内容应使用 application/json。
     */
    @POST("${BASE_ACT_USER}updateUserPicSort")
    suspend fun updateUserPicSort(
        @Body json: RequestBody,
    ): GlobalApiResponse<Any?>

    /**
     * 设置AI头像类型
     * @param type me|friend|relative|workmate|classmate|other
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}setHeaderType")
    suspend fun setHeaderType(
        @Field("pic") pic: String,
        @Field("type") type: String,
    ): GlobalApiResponse<Any?>

    /**
     * 获取收藏
     */
    @POST("${BASE_ACT_USER}getUserCollect")
    suspend fun getUserCollect(): GlobalApiResponse<List<CollectAMBean>>

    /**
     * 保存收藏
     * @param mediaId media_id, 如果是成果，就是返回得任务ID
     * @param type media|task  素材|成果
     * @param num type为task要收藏的成果的index
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveCollect")
    suspend fun saveCollect(
        @Field("media_id") mediaId: String,
        @Field("type") type: String,
        @Field("num") num: Int = 0,
    ): GlobalApiResponse<String>

    /**
     * 删除收藏
     * @param id collect_id
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}removeUserCollect")
    suspend fun removeUserCollect(
        @Field("id") id: String,
    ): GlobalApiResponse<String>

    /**
     * 获取链接是否收藏
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserCollectUrl")
    suspend fun getUserCollectUrl(
        @Field("url") url: String?,
    ): GlobalApiResponse<Int>

    /**
     * 获取素材是否收藏
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserCollectMedia")
    suspend fun getUserCollectMedia(
        @Field("media_id") mediaId: String?,
    ): GlobalApiResponse<Int>

    /**
     * 获取用户签到奖励
     */
    @POST("${BASE_ACT_USER}getUserSignRewards")
    suspend fun getUserSignRewards(): GlobalApiResponse<SignCardBean>

    /**
     * 签到/补签
     * @param type 0正常，1补签
     * @param fromType 1 普通，2 VIP
     * @param day 签到日期20230630
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}signLog")
    suspend fun signLog(
        @Field("type") type: Int,
        @Field("from_type") fromType: Int,
        @Field("day") day: Int,
    ): GlobalApiResponse<Any?>

    /**
     * 获取用户签到记录
     * @param month 202307
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserSignInLog")
    suspend fun getUserSignInLog(
        @Field("month") month: Int,
    ): GlobalApiResponse<List<CheckInBean>>

    /**
     * 保存用户反馈信息
     * @param params params: json
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}saveFeedback")
    suspend fun saveFeedback(
        @Field("email") email: String,
        @Field("content") content: String,
        @Field("params") params: String? = null,
    ): GlobalApiResponse<Int>

    /**
     * 保存用户反馈信息回复
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}saveFeedbackRe")
    suspend fun sendFeedbackMsg(
        @Field("feed_id") feedId: Int,
        @Field("content") content: String,
    ): GlobalApiResponse<Any?>

    /**
     * 用户反馈信息
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
     * 获取反馈信息回复
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}getFeedbackRes")
    suspend fun getFeedbackMsg(
        @Field("feed_id") feedId: Int,
    ): GlobalApiResponse<List<FeedbackMsgWrapBean>>

    /**
     * 分享评分
     * @param mediaId 素菜ID
     * @param score 分数3.5 - 5分
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}ratingShare")
    suspend fun ratingShare(
        @Field("task_id") taskId: String,
        @Field("media_id") mediaId: String,
        @Field("score") score: Float,
    ): GlobalApiResponse<Any?>

    /**
     * 查询配置信息
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_API}getSysConfig")
    suspend fun getSysConfig(
        @Field("type") type: String,
        @Field("key") key: String? = null,
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
     * 获取VIP服务列表
     */
    @POST("${BASE_ACT_API}getVipConfigs")
    suspend fun getVipScheme(): GlobalApiResponse<List<VipSchemeBean>>

    /**
     * 获取新算例服务列表
     */
    @POST("${BASE_ACT_API}getNewTFLOPConfigs")
    suspend fun getTFLOPConfigs(): GlobalApiResponse<List<VipSchemeBean>>

    /**
     * VIDEO素材保存图片
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}saveVideoImg")
    suspend fun saveVideoImg(
        @Field("media_id") mediaId: String? = "",
        @Field("img_url") imgUrl: String,
    ): GlobalApiResponse<Any?>

    /**
     * 使用用户剩余算力
     * @param content content 自定义，方便历史查询寻
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}subUserTFLOPS")
    suspend fun subUserTFLOPS(
        @Field("num") num: Int,
        @Field("content") content: String,
    ): GlobalApiResponse<Any?>

    /**
     * 第三方登陆
     * @param type 登录类型，app: 注册, fb：facebook，google：google, 游客：guest
     * @param thirdId 第三方登录返回的id
     * @param authToken 第三方登录返回的token
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}thirdLogin")
    suspend fun googleLogin(
        @Field("type") type: String,
        @Field("third_id") thirdId: String,
        @Field("authToken") authToken: String,
    ): GlobalApiResponse<UserBean>

    /**
     * 登陆
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}login")
    suspend fun accountLogin(
        @Field("username") username: String,
        @Field("password") password: String,
    ): GlobalApiResponse<UserBean>

    /**
     * 注册
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}register")
    suspend fun accountRegister(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("device_info") deviceInfo: String? = null,
    ): GlobalApiResponse<UserBean>

    /**
     * 修改密码；返回原始响应，由调用方读取并关闭。
     * @param password 密码
     * @param newPassword 新密码
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}updatePassword")
    suspend fun updatePassword(
        @Field("password") password: String,
        @Field("new_password") newPassword: String,
    ): GlobalApiResponse<UserBean>

    /**
     * 删除账户
     */
    @POST("${BASE_ACT_LOGIN}deleteUserInfo")
    suspend fun deleteUserInfo(): GlobalApiResponse<Any?>

    /**
     * 绑定游客登陆账簿
     * @param thirdId 第三方登录返回的id
     * @param authToken 第三方登录返回的token
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}bindGuestUser")
    suspend fun bindGuestUser(
        @Field("third_id") thirdId: String,
        @Field("authToken") authToken: String,
    ): GlobalApiResponse<UserBean>

    /**
     * 获取用户信息
     */
    @POST("${BASE_ACT_LOGIN}getUserInfo")
    suspend fun getUserInfo(): GlobalApiResponse<UserBean>

    /**
     * 保存google token
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_LOGIN}savePushToken")
    suspend fun savePushToken(
        @Field("push_token") pushToken: String,
    ): GlobalApiResponse<Any>

    /**
     * 通过SDK提交时创建订单
     * @param queryId vip
     * @param num 购买数量，默认传1
     * @param devicecampaign AB面
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_PAY}createNoPaymentOrder")
    suspend fun createNoPaymentOrder(
        @Field("query_id") queryId: String,
        @Field("num") num: Int? = null,
        @Field("devicecampaign") devicecampaign: String? = null,
    ): GlobalApiResponse<OrderBean>

    /**
     * 事件发送；返回原始响应，由调用方读取并关闭。
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_STATISTICS}saveEvent")
    suspend fun saveEvent(
        @Field("event") event: String,
        @Field("info") info: String,
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
     * googlePay返回成功
     * @param orderNo 订单号
     * @param orderToken 订单加密token
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_PAY}googlePayCallback")
    suspend fun checkOrder(
        @Field("orderNo") orderNo: String,
        @Field("orderToken") orderToken: String,
    ): GlobalApiResponse<String>

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
     * 热门分享列表
     * @param mediaType all, image, video
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}shareHotMedia")
    suspend fun shareHotMedia(
        @Field("video_type") videoType: String = "",
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = 10,
        @Field("media_type") mediaType: String = "",
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 分享列表
     * @param isOwner is_owner 1：自己分享的，0：所有分享列表
     * @param mediaType all, image, video
     * @param videoType 180, 300
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_AI}shareMedia")
    suspend fun getShareZoneList(
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = 10,
        @Field("video_type") videoType: String = "",
        @Field("media_type") mediaType: String = "",
        @Field("is_owner") isOwner: Int,
    ): GlobalApiResponse<List<AiFaceBean>>

    /**
     * 用户分享素材
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}saveMediaShare")
    suspend fun saveMediaShare(
        @Field("media_id") mediaId: String,
        @Field("img_url") imgUrl: String? = null,
    ): GlobalApiResponse<AiFaceBean>

    /**
     * 删除分享素材
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}deleteMediaShare")
    suspend fun deleteMediaShare(
        @Field("media_id") mediaId: String,
    ): GlobalApiResponse<AiFaceBean>

    /**
     * 算力点日志
     */
    @FormUrlEncoded
    @POST("${BASE_ACT_USER}getUserItemLog")
    suspend fun getUserItemLog(
        @Field("page") page: Int = 1,
        @Field("limit") limit: Int = 10,
        @Field("item") item: String = "TFLOPS",
    ): GlobalApiResponse<MutableList<ShareZoneBean>>

    // 文档新增接口

    // 登录与账户

    /**
     * 获取验证码；返回原始响应，由调用方读取并关闭。
     */
    @GET("${BASE_ACT_LOGIN}captcha")
    suspend fun getCaptcha(): ResponseBody

    /**
     * 获取设备信息
     */
    @POST("${BASE_ACT_LOGIN}getDeviceInfo")
    suspend fun getDeviceInfo(
        @Query("imei") imei: String,
    ): Map<String, Any?>

    // AI 素材与生成

    /**
     * 查询AI模型
     * @param md5 MD5
     */
    @POST("${BASE_ACT_AI}getAiMod")
    suspend fun getAiMod(
        @Query("md5") md5: String,
    ): Map<String, Any?>

    /**
     * 保存AI模型
     * @param md5 MD5
     * @param fromType 1, 2
     */
    @POST("${BASE_ACT_AI}saveAiMod")
    suspend fun saveAiMod(
        @Query("content") content: String,
        @Query("md5") md5: String,
        @Query("from_type") fromType: Int? = null,
    ): Map<String, Any?>

    /**
     * 查询AI模型Banner|Topic
     * @param md5 MD5
     * @param type type: banner|topic
     * @param topic 专题名称
     */
    @POST("${BASE_ACT_AI}saveAiModBannerTopic")
    suspend fun saveAiModBannerTopic(
        @Query("md5") md5: String,
        @Query("type") type: String,
        @Query("topic") topic: String? = null,
    ): Map<String, Any?>

    // 配置与反馈

    /**
     * 广告返回；返回原始响应，由调用方读取并关闭。
     */
    @POST("${BASE_ACT_API}adCallBack")
    suspend fun adCallBack(): ResponseBody

    /**
     * 获取设备topic
     */
    @POST("${BASE_ACT_API}getAndroidTopic")
    suspend fun getAndroidTopic(): Map<String, Any?>

    /**
     * 获取算力服务列表
     */
    @POST("${BASE_ACT_API}getTFLOPConfigs")
    suspend fun getLegacyTFLOPConfigs(): Map<String, Any?>

    /**
     * 保存设备topic
     * @param topic DailyBible_EN_HHmm
     * @param time 显示设置的时间HHmm
     */
    @POST("${BASE_ACT_API}saveAndroidTopic")
    suspend fun saveAndroidTopic(
        @Query("topic") topic: String,
        @Query("time") time: String,
        @Query("push_token") pushToken: String,
        @Query("type") type: String = "dailybible",
    ): Map<String, Any?>

    // 支付

    /**
     * 通过A提交时创建订单
     * @param queryId vip
     * @param num 购买数量，默认传1
     */
    @POST("${BASE_ACT_PAY}createNoUserOrder")
    suspend fun createNoUserOrder(
        @Query("imei") imei: String,
        @Query("query_id") queryId: Int,
        @Query("num") num: Int? = null,
    ): Map<String, Any?>

    /**
     * googlePay异步返回查询；返回原始响应，由调用方读取并关闭。
     * @param token 订单加密token
     */
    @GET("${BASE_ACT_PAY}getGoogleQuery")
    suspend fun getGoogleQuery(
        @Query("token") token: String,
    ): ResponseBody

    /**
     * googlePay异步返回；返回原始响应，由调用方读取并关闭。
     * 请求体由调用方构造 RequestBody，JSON 内容应使用 application/json。
     */
    @POST("${BASE_ACT_PAY}googleNotify")
    suspend fun googleNotify(
        @Body body: RequestBody = "".toRequestBody("application/json".toMediaType()),
    ): ResponseBody

    /**
     * googlePay查询同步返回
     * @param orderNo 订单号
     * @param orderToken 订单加密token
     */
    @POST("${BASE_ACT_PAY}googlePayCallbackMsg")
    suspend fun googlePayCallbackMsg(
        @Query("orderNo") orderNo: String,
        @Query("orderToken") orderToken: String,
    ): Map<String, Any?>

    /**
     * googlePay取消订阅
     * @param orderNo 订单号
     */
    @POST("${BASE_ACT_PAY}googlePayRevoke")
    suspend fun googlePayRevoke(
        @Query("orderNo") orderNo: String,
    ): Map<String, Any?>

    /**
     * apple支付回调
     * @param oid apple下单接口返回的oid
     * @param transid 此次appgle支付流水号
     * @param payload apple支付返回的验证代码
     */
    @POST("${BASE_ACT_PAY}ios/pay")
    suspend fun iosPay(
        @Query("oid") oid: String,
        @Query("transid") transid: String,
        @Query("payload") payload: String,
    ): Map<String, Any?>

    // 统计

    /**
     * decodeFB；返回原始响应，由调用方读取并关闭。
     * 请求体由调用方构造 RequestBody，JSON 内容应使用 application/json。
     */
    @POST("${BASE_ACT_STATISTICS}decodeFB")
    suspend fun decodeFB(
        @Body body: RequestBody,
        @Query("fb_key") fbKey: String? = null,
    ): GlobalApiResponse<*>

    /**
     * saveAdjust；返回原始响应，由调用方读取并关闭。
     */
    @GET("${BASE_ACT_STATISTICS}saveAdjust")
    suspend fun saveAdjust(): GlobalApiResponse<*>

    @POST("${BASE_ACT_STATISTICS}saveAdjust")
    suspend fun saveAdjustPost(@Body body: RequestBody): GlobalApiResponse<*>

    /**
     * saveSingular；返回原始响应，由调用方读取并关闭。
     * 请求体由调用方构造 RequestBody，JSON 内容应使用 application/json。
     */
    @POST("${BASE_ACT_STATISTICS}saveSingular")
    suspend fun saveSingular(
        @Body body: RequestBody,
    ): GlobalApiResponse<*>

    /**
     * vcd；返回原始响应，由调用方读取并关闭。
     */
    @GET("${BASE_ACT_STATISTICS}vcd/{idate}")
    suspend fun vcd(
        @Path("idate") idate: String,
    ): GlobalApiResponse<*>

    // Grok

    /**
     * 发送信息（支持图片+文字，可指定风格、目的、画像与关键节点事件）
     * @param input content
     * @param imageUrl 图片地址（公网URL或base64的data URL），可选，识别图片内容
     * @param style 回复风格：elegant/humorous/steady
     * @param purpose 交流目的：affection/date/relationship
     * @param profile 聊天对象画像提示词（由summary接口返回，可保存后回传），可选
     * @param events 已保存的关键节点事件JSON数组字符串，可选；传回后AI对含义相同的内容去重，不重复返回event
     */
    @POST("${BASE_ACT_GROK}send")
    suspend fun sendGrokMessage(
        @Query("input") input: String? = null,
        @Query("imageUrl") imageUrl: String? = null,
        @Query("style") style: String? = null,
        @Query("purpose") purpose: String? = null,
        @Query("profile") profile: String? = null,
        @Query("events") events: String? = null,
    ): Map<String, Any?>

    /**
     * 总结聊天对象画像
     * @param input 聊天记录文字，可为空（为空时仅分析截图）
     * @param imageUrl 聊天记录截图地址（公网URL或base64的data URL），可为空
     * @param events 已保存的关键节点事件JSON数组字符串，可选；传回后含义相同的节点不会重复输出
     */
    @POST("${BASE_ACT_GROK}summary")
    suspend fun summarizeGrokConversation(
        @Query("input") input: String? = null,
        @Query("imageUrl") imageUrl: String? = null,
        @Query("events") events: String? = null,
    ): Map<String, Any?>

    // AI 任务节点

    /**
     * getAiTask（任务节点接口，需 ai-node 请求头）
     * @param aiNode 节点名称
     */
    @POST("${BASE_ACT_AI_TASK}get")
    suspend fun getAiTask(
        @Header("ai-node") aiNode: String,
    ): Map<String, Any?>

    /**
     * getAiTaskState（任务节点接口，需 ai-node 请求头）
     * @param aiNode 节点名称
     */
    @POST("${BASE_ACT_AI_TASK}getState/{task_id}")
    suspend fun getAiTaskState(
        @Path("task_id") taskId: String,
        @Header("ai-node") aiNode: String,
    ): Map<String, Any?>

    /**
     * reportAiTask（任务节点接口，需 ai-node 请求头）
     * 请求体由调用方构造 RequestBody，JSON 内容应使用 application/json。
     * @param aiNode 节点名称
     */
    @POST("${BASE_ACT_AI_TASK}report")
    suspend fun reportAiTask(
        @Body json: RequestBody,
        @Header("ai-node") aiNode: String,
    ): Map<String, Any?>

    // 站点

    /**
     * index；返回原始响应，由调用方读取并关闭。
     */
    @GET(BASE_ACT_ROOT)
    suspend fun getIndex(): ResponseBody

    /**
     * getAppAds；返回原始响应，由调用方读取并关闭。
     */
    @GET("${BASE_ACT_ROOT}app-ads.txt")
    suspend fun getAppAds(): ResponseBody
}
