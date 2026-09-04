package com.zzkj.structure.net

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class GlobalApiResponse<T> : ApiResponse<T>() {

    @Json(name = "data")
    override var mData: T? = null

    @Json(name = "code")
    override var mCode: Int = transformCode()

    @Json(name = "msg")
    override var mMsg: String? = transformMsg()

}
