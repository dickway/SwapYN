package com.zzkj.structure.net

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
open class ApiResponse<T>(
    @Json(name = "ignore_state", ignore = true)
    var _mState: Int = API_STATE_SUCCESS
) : Serializable {

    @Json(name = "ignore_data", ignore = true)
    open var mData: T? = null

    @Json(name = "ignore_code", ignore = true)
    open var mCode: Int = transformCode()

    @Json(name = "ignore_error", ignore = true)
    var mError: Throwable? = null

    @Json(name = "ignore_msg", ignore = true)
    open var mMsg: String? = mError?.localizedMessage ?: transformMsg()

    @Json(name = "ignore_success", ignore = true)
    val mIsSuccess: Boolean
        get() = mCode == 0

    open fun transformCode(): Int = -1
    open fun transformMsg(): String? = null
}

const val API_STATE_SUCCESS = 0
const val API_STATE_START = 1
const val API_STATE_FAIL = 2
const val API_STATE_COMPLETE = 3

//data class ApiSuccessResponse(val response: T?) : ApiResponse<T>(data = response)
//
////class ApiEmptyResponse<T> : ApiResponse<T>()
//
//class ApiStartResponse<T> : ApiResponse<T>()
//
//class ApiCompleteResponse<T> : ApiResponse<T>()
//
//data class ApiFailedResponse<T>(
//    override val error: Throwable? = null,
//    override val code: Int = -726,
//    override val msg: String? = null
//) : ApiResponse<T>(code = code, msg = msg ?: error?.localizedMessage)
//
//data class ApiErrorResponse<T>(val throwable: Throwable) :
//    ApiResponse<T>(error = throwable, msg = throwable.localizedMessage)

