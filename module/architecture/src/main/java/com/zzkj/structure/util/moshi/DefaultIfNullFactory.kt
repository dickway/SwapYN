package com.zzkj.structure.util.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

/**
 * @author lmk
 * @date 2022/2/15
 * @description
 */
object DefaultIfNullFactory : JsonAdapter.Factory {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*> {
        val delegate = try {
            moshi.nextAdapter<Any>(this, type, annotations)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return object : JsonAdapter<Any>() {
            override fun fromJson(reader: JsonReader): Any? {
                val jsonValue = reader.readJsonValue()
                return if (delegate == null) null else try {
                    delegate.fromJsonValue(
                        when (jsonValue) {
                            is List<*> -> {
                                jsonValue.filterNotNull()
                            }
                            is Map<*, *> -> {
                                jsonValue.filterValues { it != null }
                            }
                            else -> {
                                jsonValue
                            }
                        }
                    )
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }

            override fun toJson(writer: JsonWriter, value: Any?) {
                delegate?.toJson(writer, value)
            }
        }
    }
}