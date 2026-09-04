package com.flyjingfish.openimagelib;

import android.animation.ObjectAnimator;
import android.app.SharedElementCallback;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.flyjingfish.openimagelib.beans.CloseParams;
import com.flyjingfish.openimagelib.beans.OpenImageUrl;
import com.flyjingfish.openimagelib.enums.ImageShapeType;
import com.flyjingfish.openimagelib.enums.MediaType;
import com.flyjingfish.openimagelib.enums.MoreViewShowType;
import com.flyjingfish.openimagelib.enums.OpenImageOrientation;
import com.flyjingfish.openimagelib.listener.ImageFragmentCreate;
import com.flyjingfish.openimagelib.listener.OnLoadViewFinishListener;
import com.flyjingfish.openimagelib.listener.OnSelectMediaListener;
import com.flyjingfish.openimagelib.listener.UpperLayerFragmentCreate;
import com.flyjingfish.openimagelib.listener.VideoFragmentCreate;
import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.flyjingfish.openimagelib.utils.ScreenUtils;
import com.flyjingfish.openimagelib.widget.TouchCloseLayout;
import com.flyjingfish.shapeimageviewlib.ShapeImageView;

import java.util.List;
import java.util.Map;

public abstract class OpenImageActivity extends BaseActivity implements TouchCloseLayout.OnTouchCloseListener {

    protected static final int RequestPermissionsCode = 101;
    protected View vBg;
    protected FrameLayout flTouchView;
    protected ViewPager2 viewPager;
    protected TouchCloseLayout rootView;
    protected View contentView;
    private boolean isFirstBacked = false;
    private boolean isCallClosed;
    private List<OpenImageUrl> downloadList;
    private AppCompatImageView closeImageView;
    private long currentMediaId;

    /**
     * 获取 contentView ，用于调用{@link android.app.Activity#setContentView(View view)}
     *
     * @return {@link View}
     */
    public abstract View getContentView();

    /**
     * 设置显示背景的View
     *
     * @return {@link View}
     */
    public abstract View getBgView();

    /**
     * 触摸图片可下拉的布局，必须是 {@link TouchCloseLayout}
     *
     * @return {@link TouchCloseLayout}
     */
    public abstract TouchCloseLayout getTouchCloseLayout();

    /**
     * {@link ViewPager2}的外层包裹布局，类型{@link FrameLayout}
     *
     * @return {@link FrameLayout}
     */
    public abstract FrameLayout getViewPager2Container();

    /**
     * 承载图片或视频的{@link ViewPager2}
     *
     * @return {@link ViewPager2}
     */
    public abstract ViewPager2 getViewPager2();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        initPhotosViewModel();
        initRootView();
        boolean isFinish = parseIntent();

        super.onCreate(savedInstanceState);
        if (isFinish){
            return;
        }
        setContentView(contentView);

