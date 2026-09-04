package com.face.adapter.myface

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.face.bean.MyFaceImgBean
import com.face.util.ItemTouchHelperAdapter
import com.face.R
import com.face.databinding.ItemMyfaceBinding
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader.loadImage

class MyFaceAdapter : BaseMultipleAdapter<MyFaceImgBean>(
    MyFaceImgBean.differCallback
), ItemTouchHelperAdapter {

    var onDeleteClick: ((MyFaceImgBean?) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return getItemData(position)?.itemType ?: 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<ViewDataBinding> {
        return innerCreateViewHolder(parent, R.layout.item_myface)
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        onImgData(binding as ItemMyfaceBinding, data, position)
    }

    fun onImgData(
        binding: ItemMyfaceBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {

        binding.apply {
            imgDelete.setOnClickListener {
                onDeleteClick?.invoke(data)
            }
            imgCover.loadImage(
                data?.pic,
                placeholderResId = com.key.R.drawable.img_default_m
            )
        }
    }

    var onMove: ((List<MyFaceImgBean>) -> Unit)? = null
    private var moveList = mutableListOf<MyFaceImgBean>()
    override fun onItemMove(source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val fromPosition = source.bindingAdapterPosition
        val toPosition = target.bindingAdapterPosition
        if (fromPosition < currentList.size && toPosition < currentList.size && fromPosition != toPosition) {
            if (moveList.size < 2) {
                moveList = currentList.toMutableList()
            }
            moveList.add(toPosition, moveList.removeAt(fromPosition));//数据更换
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    override fun onItemSelect(source: RecyclerView.ViewHolder) {
        //当拖拽选中时放大选中的view
        source.itemView.scaleX = 1.2f
        source.itemView.scaleY = 1.2f
    }

    override fun onItemClear(source: RecyclerView.ViewHolder) {
        //拖拽结束后恢复view的状态
        source.itemView.scaleX = 1.0f
        source.itemView.scaleY = 1.0f
        onMove?.invoke(moveList)
    }

}
