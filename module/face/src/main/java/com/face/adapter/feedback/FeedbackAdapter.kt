package com.face.adapter.feedback

import com.face.bean.FeedbackBean
import com.face.R
import com.face.databinding.ItemFeedbackBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class FeedbackAdapter : BaseAdapter<FeedbackBean, ItemFeedbackBinding>(
    layoutResId = R.layout.item_feedback,
    diffCallback = FeedbackBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemFeedbackBinding>,
        binding: ItemFeedbackBinding,
        data: FeedbackBean?,
        position: Int
    ) {
        if (data != null) {
            binding.bean = data
        }
    }
}