        initMoreView();
        initCloseView();
        initViewPager2();
        initTouchCloseLayout();
        if (isNoneClickView()) {
            onShareTransitionEnd();
        } else {
            setViewTransition();
            addTransitionListener();
        }
    }

    private void initRootView() {
        contentView = getContentView();
        vBg = getBgView();
        rootView = getTouchCloseLayout();
        flTouchView = getViewPager2Container();
        viewPager = getViewPager2();
    }

    private void initPhotosViewModel() {
        photosViewModel = new ViewModelProvider(this).get(PhotosViewModel.class);
        photosViewModel.closeViewLiveData.observe(this, integer -> close(false));
        photosViewModel.onAddOnSelectMediaListenerLiveData.observe(this, s -> {
            OnSelectMediaListener onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(s);
            if (onSelectMediaListener != null) {
                onSelectMediaListeners.add(onSelectMediaListener);
                if (isFirstBacked && showPosition < getOpenImageBeans().size()) {
                    OpenImageDetail imageDetail = getOpenImageBeans().get(showPosition);
                    onSelectMediaListener.onSelect(imageDetail.openImageUrl, showPosition);
                }
            }
            onSelectMediaListenerKeys.add(s);
        });

        photosViewModel.onRemoveOnSelectMediaListenerLiveData.observe(this, s -> {
            OnSelectMediaListener onSelectMediaListener = ImageLoadUtils.getInstance().getOnSelectMediaListener(s);
            if (onSelectMediaListener != null) {
                onSelectMediaListeners.remove(onSelectMediaListener);
            }
            ImageLoadUtils.getInstance().clearOnItemClickListener(s);
        });
    }

    private void initTouchCloseLayout() {
        boolean disEnableTouchClose = getIntent().getBooleanExtra(OpenParams.DISABLE_TOUCH_CLOSE, false);
        float touchScaleClose = getIntent().getFloatExtra(OpenParams.TOUCH_CLOSE_SCALE, .76f);
        rootView.setTouchCloseScale(touchScaleClose);
        rootView.setTouchView(flTouchView, vBg);
        rootView.setDisEnableTouchClose(disEnableTouchClose);
        if (touchCloseOrientation != null) {
            rootView.setOrientation(touchCloseOrientation);
        } else {
            rootView.setOrientation(orientation == OpenImageOrientation.VERTICAL ? OpenImageOrientation.HORIZONTAL : OpenImageOrientation.VERTICAL);
        }
        rootView.setViewPager2(viewPager);
        rootView.setOnTouchCloseListener(this);
    }
    /**
     * 开始拖动图片或视频
     */
    @Override
    public void onStartTouch() {
        if (onBackView != null) {
            onBackView.onStartTouchScale(showPosition);
        }
        touchHideMoreView();
    }

    /**
     * 停止拖动图片或视频
     */
    @Override
    public void onEndTouch() {
        if (onBackView != null) {
            onBackView.onEndTouchScale(showPosition);
        }
        showMoreView();
    }

    /**
     * 正在拖动图片或视频
     *
     * @param scale 图片或视频缩放比例
     */
    @Override
    public void onTouchScale(float scale) {
        photosViewModel.onTouchScaleLiveData.setValue(scale);
    }

    /**
     * 停止拖动图片或视频并关闭页面
     *
     * @param scale 图片或视频缩放比例
     */
    @Override
    public void onTouchClose(float scale) {
        touchCloseScale = scale;
        photosViewModel.onTouchCloseLiveData.setValue(scale);
        close(true);
    }

    private void initViewPager2() {
        int errorResId = getIntent().getIntExtra(OpenParams.ERROR_RES_ID, 0);
        float autoAspectRadio = getIntent().getFloatExtra(OpenParams.AUTO_ASPECT_RATIO, 0);
        boolean disableClickClose = getIntent().getBooleanExtra(OpenParams.DISABLE_CLICK_CLOSE, false);
        VideoFragmentCreate videoCreate = ImageLoadUtils.getInstance().getVideoFragmentCreate(videoFragmentCreateKey);
        ImageFragmentCreate imageCreate = ImageLoadUtils.getInstance().getImageFragmentCreate(imageFragmentCreateKey);
        if (videoCreate == null) {
            videoCreate = OpenImageConfig.getInstance().getVideoFragmentCreate();
        }
        if (imageCreate == null) {
            imageCreate = OpenImageConfig.getInstance().getImageFragmentCreate();
        }
        VideoFragmentCreate videoFragmentCreate = videoCreate;
        ImageFragmentCreate imageFragmentCreate = imageCreate;

        openImageAdapter = new OpenImageFragmentStateAdapter(this, viewPager) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                OpenImageDetail openImageBean = openImageBeans.get(position);
                MediaType mediaType = openImageBean.getType();
                BaseFragment fragment = null;
                if (mediaType == MediaType.VIDEO) {
                    if (videoFragmentCreate != null) {
                        fragment = videoFragmentCreate.createVideoFragment();
                        if (fragment == null) {
                            throw new IllegalArgumentException(videoFragmentCreate.getClass().getName() + "createVideoFragment");
                        }
                    } else {
                        throw new IllegalArgumentException("https://github.com/FlyJingFish/OpenImage/wiki");
                    }

                } else {
                    if (imageFragmentCreate != null) {
                        fragment = imageFragmentCreate.createImageFragment();
                    } else {
                        fragment = new ImageFragment();
                    }

                }
                Bundle bundle = new Bundle();
                String beanKey = contextKey + openImageBean;
                bundle.putString(OpenParams.IMAGE, beanKey);
                ImageLoadUtils.getInstance().setOpenImageDetail(beanKey, openImageBean);
                bundle.putInt(OpenParams.SHOW_POSITION, position);
                bundle.putInt(OpenParams.ERROR_RES_ID, errorResId);
                bundle.putInt(OpenParams.CLICK_POSITION, selectPos);
                if (srcScaleType != null) {
                    bundle.putInt(OpenParams.SRC_SCALE_TYPE, srcScaleType.ordinal());
                }
                bundle.putBoolean(OpenParams.DISABLE_CLICK_CLOSE, disableClickClose);
                bundle.putString(OpenParams.ON_ITEM_CLICK_KEY, onItemCLickKey);
                bundle.putString(OpenParams.ON_ITEM_LONG_CLICK_KEY, onItemLongCLickKey);
                bundle.putString(OpenParams.OPEN_COVER_DRAWABLE, openCoverKey);
                bundle.putFloat(OpenParams.AUTO_ASPECT_RATIO, autoAspectRadio);
                bundle.putBoolean(OpenParams.NONE_CLICK_VIEW, isNoneClickView());
                bundle.putInt(OpenParams.PRELOAD_COUNT, preloadCount);
                bundle.putBoolean(OpenParams.LAZY_PRELOAD, lazyPreload);
                bundle.putBoolean(OpenParams.BOTH_LOAD_COVER, bothLoadCover);
                fragment.setArguments(bundle);
                return fragment;
            }
        };
        openImageAdapter.setWechatExitFillInEffect(wechatExitFillInEffect);
        openImageAdapter.setNewData(getOpenImageBeans());
        viewPager.setAdapter(openImageAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {


            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                showPosition = position;
                if (lazyPreload && viewPager.getOffscreenPageLimit() != preloadCount){
                    viewPager.setOffscreenPageLimit(preloadCount);
                }
                setIndicatorPosition(position, getOpenImageBeans().size());
                showMoreView();
                if (isFirstBacked && onBackView != null) {
                    onBackView.onScrollPos(getOpenImageBeans().get(showPosition).viewPosition);
                }
                if (onSelectMediaListener != null) {
                    onSelectMediaListener.onSelect(getOpenImageBeans().get(showPosition).openImageUrl, showPosition);
                }
                for (OnSelectMediaListener selectMediaListener : onSelectMediaListeners) {
                    selectMediaListener.onSelect(getOpenImageBeans().get(showPosition).openImageUrl, showPosition);
                }
                isFirstBacked = true;
                if (!viewPager.isUserInputEnabled()){
                    viewPager.setUserInputEnabled(true);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        openImageAdapter.setOnUpdateIndicator(() -> {
            initIndicator();
            setIndicatorPosition(showPosition, getOpenImageBeans().size());
        });
        if (!lazyPreload){
            viewPager.setOffscreenPageLimit(preloadCount);
        }
        viewPager.setCurrentItem(selectPos, false);
    }

    private void setViewTransition() {
        ViewCompat.setTransitionName(viewPager, OpenParams.SHARE_VIEW + selectPos);
    }

    /**
     * 初始化更多View
     */
    protected void initMoreView() {
        if (moreViewKey != null) {
            List<MoreViewOption> viewOptions = ImageLoadUtils.getInstance().getMoreViewOption(moreViewKey);
            for (MoreViewOption moreViewOption : viewOptions) {
                View view = null;
                if (moreViewOption != null && moreViewOption.getViewType() == MoreViewOption.LAYOUT_RES) {
                    view = LayoutInflater.from(this).inflate(moreViewOption.getLayoutRes(), null, false);
                }
                if (moreViewOption != null && moreViewOption.getViewType() == MoreViewOption.LAYOUT_VIEW) {
                    view = moreViewOption.getView();
                }

                if (view != null) {
                    if (moreViewOption.isFollowTouch()) {
                        flTouchView.addView(view, moreViewOption.getLayoutParams());
                    } else {
                        rootView.addView(view, moreViewOption.getLayoutParams());
                    }
                    OnLoadViewFinishListener onLoadViewFinishListener = moreViewOption.getOnLoadViewFinishListener();
                    if (onLoadViewFinishListener != null) {
                        onLoadViewFinishListener.onLoadViewFinish(view);
                    }
                    moreViewOption.setView(view);
                    view.setVisibility(View.GONE);
                }
            }
            moreViewOptions.addAll(viewOptions);
        }
    }

    /**
     * 展示添加的更多View时回调此方法，调用时机是切换图片或停止拖动图片时
     */
    protected void showMoreView() {
        if (showPosition >= getOpenImageBeans().size()){
            return;
        }
        OpenImageDetail openImageDetail = getOpenImageBeans().get(showPosition);
        MediaType mediaType = openImageDetail.getType();
        if (moreViewOptions.size() > 0) {
            for (MoreViewOption moreViewOption : moreViewOptions) {
                MoreViewShowType showType = moreViewOption.getMoreViewShowType();
                if (mediaType == MediaType.IMAGE && (showType == MoreViewShowType.IMAGE || showType == MoreViewShowType.BOTH)) {
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                } else if (mediaType == MediaType.VIDEO && (showType == MoreViewShowType.VIDEO || showType == MoreViewShowType.BOTH)) {
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                } else {
                    moreViewOption.getView().setVisibility(View.GONE);
                }
            }
        }

        View upperView;
        if (upLayerFragment != null && (upperView = upLayerFragment.getView()) != null && upperLayerOption != null && upperLayerOption.isTouchingHide()) {
            upperView.setVisibility(View.VISIBLE);
        }
        if (closeImageView != null) {
            if ((mediaType == MediaType.IMAGE && (closeShowType == MoreViewShowType.IMAGE || closeShowType == MoreViewShowType.BOTH))||(mediaType == MediaType.VIDEO && (closeShowType == MoreViewShowType.VIDEO || closeShowType == MoreViewShowType.BOTH))) {
                closeImageView.setVisibility(View.VISIBLE);
            } else {
                closeImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 隐藏添加的更多View时回调此方法，调用时机是切换图片或开始拖动图片时
     */
    protected void touchHideMoreView() {
        if (showPosition >= getOpenImageBeans().size()){
            return;
        }
        OpenImageDetail openImageDetail = getOpenImageBeans().get(showPosition);
        MediaType mediaType = openImageDetail.getType();
        if (moreViewOptions.size() > 0) {
            for (MoreViewOption moreViewOption : moreViewOptions) {
                MoreViewShowType showType = moreViewOption.getMoreViewShowType();
                if (mediaType == MediaType.IMAGE && (showType == MoreViewShowType.IMAGE || showType == MoreViewShowType.BOTH) && !moreViewOption.isFollowTouch()) {
                    moreViewOption.getView().setVisibility(View.GONE);
                } else if (mediaType == MediaType.VIDEO && (showType == MoreViewShowType.VIDEO || showType == MoreViewShowType.BOTH) && !moreViewOption.isFollowTouch()) {
                    moreViewOption.getView().setVisibility(View.GONE);
                } else {
                    moreViewOption.getView().setVisibility(View.VISIBLE);
                }
            }
        }
        View upperView;
        if (upLayerFragment != null && (upperView = upLayerFragment.getView()) != null && upperLayerOption != null && upperLayerOption.isTouchingHide()) {
            upperView.setVisibility(View.GONE);
        }
        if (closeImageView != null && closeTouchingHide) {
            closeImageView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置显示指示器位置
     *
     * @param showPosition 当前显示的位置
     * @param total        图片或视频总数
     */
    protected void setIndicatorPosition(int showPosition, int total) {
        selectPos = showPosition;
        openActivityData.selectPos = selectPos;
        mHandler.post(() -> {
            if (indicatorType == INDICATOR_IMAGE) {//图片样式
                if (imageIndicatorAdapter != null) {
                    imageIndicatorAdapter.setTotal(total);
                    imageIndicatorAdapter.setSelectPosition(showPosition);
                    imageIndicatorLayoutManager.scrollToPosition(showPosition);
                }
            } else if (indicatorType == INDICATOR_TEXT) {
            }
        });
    }

    private void initIndicator(){
        if (themeRes != 0) {
            if (indicatorType < 2 && getOpenImageBeans().size() > 1) {
                if (indicatorType == INDICATOR_IMAGE) {//图片样式
                    float interval = AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_image_interval, -1);
                    int imageRes = AttrsUtils.getTypeValueResourceId(this, themeRes, R.attr.openImage_indicator_imageRes);
                    if (interval == -1) {
                        interval = ScreenUtils.dp2px(this, 4);
                    }
                    if (imageRes == 0) {
                        imageRes = R.drawable.open_image_indicator_image;
                    }
                    RecyclerView recyclerView = new RecyclerView(this);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    setIndicatorLayoutParams(layoutParams, themeRes);
                    rootView.addView(recyclerView, layoutParams);

                    OpenImageOrientation realOrientation = OpenImageOrientation.getOrientation(AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_indicator_image_orientation));
                    imageIndicatorLayoutManager = new LinearLayoutManager(this, realOrientation == OpenImageOrientation.HORIZONTAL ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(imageIndicatorLayoutManager);
                    imageIndicatorAdapter = new ImageIndicatorAdapter(getOpenImageBeans().size(), interval, imageRes, realOrientation);
                    recyclerView.setAdapter(imageIndicatorAdapter);
                }
            }
        }
    }

    private void initCloseView() {
        boolean closeShow = getIntent().getBooleanExtra(OpenParams.CLOSE_SHOW, false);
        if (closeShow){
            closeParamsKey = getIntent().getStringExtra(OpenParams.CLOSE_PARAMS);
            CloseParams closeParams = null;
            if (!TextUtils.isEmpty(closeParamsKey)) {
                closeParams = ImageLoadUtils.getInstance().getCloseParams(closeParamsKey);
                if (closeParams != null && (closeShowType = closeParams.getMoreViewShowType()) == null){
                    closeShowType = MoreViewShowType.IMAGE;
                }
            }
            FrameLayout.LayoutParams downloadLayoutParams;
            if (closeParams == null || closeParams.getCloseLayoutParams() == null) {
                downloadLayoutParams = new FrameLayout.LayoutParams((int) ScreenUtils.dp2px(this, 24), (int) ScreenUtils.dp2px(this, 24));
                downloadLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
                downloadLayoutParams.setMarginStart((int) ScreenUtils.dp2px(this, 14));
                downloadLayoutParams.bottomMargin = (int) ScreenUtils.dp2px(this, 8);
            } else {
                downloadLayoutParams = closeParams.getCloseLayoutParams();
            }
            closeImageView = new AppCompatImageView(this);
            int downloadSrc = R.drawable.ic_open_image_close;
            if (closeParams != null) {
                downloadSrc = closeParams.getCloseSrc();
                closeTouchingHide = closeParams.isTouchingHide();
            }
            closeImageView.setImageResource(downloadSrc);
            rootView.addView(closeImageView, downloadLayoutParams);
            closeImageView.setOnClickListener(v -> close(false));
        }

    }

    private void setIndicatorLayoutParams(FrameLayout.LayoutParams layoutParams, int themeRes) {
        int gravity = AttrsUtils.getTypeValueInt(this, themeRes, R.attr.openImage_indicator_gravity);
        int topMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginTop);
        int bottomMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginBottom);
        int leftMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginLeft);
        int rightMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginRight);
        int startMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginStart);
        int endMargin = (int) AttrsUtils.getTypeValueDimension(this, themeRes, R.attr.openImage_indicator_marginEnd);
        layoutParams.bottomMargin = bottomMargin;
        layoutParams.topMargin = topMargin + ScreenUtils.getStatusBarHeight(this);
        layoutParams.leftMargin = leftMargin;
        layoutParams.rightMargin = rightMargin;
        layoutParams.setMarginStart(startMargin);
        layoutParams.setMarginEnd(endMargin);
        if (gravity == 0) {
            if (orientation == OpenImageOrientation.VERTICAL) {
                layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
                layoutParams.setMarginEnd(endMargin == 0 ? (int) ScreenUtils.dp2px(this, 14) : endMargin);
            } else {
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                layoutParams.bottomMargin = bottomMargin == 0 ? (int) ScreenUtils.dp2px(this, 14) : bottomMargin;
            }
        } else {
            layoutParams.gravity = gravity;
        }
    }

    private void addTransitionListener() {
        Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            long timeMs = getIntent().getLongExtra(OpenParams.OPEN_ANIM_TIME_MS, 0);
            if (timeMs != 0) {
                transition.setDuration(timeMs);
            }
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);

                    onShareTransitionEnd();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    /**
     * 页面启动动画结束时回调此方法
     */
    protected void onShareTransitionEnd() {
        photosViewModel.transitionEndLiveData.setValue(true);
        mHandler.post(() -> {

            if (upperLayerOption != null && OpenImageActivity.this.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED) {
                UpperLayerFragmentCreate upperLayerCreate = upperLayerOption.getUpperLayerFragmentCreate();
                if (upperLayerCreate != null) {
                    upLayerFragment = upperLayerCreate.createLayerFragment();
                }
                if (upLayerFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    FrameLayout frameLayout = new FrameLayout(OpenImageActivity.this);
                    frameLayout.setId(R.id.open_image_upper_layer_container);
                    Bundle upperLayerBundle = getIntent().getBundleExtra(OpenParams.UPPER_LAYER_BUNDLE);
                    if (upperLayerBundle != null) {
                        upLayerFragment.setArguments(upperLayerBundle);
                    }
                    if (upperLayerOption.isFollowTouch()) {
                        flTouchView.addView(frameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else {
                        rootView.addView(frameLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    transaction.replace(R.id.open_image_upper_layer_container, upLayerFragment).commitAllowingStateLoss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showPosition = 0;
        mHandler.removeCallbacksAndMessages(null);
        ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectKey);
        ImageLoadUtils.getInstance().clearPermissionsInterceptListener(onPermissionKey);
        ImageLoadUtils.getInstance().clearCoverDrawable(openCoverKey);
        ImageLoadUtils.getInstance().clearCoverFilePath(openCoverKey);
        ImageLoadUtils.getInstance().clearSmallCoverDrawable(openCoverKey);
        ImageLoadUtils.getInstance().clearPageTransformers(pageTransformersKey);
        ImageLoadUtils.getInstance().clearOnItemClickListener(onItemCLickKey);
        ImageLoadUtils.getInstance().clearOnItemLongClickListener(onItemLongCLickKey);
        ImageLoadUtils.getInstance().clearMoreViewOption(moreViewKey);
        ImageLoadUtils.getInstance().clearOnBackView(onBackViewKey);
        ImageLoadUtils.getInstance().clearImageFragmentCreate(imageFragmentCreateKey);
        ImageLoadUtils.getInstance().clearVideoFragmentCreate(videoFragmentCreateKey);
        ImageLoadUtils.getInstance().clearUpperLayerFragmentCreate(upperLayerFragmentCreateKey);
        if (wechatEffectAnim != null) {
            wechatEffectAnim.cancel();
        }
        for (String onSelectMediaListenerKey : onSelectMediaListenerKeys) {
            ImageLoadUtils.getInstance().clearOnSelectMediaListener(onSelectMediaListenerKey);
        }
        onSelectMediaListenerKeys.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canFragmentBack()) {
                close(false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setExitView() {
        BackViewType backViewType = BackViewType.NO_SHARE;
        ImageView backView = null;
        ExitOnBackView.ShareExitViewBean shareExitViewBean;
        if (onBackView != null && (shareExitViewBean = onBackView.onBack(showPosition)) != null) {
            backViewType = shareExitViewBean.backViewType;
            backView = shareExitViewBean.shareExitView;
        }
        if (backViewType == BackViewType.NO_SHARE) {
            ViewCompat.setTransitionName(viewPager, "");
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    names.clear();
                    sharedElements.clear();
                }
            });
            return;
        }

        View shareView = getCoverView();
        final ImageView exitView = backView;
        if (shareView != null) {
            ViewCompat.setTransitionName(viewPager, "");
            ViewCompat.setTransitionName(shareView, OpenParams.SHARE_VIEW + showPosition);
            if (backViewType == BackViewType.SHARE_WECHAT) {
                addWechatEffect(shareView);
            }
            Drawable drawable;
            if ((shareView instanceof PhotoView) && (((PhotoView) shareView).getSrcScaleType() == ShapeImageView.ShapeScaleType.CENTER
                    || ((PhotoView) shareView).getSrcScaleType() == ShapeImageView.ShapeScaleType.CENTER_INSIDE)
                    && exitView != null && (drawable = exitView.getDrawable()) != null) {
                ((PhotoView) shareView).setExitDrawableWidthHeight(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            }
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    int startWidth = exitView.getWidth();
                    int startHeight = exitView.getHeight();
                    if ((exitView instanceof ShapeImageView) && (shareView instanceof PhotoView)) {
                        ShapeImageView.ShapeScaleType shapeScaleType = ((ShapeImageView) exitView).getShapeScaleType();
                        ((PhotoView) shareView).setSrcScaleType(shapeScaleType);
                        if (shapeScaleType == ShapeImageView.ShapeScaleType.AUTO_START_CENTER_CROP || srcScaleType == ShapeImageView.ShapeScaleType.AUTO_END_CENTER_CROP) {
                            ((PhotoView) shareView).setAutoCropHeightWidthRatio(((ShapeImageView) exitView).getAutoCropHeightWidthRatio());
                        }
                        if (((ShapeImageView) exitView).getShapeType() == ShapeImageView.ShapeType.OVAL && startWidth == startHeight) {
                            ((PhotoView) shareView).setShapeType(ShapeImageView.ShapeType.RECTANGLE);
                            ((PhotoView) shareView).setRadius(
                                    (int) (startWidth / 2 / touchCloseScale),
                                    (int) (startWidth / 2 / touchCloseScale),
                                    (int) (startWidth / 2 / touchCloseScale),
                                    (int) (startWidth / 2 / touchCloseScale));
                            ((PhotoView) shareView).setRelativeRadius(
                                    (int) (startWidth / 2 / touchCloseScale),
                                    (int) (startWidth / 2 / touchCloseScale),
                                    (int) (startWidth / 2 / touchCloseScale),
                                    (int) (startWidth / 2 / touchCloseScale));
                        } else {
                            ((PhotoView) shareView).setShapeType(((ShapeImageView) exitView).getShapeType());
                            ((PhotoView) shareView).setRadius((int) ((int) ((ShapeImageView) exitView).getLeftTopRadius() / touchCloseScale),
                                    (int) ((int) ((ShapeImageView) exitView).getRightTopRadius() / touchCloseScale),
                                    (int) ((int) ((ShapeImageView) exitView).getRightBottomRadius() / touchCloseScale),
                                    (int) ((int) ((ShapeImageView) exitView).getLeftBottomRadius() / touchCloseScale));
                            ((PhotoView) shareView).setRelativeRadius((int) ((int) ((ShapeImageView) exitView).getStartTopRadius() / touchCloseScale),
                                    (int) ((int) ((ShapeImageView) exitView).getEndTopRadius() / touchCloseScale),
                                    (int) ((int) ((ShapeImageView) exitView).getEndBottomRadius() / touchCloseScale),
                                    (int) ((int) ((ShapeImageView) exitView).getStartBottomRadius() / touchCloseScale));
                        }
                    } else if (imageShapeParams != null && (shareView instanceof PhotoView)) {
                        if (imageShapeParams.shapeType == ImageShapeType.OVAL) {
                            if (startWidth == startHeight) {
                                ((PhotoView) shareView).setShapeType(ShapeImageView.ShapeType.RECTANGLE);
                                ((PhotoView) shareView).setRadius(
                                        (int) (startWidth / 2 / touchCloseScale),
                                        (int) (startWidth / 2 / touchCloseScale),
                                        (int) (startWidth / 2 / touchCloseScale),
                                        (int) (startWidth / 2 / touchCloseScale));
                            } else {
                                ((PhotoView) shareView).setShapeType(ShapeImageView.ShapeType.OVAL);
                            }
                        } else if (imageShapeParams.rectangleConnerRadius != null) {
                            ((PhotoView) shareView).setShapeType(ShapeImageView.ShapeType.RECTANGLE);
                            ((PhotoView) shareView).setRadius((int) (imageShapeParams.rectangleConnerRadius.leftTopRadius / touchCloseScale),
                                    (int) (imageShapeParams.rectangleConnerRadius.rightTopRadius / touchCloseScale),
                                    (int) (imageShapeParams.rectangleConnerRadius.rightBottomRadius / touchCloseScale),
                                    (int) (imageShapeParams.rectangleConnerRadius.leftBottomRadius / touchCloseScale));
                        }
                    }
                    if (shareView instanceof PhotoView) {
                        ((PhotoView) shareView).setStartWidth(startWidth);
                        ((PhotoView) shareView).setStartHeight(startHeight);
                    }
                    sharedElements.put(OpenParams.SHARE_VIEW + showPosition, shareView);
                }
            });
        } else {
            if (backViewType == BackViewType.SHARE_WECHAT) {
                addWechatEffect(viewPager);
            }
            ViewCompat.setTransitionName(viewPager, OpenParams.SHARE_VIEW + showPosition);
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    super.onMapSharedElements(names, sharedElements);
                    if (names.size() == 0) {
                        return;
                    }
                    sharedElements.put(OpenParams.SHARE_VIEW + showPosition, viewPager);
                }
            });
        }


    }

    private void addWechatEffect(View shareView) {
        Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            wechatEffectAnim = ObjectAnimator.ofFloat(shareView, "alpha", 1f, 0f);
            wechatEffectAnim.setStartDelay(WECHAT_DURATION - WECHAT_DURATION_END_ALPHA);
            wechatEffectAnim.setDuration(WECHAT_DURATION_END_ALPHA);
            transition.setDuration(WECHAT_DURATION);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    wechatEffectAnim.start();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    wechatEffectAnim.cancel();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                    wechatEffectAnim.cancel();
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    wechatEffectAnim.pause();
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    wechatEffectAnim.resume();
                }
            });
        }
    }

    /**
     * 关闭页面
     *
     * @param isTouchClose 是否是拖动关闭
     */
    protected void close(boolean isTouchClose) {
        if (isNoneClickView()) {
            if (onBackView != null) {
                onBackView.onBack(showPosition);
            }
            finishAfterTransition();
            return;
        }
        if (isCallClosed) {
            return;
        }
        if (onBackView != null) {
            onBackView.onTouchClose(isTouchClose);
        }
        touchHideMoreView();
        setExitView();
        photosViewModel.onCanLayoutLiveData.setValue(false);
        if (!isTouchClose && fontStyle == FontStyle.FULL_SCREEN) {
            mHandler.postDelayed(this::finishAfterTransition, 100);
        } else {
            finishAfterTransition();
        }
        isCallClosed = true;
    }

    private View getCoverView() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BaseFragment) {
            BaseFragment baseFragment = (BaseFragment) fragment;
            return baseFragment.getExitImageView();
        }
        return null;
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag("f" + openImageAdapter.getItemId(showPosition));
    }

    protected boolean canFragmentBack() {
        boolean canLayerBack = true;
        boolean canImageBack = true;
        //首先判断最上层
        if (upLayerFragment != null) {
            canLayerBack = upLayerFragment.onKeyBackDown();
        }
        //其次判断下层图片页面
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BaseInnerFragment) {
            BaseInnerFragment baseFragment = (BaseInnerFragment) fragment;
            canImageBack = baseFragment.onKeyBackDown();
        }
        return canLayerBack && canImageBack;
    }

}
