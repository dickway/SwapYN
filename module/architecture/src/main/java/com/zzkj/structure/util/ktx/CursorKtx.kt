package com.zzkj.structure.util.ktx

import android.database.Cursor

fun Cursor.getBlobOrNullByColumnName(columnName: String): ByteArray? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getBlob(index)
    }

fun Cursor.getBlobByColumnName(columnName: String, default: ByteArray): ByteArray =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getBlob(index)
    } ?: default

fun Cursor.getDoubleOrNullByColumnName(columnName: String): Double? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getDouble(index)
    }

fun Cursor.getDoubleByColumnName(columnName: String, default: Double = 0.0): Double =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getDouble(index)
    } ?: default

fun Cursor.getFloatOrNullByColumnName(columnName: String): Float? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getFloat(index)
    }

fun Cursor.getFloatOrNullByColumnName(columnName: String, default: Float = 0F): Float =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getFloat(index)
    } ?: default

fun Cursor.getIntOrNullByColumnName(columnName: String): Int? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getInt(index)
    }

fun Cursor.getIntByColumnName(columnName: String, default: Int = 0): Int =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getInt(index)
    } ?: default

fun Cursor.getLongOrNullByColumnName(columnName: String): Long? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getLong(index)
    }

fun Cursor.getLongByColumnName(columnName: String, default: Long = 0): Long =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getLong(index)
    } ?: default

fun Cursor.getShortOrNullByColumnName(columnName: String): Short? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getShort(index)
    }

fun Cursor.getShortByColumnName(columnName: String, default: Short = 0): Short =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getShort(index)
    } ?: default

fun Cursor.getStringOrNullByColumnName(columnName: String): String? =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) null else getString(index)
    }

fun Cursor.getStringByColumnName(columnName: String, default: String = ""): String =
    getColumnIndex(columnName).takeIf { it != -1 }?.let { index ->
        if (isNull(index)) default else getString(index)
    } ?: default