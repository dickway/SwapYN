package com.face.ui.fragment

import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import coil.load
import com.blankj.utilcode.util.LogUtils
import com.base.BlurTransformation
import com.face.BR
import com.face.R
import com.face.adapter.swap.SwapImgVideoAdapter
import com.face.bean.AiFaceBean
import com.face.bean.MediaByBean
import com.face.databinding.FragmentSwapBinding
import com.face.net.Repository
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.util.VibrationUtil
import com.face.video.CustomManager
import com.face.video.VideoHeaderManager
import com.face.video.ZoomVideo
import com.face.view.DisclaimerDialog
import com.face.view.ReportDialog
import com.face.view.ScoreDialog
import com.face.view.UpHintDialog
import com.face.viewmodel.activity.SwapListViewModel
import com.face.viewmodel.activity.SwapViewModel
import com.flyjingfish.openimagefulllib.GSYVideoOpenPlayer.GONE
import com.flyjingfish.openimagefulllib.GSYVideoOpenPlayer.VISIBLE
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.toast

class SwapFragment :
    BaseBindingFragment<FragmentSwapBinding, SwapViewModel>(
        R.layout.fragment_swap,
        SwapViewModel::class.java
    ) {

    private val type: String? by lazy {
        arguments?.getString("type")
    }

    companion object {
        fun newInstance(type: String): SwapFragment {
            val fragment = SwapFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }

    //Activity的ViewModel
    val mModelActivity: SwapListViewModel by lazy {
        getViewModel(SwapListViewModel::class.java, requireActivity())
    }

    private val viewPagerAdapter: SwapImgVideoAdapter by lazy {
        SwapImgVideoAdapter(requireActivity(), type)
    }

    lateinit var observerBlur: Observer<Int>
    lateinit var observerListBean: Observer<MutableList<AiFaceBean>>
    lateinit var observerBean: Observer<AiFaceBean>

//    private val mPreloadManager by lazy {
//        PreloadManager.getInstance()
//    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        container?.rootView?.let {
//            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
//                val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
//                val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
//                v.setPadding(0, statusBar, 0, navBar)
//                insets
//            }
//        }
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            viewpager2.orientation = ViewPager2.ORIENTATION_VERTICAL
            viewpager2.adapter = viewPagerAdapter
            viewpager2.offscreenPageLimit = 2
            (viewpager2.getChildAt(0) as? RecyclerView)?.recycledViewPool?.apply {
                setMaxRecycledViews(0, 3) // 控制每种 ViewType 最多缓存几个
            }
            viewpager2.registerOnPageChangeCallback(onPageChangeCallback)
            if (SPUtils.openScrolleApp && mModelActivity.mediaListBean.value.size > 1) {//初次界面提示上下滑动
                SPUtils.openScrolleApp = false
                UpHintDialog().showIgnoreState(activity)
            }
            //图标阴影效果
            imgCollect.setImageResource(if (GVM.INSTANT.isShowTool.value != 0) R.drawable.checkbox_collect else R.drawable.checkbox_collect2)
            tvCollect.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvDisclaimer.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvReport.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            viewPagerAdapter.onViewClick = {
                if (!mModelActivity.isCollect.value) {
                    onCollectClick()
                }
            }
            imgCollect.singleClick {
                onCollectClick()
            }
        }
        onActivityObserver()
    }

    fun onActivityObserver() {
        val unlock = mBinding?.viewpager2?.blockUpwardSwipeOnPage() // 禁止在第?页上滑
        observerBlur = Observer {
            if (it == -1 && type == "Swap") {
                unlock?.invoke()
                viewPagerAdapter.notifyDataSetChanged()
                playPosition()
            }
        }
        mModelActivity.indexBlur.observe(this, observerBlur)

        observerListBean = Observer { beans ->
            viewPagerAdapter.submitList(beans)
        }
        mModelActivity.mediaListBean.observe(this, observerListBean)

        observerBean = Observer {
            it.apply {
                mModelActivity.isBlurView.value = (isBlur && !GVM.INSTANT.isVip.value)
                //需要同步显示素材
                mBinding?.viewpager2?.currentItem = mModelActivity.curSelectedPosition
                playPosition()
            }
        }
        mModelActivity.mediaByBean.observe(this, observerBean)
    }

    fun replyZoom() {
        VibrationUtil.vibrateOnce(requireContext().applicationContext, 200)
        mModel.cacheView?.getAttacher()?.replyZoom()
        mModel.photoView?.attacher?.replyZoom()
    }

    fun onVideoSoundClick() {
        GVM.INSTANT.swapNeedMute.value = !GVM.INSTANT.swapNeedMute.value
        mModel.cacheView?.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
    }

    fun onToAct() {
        DisclaimerDialog().showIgnoreState(this)
    }

    fun onScore() {
        ScoreDialog(mModelActivity.mediaByBean.value?.getScoreFloat()).showIgnoreState(this)
    }

    fun onReport() {
        ReportDialog { black, content ->
            mModel.getReport(mModelActivity.mediaByBean.value?.id, content, black)
        }.showIgnoreState(this)
    }

    fun onCollectClick() {
        val collect = mModelActivity.isCollect.value
        val collectId = mModelActivity.collectStr
        val mediaId = mModelActivity.mediaByBean.value?.id ?: ""
        mModelActivity.isCollect.value = !mModelActivity.isCollect.value
        launchRequestOnIO({
            if (collect) {
                Repository.removeUserCollect(collectId)
            } else {
                Repository.saveCollect(mediaId, "media")
            }
        }) {
            onSuccess = {
                mModelActivity.collectStr = it.toIntOrZero().toString()
                mModelActivity.mediaListBean.value.find { it.id == mediaId }
                    ?.let { bean ->
                        bean.collectId = it.toIntOrZero().toString()
                    }
                if (!collect) toast(getStringX(R.string.collect_success))
            }
            onFailed = { _, _, errorMsg ->
                mModelActivity.isCollect.value = !collect
                toast(errorMsg)
            }
        }
    }


    fun playPosition() {
        CustomManager.clearAllVideo(ZoomVideo.Tag)
        val mBean =
            mModelActivity.mediaListBean.value.getOrNull(mModelActivity.curSelectedPosition % mModelActivity.mediaSize)
        if (mBean?.mediaType == "video" && type == "Swap") {
            mBinding?.viewpager2?.let {
                it.postDelayed({
                    val viewHolder = (it.getChildAt(0) as? RecyclerView)
                        ?.findViewHolderForAdapterPosition(mModelActivity.curSelectedPosition)
                    val player = viewPagerAdapter.getPlayerByViewHolder(viewHolder)
                    if (player != null) {
//                        if (mModelActivity.curSelectedPosition != 0) {
//                            mPreloadManager.removePreloadTask(
//                                mBean.videoUrl,
//                                mModelActivity.curSelectedPosition
//                            )
//                        }
//                        val url = mPreloadManager.getPlayUrl(mBean.videoUrl, requireContext())
                        player.getAttacher()?.replyZoom()
                        player.setUp(mBean.videoUrl, true, null,VideoHeaderManager.headers,"")
//                        player.setUp(url, true, "")
                        player.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
                        if (!mModelActivity.isBlurView.value) {//显示办了vip界面就不播放
                            player.startPlayLogic()
                        }
                        //广告播放中暂停视频
                        if (mModelActivity.showInterstitial) player.onVideoPause()
                        mModelActivity.showInterstitial = false
                        mModel.cacheView = player
                        mModel.cacheView?.getAttacher()?.setOnZoomListener { iszoom ->
                            if (iszoom) {
                                mBinding?.zoomView?.visibility = VISIBLE
                            } else {
                                mBinding?.zoomView?.visibility = GONE
                            }
                        }
                        mModel.cacheView?.getAttacher()?.setOnShockListener {
                            VibrationUtil.vibrateOnce(requireContext().applicationContext, 100)
                        }

                    }
                }, 200)
            }
        } else {
            mBinding?.viewpager2?.let {
                it.postDelayed({
                    val viewHolder = (it.getChildAt(0) as? RecyclerView)
                        ?.findViewHolderForAdapterPosition(mModelActivity.curSelectedPosition)
                    mModel.photoView = viewPagerAdapter.getPhotoByViewHolder(viewHolder)
                    mModel.photoView?.attacher?.replyZoom()
                    mModel.photoView?.attacher?.setOnZoomListener { iszoom ->
                        if (iszoom) {
                            mBinding?.zoomView?.visibility = VISIBLE
                        } else {
                            mBinding?.zoomView?.visibility = GONE
                        }
                    }
                    mModel.photoView?.attacher?.setOnShockListener {
                        VibrationUtil.vibrateOnce(requireContext().applicationContext, 100)
                    }
                }, 200)
            }
        }
    }

    private val onPageChangeCallback = object : OnPageChangeCallback() {

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val recyclerView = mBinding?.viewpager2?.getChildAt(0) as? RecyclerView ?: return
            if (position < mModelActivity.curSelectedPosition) {
                val prevHolder = recyclerView.findViewHolderForAdapterPosition(position)
                val player1 = viewPagerAdapter.getPlayerByViewHolder(prevHolder)
                mBinding?.viewpager2?.post {
                    player1?.showLayout()
                }
            }
            if (position == mModelActivity.curSelectedPosition) {
                val nextHolder = recyclerView.findViewHolderForAdapterPosition(position + 1)
                val player2 = viewPagerAdapter.getPlayerByViewHolder(nextHolder)
                mBinding?.viewpager2?.post {
                    player2?.showLayout()
                }
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position != mModelActivity.curSelectedPosition) {
                mModelActivity.curSelectedPosition = position
                GVM.INSTANT.videoOpenTime.value = System.currentTimeMillis()
                mModelActivity.mediaByBean.value =
                    mModelActivity.mediaListBean.value.getOrNull(position % mModelActivity.mediaSize)

                mBinding?.viewpager2?.apply {
                    val viewHolder = (getChildAt(0) as? RecyclerView)
                        ?.findViewHolderForAdapterPosition(mModelActivity.curSelectedPosition)
                    mModel.cacheView = viewPagerAdapter.getPlayerByViewHolder(viewHolder)
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
//            if (state==ViewPager2.SCROLL_STATE_IDLE){
//                // 滑动结束后确保恢复预加载
//                PreloadManager.getInstance(activity).mIsStartPreload = true
//            }
        }
    }

    fun ViewPager2.blockUpwardSwipeOnPage(): () -> Unit {
        var locked = true
        var startY = 0f

        val recyclerView = this.getChildAt(0) as RecyclerView

        // 监听当前页变化
        this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                locked = position == (mModelActivity.indexBlur.value ?: -1)
            }
        })

        val interceptor = object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (!locked) return false

                when (e.action) {
                    MotionEvent.ACTION_DOWN -> startY = e.y
                    MotionEvent.ACTION_MOVE -> {
                        val dy = e.y - startY
                        if (dy < -10) {
                            // 上滑，拦截
                            return true
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }

        recyclerView.addOnItemTouchListener(interceptor)

        // 提供解锁函数
        return {
            locked = false
        }
    }


    override fun onPause() {
        super.onPause()
        mModel.cacheView?.onVideoPause()
    }

    override fun onStart() {
        GVM.INSTANT.swapOpenTime.value = System.currentTimeMillis()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        mModel.cacheView?.onVideoResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //        mPreloadManager.removeAllPreloadTask()
        CustomManager.clearAllVideo(ZoomVideo.Tag)
        mModelActivity.indexBlur.removeObserver(observerBlur)
        mModelActivity.mediaListBean.removeObserver(observerListBean)
        mModelActivity.mediaByBean.removeObserver(observerBean)
        mModel.cacheView = null
        mBinding?.viewpager2?.adapter = null
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.avm, mModelActivity)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}