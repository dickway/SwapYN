package com.face.video.cache;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.StorageUtils;
import com.danikula.videocache.file.FileNameGenerator;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.face.ui.App;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 抖音预加载工具，使用AndroidVideoCache实现
 */
public class PreloadManager {

    private static PreloadManager sPreloadManager;

    /**
     * 单线程池，按照添加顺序依次执行{@link PreloadTask}
     */
//    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private final ExecutorService mExecutorService = Executors.newCachedThreadPool();
    /**
     * 保存正在预加载的{@link PreloadTask}
     */
    private final LinkedHashMap<String, PreloadTask> mPreloadTasks = new LinkedHashMap<>();

    /**
     * 标识是否需要预加载
     */
    private boolean mIsStartPreload = true;

    private final HttpProxyCacheServer mHttpProxyCacheServer;

    /**
     * 预加载的大小，每个视频预加载1M，这个参数可根据实际情况调整
     */
    public static final int PRELOAD_LENGTH = 2 * 1024 * 1024;

    private PreloadManager(Context context) {
        mHttpProxyCacheServer = new ProxyCacheManager().newProxy(context);
    }

    public static PreloadManager getInstance() {
        if (sPreloadManager == null) {
            synchronized (PreloadManager.class) {
                if (sPreloadManager == null) {
                    sPreloadManager = new PreloadManager(App.getINSTANCE().getApplicationContext());
                }
            }
        }
        return sPreloadManager;
    }

    /**
     * 开始预加载
     *
     * @param rawUrl 原始视频地址
     */
    public void addPreloadTask(String rawUrl, int position, Context context) {
        if (isPreloaded(rawUrl, context)) return;
        PreloadTask task = new PreloadTask();
        task.mRawUrl = rawUrl;
        task.mPosition = position;
        task.mCacheServer = mHttpProxyCacheServer;
        LogUtils.d("addPreloadTask: %s",position );
        mPreloadTasks.put(rawUrl, task);
        if (mIsStartPreload) {
            //开始预加载
            LogUtils.d("开始预加载");
            task.executeOn(mExecutorService);
        }
    }

    /**
     * 判断该播放地址是否已经预加载
     */
    boolean isPreloaded(String rawUrl, Context context) {
        //先判断是否有缓存文件，如果已经存在缓存文件，并且其大小大于1KB，则表示已经预加载完成了

        FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
        String name = md5FileNameGenerator.generate(rawUrl);
//        File cacheFile = mHttpProxyCacheServer.getCacheFile(rawUrl);
        File cacheFile = new File(StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name);
//        LogUtils.e("cacheFile:"+cacheFile);
        if (cacheFile.exists()) {
            if (cacheFile.length() >= 1024*1024*1) {
                return true;
            } else {
                //这种情况一般是缓存出错，把缓存删掉，重新缓存
                cacheFile.delete();
                return false;
            }
        }
        //再判断是否有临时缓存文件，如果已经存在临时缓存文件，并且临时缓存文件超过了预加载大小，则表示已经预加载完成了
        String pathTmp = StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name + ".download";
//        LogUtils.e("pathTmp:"+pathTmp);
//        File tempCacheFile = mHttpProxyCacheServer.getTempCacheFile(rawUrl);
        File tempCacheFile = new File(pathTmp);
        if (tempCacheFile.exists()) {
            return tempCacheFile.length() >= PRELOAD_LENGTH;
        }

        return false;
    }

    public void deleteUrl(String rawUrl, Context context){
        //先判断是否有缓存文件，如果已经存在缓存文件，并且其大小大于1KB，则表示已经预加载完成了

        FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
        String name = md5FileNameGenerator.generate(rawUrl);
//        File cacheFile = mHttpProxyCacheServer.getCacheFile(rawUrl);
        File cacheFile = new File(StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name);
//        LogUtils.e("cacheFile:"+cacheFile);
        if (cacheFile.exists()) {
                //这种情况一般是缓存出错，把缓存删掉，重新缓存
                cacheFile.delete();
        }
        //再判断是否有临时缓存文件，如果已经存在临时缓存文件，并且临时缓存文件超过了预加载大小，则表示已经预加载完成了
        String pathTmp = StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name + ".download";
//        LogUtils.e("pathTmp:"+pathTmp);
//        File tempCacheFile = mHttpProxyCacheServer.getTempCacheFile(rawUrl);
        File tempCacheFile = new File(pathTmp);
        if (tempCacheFile.exists()) {
            tempCacheFile.delete();
        }
    }


