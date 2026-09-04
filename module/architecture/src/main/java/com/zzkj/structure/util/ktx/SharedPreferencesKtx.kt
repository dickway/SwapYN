package com.zzkj.structure.util.ktx

import com.zzkj.structure.base.BaseApp.Companion.INSTANCE as app
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.apkfuns.logutils.LogUtils
import com.zzkj.structure.util.moshi.MoshiHelper
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author lmk
 * @date 2023/1/15
 * @description
 */
open class BaseSharedPreferences(name: String) {


    val sp: SharedPreferences by lazy {
        app.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    protected fun preference(
        default: String,
        key: String? = null,
        apply: Boolean = true,
    ) = Property(default = default, key = key, apply = apply)

    protected fun preference(
        default: Int,
        key: String? = null,
        apply: Boolean = true,
    ) = Property(default = default, key = key, apply = apply)

    protected fun preference(
        default: Long,
        key: String? = null,
        apply: Boolean = true,
    ) = Property(default = default, key = key, apply = apply)

    protected fun preference(
        default: Float,
        key: String? = null,
        apply: Boolean = true,
    ) = Property(default = default, key = key, apply = apply)

    protected fun preference(
        default: Double,
        key: String? = null,
        apply: Boolean = true,
    ) = Property(default = default, key = key, apply = apply)

    protected fun preference(
        default: Boolean,
        key: String? = null,
        apply: Boolean = true,
    ) = Property(default = default, key = key, apply = apply)

    protected inline fun <reified V : Any> preferenceObject(
        default: V,
        key: String? = null,
        apply: Boolean = true,
    ) = ObjectProperty(default = default, key = key, clazz = V::class.java, apply = apply)

    protected inline fun <reified V : Any> preferenceList(
        default: List<V>,
        key: String? = null,
        apply: Boolean = true,
    ) = ListProperty(default = default, key = key, clazz = V::class.java, apply = apply)

    protected class Property<V>(
        private val default: V,
        private val key: String? = null,
        private val apply: Boolean = true,
    ) : ReadWriteProperty<BaseSharedPreferences, V> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: BaseSharedPreferences, property: KProperty<*>): V =
            with(thisRef.sp) {
                val k = key ?: property.name
                when (default) {
                    is String -> get(k, default) as V
                    is Int -> get(k, default) as V
                    is Long -> get(k, default) as V
                    is Float -> get(k, default) as V
                    is Double -> get(k, default) as V
                    is Boolean -> get(k, default) as V
                    else -> throw IllegalArgumentException()
                }
            }

        @SuppressLint("ApplySharedPref")
        override fun setValue(thisRef: BaseSharedPreferences, property: KProperty<*>, value: V) {
            with(thisRef.sp.edit()) {
                val k = key ?: property.name
                when (value) {
                    is String -> put(k, value, apply)
                    is Int -> put(k, value, apply)
                    is Long -> put(k, value, apply)
                    is Float -> put(k, value, apply)
                    is Double -> put(k, value, apply)
                    is Boolean -> put(k, value, apply)
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    protected class ObjectProperty<V>(
        private val default: V,
        private val clazz: Class<V>,
        private val key: String? = null,
        private val apply: Boolean = true,
    ) : ReadWriteProperty<BaseSharedPreferences, V> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: BaseSharedPreferences, property: KProperty<*>): V =
            with(thisRef.sp) {
                val k = key ?: property.name
                getString(k, null).takeIf { !it.isNullOrBlank() }?.let {
                    MoshiHelper.adapter(clazz).fromJson(it)
                } ?: default
            }

        @SuppressLint("ApplySharedPref")
        override fun setValue(thisRef: BaseSharedPreferences, property: KProperty<*>, value: V) {
            with(thisRef.sp.edit()) {
                val k = key ?: property.name
                put(k, MoshiHelper.adapter(clazz).toJson(value), apply)
            }
        }
    }

    protected class ListProperty<V>(
        private val default: List<V>,
        private val clazz: Class<V>,
        private val key: String? = null,
        private val apply: Boolean = true,
    ) : ReadWriteProperty<BaseSharedPreferences, List<V>> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: BaseSharedPreferences, property: KProperty<*>): List<V> =
            with(thisRef.sp) {
                val k = key ?: property.name
                getString(k, null).takeIf { !it.isNullOrBlank() }?.let {
                    MoshiHelper.listAdapter(clazz).fromJson(it)
                } ?: default
            }

