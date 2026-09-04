package com.face.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.core.widget.TextViewCompat
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.face.R
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setVisibleIfNot

class NoDataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var adapter: PagingDataAdapter<*, *>? = null

    private var mv: TextView? = null

    init {
        setBackgroundResource(R.color.colorTransparent)
        gravity = Gravity.CENTER_HORIZONTAL
        orientation = VERTICAL
        addView(ImageView(context).apply {
            setImageResource(com.key.R.drawable.img_no_data)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        })
        mv = TextView(context).apply {
            setTextColor(getColorX(R.color.colorFontHint))
            text = getStringX(R.string.nodata)
            TextViewCompat.setTextAppearance(this, R.style.font_16)
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = 12
                }
        }
        addView(mv)
        setVisibleIfNot(false)
    }

    fun setNoData(noData: Boolean) {
        setVisibleIfNot(noData)
    }

    fun setTextData(tvStr: String) {
        mv?.text = tvStr
    }


    fun setPagingAdapter(adapter: PagingDataAdapter<*, *>) {
        this.adapter = adapter
        adapter.addLoadStateListener(loadStateListener)
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = { state ->
        adapter?.let {
            setVisibleIfNot(state.refresh is LoadState.NotLoading && it.itemCount == 0)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        adapter?.addLoadStateListener(loadStateListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter?.removeLoadStateListener(loadStateListener)
    }
}