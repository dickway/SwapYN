package com.face.database

import androidx.room.Dao
import androidx.room.Query
import com.face.bean.SearchHistoryBean
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao : BaseDao<SearchHistoryBean> {

    @Query("select * from ${SearchHistoryBean.TABLE_NAME} order by update_time desc")
    fun getAll(): Flow<List<SearchHistoryBean>>

    @Query("delete from ${SearchHistoryBean.TABLE_NAME}")
    fun clear()
}