package com.face.net

import com.face.bean.AiFaceBean
import com.face.bean.ConfigBean
import com.face.bean.UserBean
import com.face.key.Constants
import com.face.ui.LoginActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.net.ApiResponse
import com.zzkj.structure.net.BaseRepository
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.singleToast
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import kotlin.math.abs


/**
 * @author 再战科技
 * @date 2022/2/18
 * @description
 */
object Repository : BaseRepository() {

    private var service = RetrofitClient.getService()

    private var service2 = RetrofitClient.getService2()

    suspend fun getVipScheme() = executeHttp {
        service.getVipScheme()
    }

    suspend fun saveUserSetting(vararg beans: ConfigBean) = saveUserSetting(beans.toList())

    suspend fun saveUserSetting(beans: List<ConfigBean>) = executeHttp {
        service.saveUserSetting(MoshiHelper.listAdapter(ConfigBean::class.java).toJson(beans))
    }

    suspend fun getUserSetting(type: String) = executeHttp { service.getUserSetting(type) }

    suspend fun getBanners() = executeHttp { service.getBanners() }

    suspend fun userGifts() = executeHttp { service.userGifts() }


    suspend fun getUserLikeMedia() = executeHttp { service.getUserLikeMedia() }

    suspend fun getRecommendMedia() = executeHttp { service.getRecommendMedia() }

    suspend fun getHots() = executeHttp { service.getHots() }


    suspend fun getTopSearchData() =
        executeHttp { service.getTopSearchData() }


    suspend fun searchAi(type: String, keywords: String) =
        executePagingHttp<List<AiFaceBean>, AiFaceBean> { page, pageSize ->
            service.searchAi(type, keywords, page, pageSize)
        }


    suspend fun getShortMedia(
        mediaId: String?,
        tag: String?,
        page: Int
    ) = executeHttp {
        service.getShortMedia(mediaId, tag, page)
    }

    suspend fun overMedia(
        mediaId: String?,
        sate: Int
    ) = executeHttp {
        service.overMedia(mediaId, sate)
    }

    suspend fun getMediaByID(
        id: String
    ) = executeHttp {
        service.getMediaByID(id)
    }

    suspend fun getMediaByGroup(
        groupId: String
    ) = executeHttp {
        service.getMediaByGroup(groupId)
    }


    suspend fun getUserAiMedia(
        type: String, param: String = ""
    ) = executeHttp {
        service.getUserAiMedia(type, param)
    }

    suspend fun getStyle(
        type: String
    ) = executeHttp {
        service.getSysConfig(type)
    }

    suspend fun getGift(
        type: String
    ) = executeHttp {
        service.getSysConfig2(type)
    }

    suspend fun giftOverNum() = executeHttp {
        service.giftOverNum()
    }

    suspend fun saveGiftUser() = executeHttp {
        service.saveGiftUser()
    }

    suspend fun exchangeUserVip(productId: String) = executeHttp {
        service.exchangeUserVip(productId)
    }


    suspend fun getShareLink() = executeHttp {
        service.createshareUrl()
    }

    suspend fun addUserTFLOPS(content: String) = executeHttp {
        service.addUserTFLOPS(content)
    }

    suspend fun getRatingRewardInfo() = executeHttp {
        service.getRatingRewardInfo()
    }

    suspend fun ratingReward(configId: Int) = executeHttp {
        service.ratingReward(configId)
    }

    suspend fun signReward(id: Int) = executeHttp {
        service.signReward(id)
    }

    suspend fun getSignRewardConfig() = executeHttp {
        service.getSignRewardConfig()
    }


    suspend fun getUserViewHistory() = executeHttp {
        service.getUserViewHistory()
    }

    suspend fun img2VideoConfig() = executeHttp {
        service.img2VideoConfig()
    }


    suspend fun getTFLOPConfigs() = executeHttp {
        service.getTFLOPConfigs()
    }

    suspend fun saveVideoImg(media_id: String?, img_url: String) = executeHttp {
        service.saveVideoImg(media_id, img_url)
    }


    suspend fun subUserTFLOPS(num: Int = 1, type: String) = executeHttp {
        service.subUserTFLOPS(num, type)
    }

    suspend fun deleteUserAiMedia(
        id: String
    ) = executeHttp {
        service.deleteUserAiMedia(id)
    }

    suspend fun userMediaNum() = executeHttp {
        service.userMediaNum()
    }

    suspend fun payUserMediaNum(num: Int, type: String) = executeHttp {
        service.payUserMediaNum(num, type)
    }


    suspend fun ratingShare(
        taskId: String, mediaId: String, score: Float
    ) = executeHttp {
        service.ratingShare(taskId, mediaId, score)
    }

