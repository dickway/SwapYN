package com.zzkj.structure.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author lmk
 * @date 2022/2/16
 * @description
 */
class CommonFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var fragments = listOf<Fragment>()

    constructor(fragment: Fragment) : this(fragment.childFragmentManager, fragment.lifecycle)
    constructor(fragmentActivity: FragmentActivity) : this(
        fragmentActivity.supportFragmentManager,
        fragmentActivity.lifecycle
    )

    fun setFragments(vararg fragment: Fragment) = apply {
        fragments = fragment.toList()
    }

    fun setFragments(fragments: List<Fragment>) = apply {
        this.fragments = fragments
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}