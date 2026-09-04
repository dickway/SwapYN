package com.face.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg elements: T)

    @Delete
    fun delete(element: T)

    @Delete
    fun delete(elements: List<T>)

    @Delete
    fun delete(vararg elements: T)

    @Update
    fun update(element: T)
}