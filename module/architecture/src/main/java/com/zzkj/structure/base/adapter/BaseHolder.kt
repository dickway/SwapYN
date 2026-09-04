package com.zzkj.structure.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * @author
 * @date 2022/2/11
 * @description
 */
class BaseHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)