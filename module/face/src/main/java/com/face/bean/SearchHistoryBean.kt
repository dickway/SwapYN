package com.face.bean

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author 再战科技
 * @date 2022/3/30
 * @description
 */
@Entity(tableName = SearchHistoryBean.TABLE_NAME)
data class SearchHistoryBean(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String = "",
    /**
     * 本地数据库更新时间
     */
    @ColumnInfo(name = "update_time")
    var updateTime: Long = 0,
) {
    companion object {

        const val TABLE_NAME = "search_history"

        val differCallback = object : DiffUtil.ItemCallback<SearchHistoryBean>() {
            override fun areItemsTheSame(
                oldItem: SearchHistoryBean,
                newItem: SearchHistoryBean
            ): Boolean {
                return true
            }

            override fun areContentsTheSame(
                oldItem: SearchHistoryBean,
                newItem: SearchHistoryBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
