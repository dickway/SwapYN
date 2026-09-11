package com.a.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzkj.structure.util.ktx.dp

/** Equal gaps between tiles, with no inset at the outside edges of the grid. */
class AMeGridItemDecoration : RecyclerView.ItemDecoration() {
    private val spacing = 2.dp

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.setEmpty()
        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return
        if (parent.getChildAdapterPosition(view) == RecyclerView.NO_POSITION) return
        val column = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        val spanCount = layoutManager.spanCount
        val start = column * spacing / spanCount
        val end = spacing - (column + 1) * spacing / spanCount
        val isRtl = parent.layoutDirection == View.LAYOUT_DIRECTION_RTL
        outRect.set(if (isRtl) end else start, 0, if (isRtl) start else end, spacing)
    }
}
