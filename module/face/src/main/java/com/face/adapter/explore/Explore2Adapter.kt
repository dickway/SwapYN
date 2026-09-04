package com.face.adapter.explore

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.util.GVM
import com.face.R
import com.face.bean.Explore2Bean
import com.face.databinding.ItemExplore2Binding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class Explore2Adapter : BaseAdapter<Explore2Bean, ItemExplore2Binding>(
    R.layout.item_explore2, Explore2Bean.differCallback
) {


    override fun onBindData(
        holder: BaseHolder<ItemExplore2Binding>,
        binding: ItemExplore2Binding,
        data: Explore2Bean?,
        position: Int
    ) {
        if ((data?.list?.size?:0)>0){
            val itmeAdapter = Explore2FaceAdapter()
            holder.binding.apply {
                tvTime.visibility=View.VISIBLE
                recyclerView.visibility=View.VISIBLE
                tvTime.text = data?.getTime(tvTime.context)
                recyclerView.layoutManager =
                    StaggeredGridLayoutManager(
                        GVM.INSTANT.faceListSpan,
                        StaggeredGridLayoutManager.VERTICAL
                    )
                recyclerView.adapter = itmeAdapter
            }
            itmeAdapter.submitList(data?.list)
        }else{
            holder.binding.apply {
                tvTime.visibility=View.GONE
                recyclerView.visibility=View.GONE
            }

        }

    }

}