    /**
     * 暂停预加载
     * 根据是否反向滑动取消在position之下或之上的PreloadTask
     *
     * @param position        当前滑到的位置
     * @param isReverseScroll 列表是否反向滑动
     */
    public void pausePreload(int position, boolean isReverseScroll) {
//        Log.e("TAG", "pausePreload：" + position + " isReverseScroll: " + isReverseScroll);
        mIsStartPreload = false;
        for (Map.Entry<String, PreloadTask> next : mPreloadTasks.entrySet()) {
            PreloadTask task = next.getValue();
            if (isReverseScroll) {
                if (task.mPosition >= position) {
                    task.cancel();
                }
            } else {
                if (task.mPosition <= position) {
                    task.cancel();
                }
            }
        }
    }

    /**
     * 恢复预加载
     * 根据是否反向滑动开始在position之下或之上的PreloadTask
     *
     * @param position        当前滑到的位置
     * @param isReverseScroll 列表是否反向滑动
     */
    public void resumePreload(int position, boolean isReverseScroll, Context context) {
//        Log.e("TAG", "resumePreload：" + position + " isReverseScroll: " + isReverseScroll);
        mIsStartPreload = true;
        for (Map.Entry<String, PreloadTask> next : mPreloadTasks.entrySet()) {
            PreloadTask task = next.getValue();
            if (isReverseScroll) {
                if (task.mPosition < position) {
                    if (!isPreloaded(task.mRawUrl, context)) {
                        task.executeOn(mExecutorService);
                    }
                }
            } else {
                if (task.mPosition > position) {
                    if (!isPreloaded(task.mRawUrl, context)) {
                        task.executeOn(mExecutorService);
                    }
                }
            }
        }
    }

    /**
     * 通过原始地址取消预加载
     *
     * @param rawUrl 原始地址
     */
    public void removePreloadTask(String rawUrl,int po) {
        PreloadTask task = mPreloadTasks.get(rawUrl);
        if (task != null) {
            task.cancel();
            LogUtils.d("删除预加载: %s",po);
            mPreloadTasks.remove(rawUrl);
        }
    }

    /**
     * 取消所有的预加载
     */
    public void removeAllPreloadTask() {
        Iterator<Map.Entry<String, PreloadTask>> iterator = mPreloadTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PreloadTask> next = iterator.next();
            PreloadTask task = next.getValue();
            task.cancel();
            iterator.remove();
        }
    }

    /**
     * 获取播放地址
     */
//    public String getPlayUrl(String rawUrl, Context context) {
//        PreloadTask task = mPreloadTasks.get(rawUrl);
//        if (task != null) {
//            task.cancel();
//        }
//        LogUtils.e("是否存在缓存：%s",  GSYVideoManager.instance().cachePreview(context,null,rawUrl));
//        if (isPreloaded(rawUrl, context)) {
//            return mHttpProxyCacheServer.getProxyUrl(rawUrl);
//        } else {
//            return rawUrl;
//        }
//    }

    public String getPlayUrl(String rawUrl, Context context) {
        PreloadTask task = mPreloadTasks.get(rawUrl);
        if (task != null) {
            task.cancel(); // 取消预加载
        }

        FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
        String name = md5FileNameGenerator.generate(rawUrl);
        File cacheFile = new File(StorageUtils.getIndividualCacheDirectory
                (context.getApplicationContext()).getAbsolutePath()
                + File.separator + name);

        if (cacheFile.exists() && cacheFile.length() > 1.5 * 1024 * 1024) {
            return mHttpProxyCacheServer.getProxyUrl(rawUrl); // ✔️ 完整缓存
        }

        // ❌ 其他情况（临时缓存或缓存异常）都走原始地址
        return rawUrl;
    }

    public boolean isCheckError(String rawUrl){
        if (PreloadTask.blackList.contains(rawUrl)){
            LogUtils.d("预加载失败，进入小黑屋");
            return true;
        }else {
            return false;
        }
    }
}
