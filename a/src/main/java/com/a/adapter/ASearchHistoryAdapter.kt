package com.a.adapter

import com.face.bean.SearchHistoryBean
import com.face.R
import com.face.databinding.ItemSearchHistoryBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class ASearchHistoryAdapter : BaseAdapter<SearchHistoryBean, ItemSearchHistoryBinding>(
    R.layout.item_search_history,
    SearchHistoryBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemSearchHistoryBinding>,
        binding: ItemSearchHistoryBinding,
        data: SearchHistoryBean?,
        position: Int
    ) {
        binding.tvName.text = data?.name
    }
}