    suspend fun getSysConfig(
        type: String
    ) = executeHttp {
        service.getSysConfig(type)
    }

    suspend fun getUserAiTask(
        type: String
    ) = executeHttp {
        service.getUserAiTask(type)
    }


    suspend fun getDeviceCampaign() = executeHttp {
        service.getDeviceCampaign(
            SPUtils.gaid, SPUtils.installReferrer, GVM.INSTANT.userInfo.value.userId
        )
    }

    suspend fun setHeaderType(
        scr: String,
        type: String
    ) = executeHttp {
        EventUtil.clickCheck(type)
        service.setHeaderType(scr, type)
    }

    suspend fun getUserPics(type: String = "") = executeHttp {
        var typeData = type
        when (type) {
            "All" -> typeData = ""
        }
        service.getUserPics(typeData)
    }

    suspend fun mediaBlack(mediaId: String = "", content: String = "", bolck: Int) = executeHttp {
        service.mediaBlack(mediaId, content, bolck)
    }


    suspend fun sendAiTask(type: String, media_id: String?, sources: String) =
        executeHttp {
            service.sendAiTask(type, media_id, sources)
        }

    suspend fun sendGroupAiTask(type: String, groupId: String?, sources: String) =
        executeHttp {
            service.sendGroupAiTask(type, groupId, sources)
        }


    suspend fun saveUserAiMedia(
        type: String,
        url: String,
        tags: String,
        hw: String,
        param: String = "",
        img_url: String = ""
    ) = executeHttp {
        service.saveUserAiMedia(type, url, tags, hw, param, img_url)
    }


    suspend fun saveUserGenerateRecords(url: String?, type: String, name: String?) = executeHttp {
        service.saveUserGenerateRecords(null, type, name, url)
    }

    suspend fun updataUserGenerateRecords(id: String?, type: String, name: String?) = executeHttp {
        service.saveUserGenerateRecords(id, type, name, null)
    }

    suspend fun getUserGenerate(id: String?) = executeHttp {
        service.getUserGenerate(id)
    }

    suspend fun queryAiTask(taskId: String) = executeHttp {
        service.queryAiTask(taskId)
    }

    suspend fun refundAiGroupTask(taskGroup: String) = executeHttp {
        service.refundAiGroupTask(taskGroup)
    }

    suspend fun queryAiGroupTask(taskGroup: String) = executeHttp {
        service.queryAiGroupTask(taskGroup)
    }

    suspend fun resetAiTask(taskId: String) = executeHttp {
        service.resetAiTask(taskId)
    }


    suspend fun deleteUserInfo() = executeHttp {
        service.deleteUserInfo()
    }


    suspend fun cancelTask(taskId: String) = executeHttp {
        service.cancelTask(taskId)
    }


    suspend fun getUserMedia(mediaType: String) = executeHttp {
        var typeData = ""
        when (mediaType) {
            "All" -> typeData = ""
            "Video" -> typeData = "video"
            "Picture" -> typeData = "image"
        }
        service.getUserMedia(typeData)
    }

    suspend fun getUserPlayMedia(mediaType: String) = executeHttp {
        service.getUserMedia(mediaType)
    }


    suspend fun getUserRecordPage(type: String?) = executeHttp {
        service.getUserRecordPage(type)
    }

    suspend fun deleteUserPic(dis: String?) = executeHttp {
        service.deleteUserPic(dis)
    }

    suspend fun deleteUserRecord(dis: String?) = executeHttp {
        service.deleteUserRecord(dis)
    }


    suspend fun deleteUserMedia(id: String?, type: String = "task") = executeHttp {
        service.deleteUserMedia(id, type)
    }

    suspend fun removeUserView() = executeHttp {
        service.removeUserView()
    }


    suspend fun updateUserPicSort(json: String) = executeHttp {
        service.updateUserPicSort(json.toRequestBody("application/json".toMediaTypeOrNull()))
    }


    suspend fun getUserCollect() = executeHttp {
        service.getUserCollect()
    }


    suspend fun saveCollect(
        id: String,
        type: String,
        num: Int = 0
    ) = executeHttp {
        EventUtil.clickCollect(id, type)
        service.saveCollect(id, type, num)
    }


    suspend fun removeUserCollect(
        id: String
    ) = executeHttp {
        service.removeUserCollect(id)
    }


    suspend fun getDateNewMedia(
        date: String?
    ) = executeHttp {
        service.getDateNewMedia(date)
    }

    suspend fun getUserCollectUrl(
        url: String?
    ) = executeHttp {
        service.getUserCollectUrl(url)
    }

    suspend fun getUserCollectMedia(
        mediaId: String?
    ) = executeHttp {
        service.getUserCollectMedia(mediaId)
    }

