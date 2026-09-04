package com.flyjingfish.openimagefulllib;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyjingfish.openimagelib.photoview.PhotoView;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class GSYVideoOpenPlayer extends StandardGSYVideoPlayer {

    private String pageContextKey;
    private String uUKey;

    protected OpenImageGSYVideoHelper gsyVideoHelper;

    public int videoWidth = 0;
    public int videoHeight = 0;

    public GSYVideoOpenPlayer(Context context) {
        this(context, null);
    }

    public GSYVideoOpenPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private static class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN,
                     AudioManager.AUDIOFOCUS_LOSS,
                     AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
                     AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    }

    private final AudioManager.OnAudioFocusChangeListener EMPTY = new MyOnAudioFocusChangeListener();

    @Override
    protected void init(Context context) {
        super.init(context);
        onAudioFocusChangeListener = EMPTY;
    }


    void initAttrs(Context context, AttributeSet attrs) {
        pageContextKey = context.toString();
        uUKey = UUID.randomUUID().toString();
    }

    public String getVideoKey() {
        return pageContextKey + "$" + uUKey;
    }

    public OpenImageGSYVideoHelper playUrl(String videoUrl) {
        Map<String, String> mapHeaderData = new HashMap<>();
        mapHeaderData.put("referer", "yourdomain.com");
        OpenImageGSYVideoHelper.GSYVideoHelperBuilder builder = new OpenImageGSYVideoHelper.GSYVideoHelperBuilder();
        builder.setHideActionBar(true);
        builder.setHideStatusBar(true);
        builder.setHideKey(true);
        builder.setMapHeadData(mapHeaderData);
        builder.setUrl(videoUrl);
        builder.setEnlargeImageRes(getEnlargeImageRes());
        builder.setShrinkImageRes(getShrinkImageRes());
        builder.setAutoFullWithSize(true);
        builder.setShowFullAnimation(true);
        builder.setLockLand(true);
        builder.setReleaseWhenLossAudio(false);
        builder.setCacheWithPlay(true);
        builder.setLooping(isLooping());
        return playUrl(builder);
    }

    public OpenImageGSYVideoHelper playUrl(OpenImageGSYVideoHelper.GSYVideoHelperBuilder builder) {
        gsyVideoHelper = new OpenImageGSYVideoHelper(getContext(), this);
        gsyVideoHelper.setGsyVideoOptionBuilder(builder);

        if (getFullscreenButton() != null) {
            getFullscreenButton().setOnClickListener(v -> {
                if (mThumbImageView instanceof PhotoView) {
                    PhotoView photoImageView = (PhotoView) mThumbImageView;
                    photoImageView.getAttacher().setScreenOrientationChange(true);
                }
                gsyVideoHelper.doFullBtnLogic();
            });
        }
        gsyVideoHelper.readyPlay();
        return gsyVideoHelper;
    }

    public ViewGroup getTextureViewContainer() {
        return mTextureViewContainer;
    }

    public ImageView getBackButton() {
        return mBackButton;
    }

    public void goneAllWidget() {
        hideAllWidget();
    }

    public void showAllWidget() {
        if (mCurrentState == CURRENT_STATE_NORMAL) {
            changeUiToNormal();
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            changeUiToPauseShow();
        } else if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE) {
            changeUiToCompleteShow();
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            changeUiToError();
        }
    }

    @Override
    public void onVideoSizeChanged() {
        super.onVideoSizeChanged();
        for (OnVideoSizeChangedListener onVideoSizeChangedListener : onVideoSizeChangedListeners) {
//            onVideoSizeChangedListener.onVideoSizeChanged(getCurrentVideoWidth(),getCurrentVideoHeight());
            onVideoSizeChangedListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

    public interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(int width, int height);
    }

    private final List<OnVideoSizeChangedListener> onVideoSizeChangedListeners = new ArrayList<>();

    public void addOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        this.onVideoSizeChangedListeners.add(onVideoSizeChangedListener);
    }

    public void removeOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        this.onVideoSizeChangedListeners.remove(onVideoSizeChangedListener);
    }

    public boolean isShowingThumb() {
        return mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE;
    }
}
