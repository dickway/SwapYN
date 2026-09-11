package com.face.adapter.other

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemCustomBinding
import com.face.databinding.ItemCustomHintBinding
import com.face.ui.SwapFaceListActivity
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.singleClick

class CustomAdapter(private val isAdapterShare: Boolean = false) : BaseMultipleAdapter<AiFaceBean>(
    AiFaceBean.differCallback
) {
    var onDeleteClick: ((AiFaceBean) -> Unit)? = null
    var onSonClick: ((view: View, position: Int, data: AiFaceBean) -> Unit)? = null
    var isShowDe = false
    var onBuyClick: ((AiFaceBean) -> Unit)? = null


    override fun getItemViewType(position: Int): Int {
        return getItemData(position)?.itemType ?: 0
    }

    fun setShowDelete(isShow: Boolean) {
        isShowDe = isShow
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<ViewDataBinding> {
        val holder: BaseHolder<ViewDataBinding> = innerCreateViewHolder(
            parent,
            when (viewType) {
                AiFaceBean.ITEM_TYPE_TYPE -> R.layout.item_custom_hint
                else -> R.layout.item_custom
            }
        )
        return holder
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        val lp = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.isFullSpan = true
        when (getItemViewType(position)) {
            AiFaceBean.ITEM_TYPE_TYPE -> {
                bindTextData(binding as ItemCustomHintBinding, data, position)
            }

            else -> {
                lp.isFullSpan = false
                bindListData(binding as ItemCustomBinding, data, position, holder)
            }
        }
    }

    private fun bindTextData(
        binding: ItemCustomHintBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        binding.apply {
            data?.subNum?.let {
                tvNum.text = if (it < 0) "-" else it.toString()
                if (it <= 5) {
                    tvBuy.visibility = View.VISIBLE
                } else {
                    tvBuy.visibility = View.GONE
                }
                tvBuy.singleClick { onBuyClick?.invoke(data) }
            }
        }
    }

    private fun bindListData(
        binding: ItemCustomBinding,
        data: AiFaceBean?,
        position: Int,
        holder: BaseHolder<ViewDataBinding>,
    ) {
        binding.apply {
            showShare = isAdapterShare
            data?.apply {
                bean = data
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 32.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 32.dp) / 2 * 4 / 3
                }
                isShow = isShowDe
                val loadUrl = imageUrl.ifEmpty {
                    webpUrl
                }
                ltState.singleClick { its ->
                    onSonClick?.invoke(its, position, data)
                }

                imgCover.loadImage(
                    loadUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.singleClick {
                    val pos = holder.bindingAdapterPosition
                    if (pos !in currentList.indices) return@singleClick
                    val list =
                        (currentList.drop(pos) + currentList.take(pos)).take(50).toMutableList()
                    SwapFaceListActivity.jump(imgCover.context, id, 0, list)
                }
                deleteImg.singleClick {
                    onDeleteClick?.invoke(data)
                }
            }
        }
    }


}