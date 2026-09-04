package com.face.adapter.myface

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.face.R
import com.face.bean.MyAllFaceBean
import com.face.databinding.ItemMyallfaceItemBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class MyAllFaceAdapter : BaseAdapter<MyAllFaceBean, ItemMyallfaceItemBinding>(
    R.layout.item_myallface_item, MyAllFaceBean.differCallback
) {
    var onFaceEdit: ((String?) -> Unit)? = null

    override fun onBindData(
        holder: BaseHolder<ItemMyallfaceItemBinding>,
        binding: ItemMyallfaceItemBinding,
        data: MyAllFaceBean?,
        position: Int
    ) {
        val itmeAdapter = MyAllFaceHAdapter()
        binding.apply {
            data?.apply {
                tvName.text = data.name
                if ((data.listFace?.size ?: 0) > 0) {
                    tvEdit.visibility = View.VISIBLE
                } else {
                    tvEdit.visibility = View.GONE
                }
                tvEdit.setOnClickListener { onFaceEdit?.invoke(data.name) }
            }

            if (recyclerView.layoutManager == null) {
                recyclerView.layoutManager = LinearLayoutManager(
                    null, LinearLayoutManager.HORIZONTAL, false
                )
            }
            noDataView.visibility = if (data?.listFace.isNullOrEmpty()) View.VISIBLE else View.GONE
            recyclerView.adapter = itmeAdapter

        }
        itmeAdapter.submitList(data?.listFace)
    }

}