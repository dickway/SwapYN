package com.zzkj.structure.util.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * @author lmk
 * @date 2022/2/18
 * @description
 */
object MoshiHelper {
    val moshi: Moshi = Moshi
        .Builder()
        .add(DefaultIfNullFactory)
//        .add(DefaultIfNullFactory())
        .build()

    fun <T> adapter(type: Class<T>): JsonAdapter<T> = moshi.adapter(type)

    fun <T> listAdapter(type: Class<T>): JsonAdapter<List<T>> {
        val listType: Type = Types.newParameterizedType(
            List::class.java,
            type
        )
        return moshi.adapter(listType)
    }
    inline fun <reified T> convertJsonToList(json: String): List<T>? {
        val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
        return moshi.adapter<List<T>>(type).fromJson(json)
    }

    inline fun <reified T> convertObjectToJson(objectData: T): String =
        moshi.adapter(T::class.java).toJson(objectData)
}