    suspend fun getMediaHomeATag(
        tag: String
    ) = executeHttp {
        service.getMediaByTag("", "image", tag, 1, 20)
    }

    suspend fun getMediaByTag(
        isHot: String, type: String, tag: String
    ) = executePagingHttp<List<AiFaceBean>, AiFaceBean> { page, pageSize ->
        val tageData = if (tag == "All") "" else tag
        service.getMediaByTag(isHot, type, tageData, page, pageSize)
    }

//    suspend fun getTopic(
//        openValue: Int
//    ) = executeHttp {
//        service.getTopic(openValue)
//    }

    suspend fun getTopic(
        openValue: Int
    ) = executePagingHttp<List<AiFaceBean>, AiFaceBean> { page, pageSize ->
        service.getTopic(openValue, page, pageSize)
    }

    suspend fun getBannerDetails() =
        executePagingHttp<List<AiFaceBean>, AiFaceBean> { page, pageSize ->
            service.getBanners(page, pageSize)
        }

    suspend fun getUserLikeMedias() =
        executePagingHttp<List<AiFaceBean>, AiFaceBean> { page, pageSize ->
            service.getUserLikeMedias(page, pageSize)
        }

    suspend fun getHotsDetails() =
        executePagingHttp<List<AiFaceBean>, AiFaceBean> { page, pageSize ->
            service.getHots(page, pageSize)
        }


    suspend fun getMediaByTagHome(
        isHot: String, type: String, tag: String, page: Int
    ) = executeHttp {

        var hotData = "new"
        when (isHot) {
            "Latest" -> hotData = "new"
            "Popular" -> hotData = "hot"
        }
        var typeData = ""
        when (type) {
            "All" -> typeData = ""
            "Video" -> typeData = "video"
            "Picture" -> typeData = "image"
        }
        val tageData: String = if (tag == "All") "" else tag
        service.getMediaByTag(hotData, typeData, tageData, page, Constants.pageSize)
    }

    suspend fun getTags(type: Int) =
        executeHttp { service.getTags(type) }

    /**
     * 用户反馈
     * @param email String
     * @param content String
     * @param fileUrl String?
     * @param videoUrl String? 用户反馈上传的视频
     * @param videoId Int?
     * @param seasonsIndex Int?
     * @param episodesIndex Int?
     * @return ApiResponse<Int>
     */
    suspend fun saveFeedback(
        email: String,
        content: String,
        fileUrl: String? = null,
        fileIsImage: Boolean = true,
        videoId: String? = null,
        seasonsIndex: Int? = null,
        episodesIndex: Int? = null,
        params: String? = null
    ): ApiResponse<Int> = executeHttp {
        service.saveFeedback(
            email,
            JSONObject().apply {
                put("content", content)
                if (!fileUrl.isNullOrBlank()) {
                    put(
                        if (fileIsImage) "imgUrls" else "videoUrls",
                        JSONArray().apply { put(fileUrl) })
                }
                if (!videoId.isNullOrBlank()) {
                    put("mediaId", videoId)
                }
                if (seasonsIndex != null && seasonsIndex > 0) {
                    put("seasonsIndex", seasonsIndex)
                }
                if (episodesIndex != null && episodesIndex > 0) {
                    put("episodesIndex", episodesIndex)
                }
            }.toString(),
            params
        )
    }

    suspend fun sendFeedbackMsg(
        feedbackId: Int, content: String?, imgUrl: String?, videoUrl: String?
    ): ApiResponse<*> = executeHttp {
        service.sendFeedbackMsg(
            feedbackId,
            JSONObject().apply {
                if (!content.isNullOrEmpty()) {
                    put("content", content)
                }
                if (!imgUrl.isNullOrBlank()) {
                    put("imgUrls", JSONArray().apply { put(imgUrl) })
                }
                if (!videoUrl.isNullOrBlank()) {
                    put("videoUrls", JSONArray().apply { put(videoUrl) })
                }
            }.toString(),
        )
    }

    suspend fun getFeedback() = executeHttp { service.getFeedback() }

    suspend fun getFeedbackMsg(feedbackId: Int) = executeHttp { service.getFeedbackMsg(feedbackId) }


    suspend fun createNoPaymentOrder(schemeId: String) =
        executeHttp { service.createNoPaymentOrder(schemeId) }

    suspend fun checkOrder(orderId: String, purchaseToken: String) =
        executeHttp { service.checkOrder(orderId, purchaseToken) }


    suspend fun googleLogin(type: String = "google", id: String, token: String) = executeHttp {
        service.googleLogin(type, id = id, token = token)
    }