        @SuppressLint("ApplySharedPref")
        override fun setValue(
            thisRef: BaseSharedPreferences,
            property: KProperty<*>,
            value: List<V>,
        ) {
            with(thisRef.sp.edit()) {
                val k = key ?: property.name
                put(k, MoshiHelper.listAdapter(clazz).toJson(value), apply)
            }
        }
    }
}

fun SharedPreferences.Editor.put(key: String, value: Boolean, apply: Boolean? = null) {
    putBoolean(key, value)
    save(this, apply)
}

fun SharedPreferences.Editor.put(key: String, value: Double, apply: Boolean? = null) {
    putLong(key, value.toRawBits())
    save(this, apply)
}

fun SharedPreferences.Editor.put(key: String, value: Int, apply: Boolean? = null) {
    putInt(key, value)
    save(this, apply)
}

fun SharedPreferences.Editor.put(key: String, value: Long, apply: Boolean? = null) {
    putLong(key, value)
    save(this, apply)
}

fun SharedPreferences.Editor.put(key: String, value: String, apply: Boolean? = null) {
    putString(key, value)
    save(this, apply)
}

fun SharedPreferences.Editor.put(key: String, value: Set<String>, apply: Boolean? = null) {
    putStringSet(key, value)
    save(this, apply)
}

fun SharedPreferences.Editor.put(key: String, value: Float, apply: Boolean? = null) {
    putFloat(key, value)
    save(this, apply)
}

private fun save(editor: SharedPreferences.Editor, apply: Boolean?) {
    if (apply == true) {
        editor.apply()
    } else if (apply == false) {
        editor.commit()
    }
}

fun SharedPreferences.get(key: String, default: Boolean) = getBoolean(key, default)
fun SharedPreferences.get(key: String, default: Float) = getFloat(key, default)
fun SharedPreferences.get(key: String, default: Int) = getInt(key, default)
fun SharedPreferences.get(key: String, default: Long) = getLong(key, default)
fun SharedPreferences.get(key: String, default: String) = getString(key, default) ?: default
fun SharedPreferences.get(key: String, default: Set<String>) = getStringSet(key, default) ?: default
fun SharedPreferences.get(key: String, default: Double) = if (contains(key)) {
    Double.fromBits(getLong(key, 0))
} else {
    default
}


inline fun <reified V : Any> SharedPreferences.getObject(key: String, default: V): V =
    getString(key, null).takeIf { !it.isNullOrBlank() }?.let {
        MoshiHelper.adapter(V::class.java).fromJson(it)
    } ?: default

@SuppressLint("ApplySharedPref")
inline fun <reified V> SharedPreferences.Editor.putObject(
    key: String,
    value: V,
    apply: Boolean = true,
) {
    put(key, MoshiHelper.adapter(V::class.java).toJson(value), apply)
}


inline fun <reified V : Any> SharedPreferences.getList(key: String, default: List<V>): List<V> =
    getString(key, null)?.takeIf { it.isNotBlank() }?.let {
        MoshiHelper.listAdapter(V::class.java).fromJson(it)
    } ?: default

@SuppressLint("ApplySharedPref")
inline fun <reified V> SharedPreferences.Editor.putList(
    key: String,
    value: List<V>,
    apply: Boolean = true,
) {
    put(key, MoshiHelper.listAdapter(V::class.java).toJson(value), apply)
}
