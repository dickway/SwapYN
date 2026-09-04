package com.face.adapter.search

import com.face.R
import com.face.bean.SearchHotItemBean
import com.face.databinding.ItemSearchHotBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class SearchHotAdapter : BaseAdapter<SearchHotItemBean, ItemSearchHotBinding>(
    R.layout.item_search_hot,
    SearchHotItemBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemSearchHotBinding>,
        binding: ItemSearchHotBinding,
        data: SearchHotItemBean?,
        position: Int
    ) {
        binding.tvName.text = data?.title
    }
}