    suspend fun accountLogin(username: String, password: String) = executeHttp {
        service.accountLogin(username, password)
    }

    suspend fun accountRegister(username: String, password: String) = executeHttp {
        service.accountRegister(username, password)
    }

    suspend fun updatePassword(password: String, newPassword: String) = executeHttp {
        service.updatePassword(password, newPassword)
    }


    suspend fun bindGuestUser(id: String, token: String) = executeHttp {
        service.bindGuestUser(id = id, token = token)
    }


    suspend fun getUserInfo() = executeHttp { service.getUserInfo() }

    suspend fun getUserSignRewards() = executeHttp { service.getUserSignRewards() }

    suspend fun signLog(
        type: Int,
        fromType: Int,
        day: Int
    ) = executeHttp { service.signLog(type, fromType, day) }

    suspend fun getUserSignInLog(month: Int) = executeHttp { service.getUserSignInLog(month) }

    suspend fun savePushToken(pushToken: String) = executeHttp {
        service.savePushToken(pushToken)
    }

    /**
     * 上传头像
     * @param file File
     * @return ApiResponse<String>
     */
    suspend fun uploadFile(file: MultipartBody.Part) = executeHttp {
        service.uploadFile(file)
    }

    /**
     * 获取上传链接
     * @param fileName 文件格式
     * @return ApiResponse<String>
     */
    suspend fun getPresignedUrl(fileName: String) = executeHttp {
        service.getPresignedUrl(fileName)
    }

    /**
     * 通用上传文件
     * @param file File
     * @param type String
     * @return ApiResponse<String>
     */
    suspend fun uploadFile(file: File, type: String) = executeHttp {
        service.uploadFile(
            MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody(type.toMediaTypeOrNull())
            )
        )
    }

    suspend fun completeUpload(json: String) = executeHttp {
        service.completeUpload(json.toRequestBody("application/json".toMediaTypeOrNull()))
    }


    suspend fun getPresignedUrlPart(fileName: String, num: Int) =
        executeHttp { service.getPresignedUrlPart(fileName, num) }


    suspend fun uploadChunkFile(url: String, repository: RequestBody) =
        service2.uploadFile2(url, repository)


    suspend fun saveEvent(eventName: String, parameter: String) =
        executeHttp { service.saveEvent(eventName, parameter) }

    override fun handleCodeError(code: Int, msg: String?) {
        //登录过期
        if (abs(code) == 9 && AppManager.topActivity?.localClassName?.contains(
                "LoginActivity",
                true
            ) != true && AppManager.topActivity?.localClassName?.contains(
                "WelcomeActivity",
                true
            ) != true && AppManager.topActivity !is LoginActivity
        ) {
            singleToast(msg)
            GVM.INSTANT.launch(Dispatchers.Main) {
                GVM.INSTANT.updateUserInfo(UserBean(), 4)
            }
        }
    }

    override fun getDefaultPageSize(): Int = Constants.pageSize


    /**
     * 分享列表
     */
    suspend fun getShareZoneList(
        page: Int = 1, is_owner: Int = 0, video_type: String = ""
    ) = executeHttp {
        var media_type: String

        val type = when {
            "1" in video_type -> {
                media_type = "video"
                "20"
            }

            "3" in video_type -> {
                media_type = "video"
                "180"
            }

            "5" in video_type -> {
                media_type = "video"
                "300"
            }

            "image" in video_type -> {
                media_type = "image"
                ""
            }

            "video" in video_type -> {
                media_type = "video"
                ""
            }

            else -> {
                media_type = "all"
                ""
            }
        }

        service.getShareZoneList(
            page = page,
            is_owner = is_owner,
            media_type = media_type,
            video_type = type
        )
    }

    suspend fun shareHotMedia(page: Int = 1, video_type: String = "") =
        executeHttp {
            var media_type: String

            val type = when {
                "1" in video_type -> {
                    media_type = "video"
                    "20"
                }

                "3" in video_type -> {
                    media_type = "video"
                    "180"
                }

                "5" in video_type -> {
                    media_type = "video"
                    "300"
                }

                "image" in video_type -> {
                    media_type = "image"
                    ""
                }

                else -> {
                    media_type = "all"
                    ""
                }
            }

            service.shareHotMedia(page = page, video_type = type, media_type = media_type)
        }

    suspend fun saveMediaShare(
        mediaId: String
    ) = executeHttp { service.saveMediaShare(mediaId = mediaId) }

    suspend fun deleteMediaShare(
        mediaId: String
    ) = executeHttp { service.deleteMediaShare(mediaId = mediaId) }

    suspend fun getUserItemLog(page: Int = 1) = executeHttp { service.getUserItemLog(page) }


}