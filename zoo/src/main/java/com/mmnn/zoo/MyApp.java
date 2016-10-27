package com.mmnn.zoo;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by dz on 2015/11/18.dz
 */
public class MyApp extends Application {

    private static MyApp sInstance;

    public static MyApp getInstance() {
        return sInstance;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCache(new LruMemoryCache(3 * 1024 * 1024))
                .memoryCacheSize(3 * 1024 * 1024)
//				.discCache(
//						new TotalSizeLimitedDiscCache(getImageCacheDir(context),
//								new Md5FileNameGenerator(), 8 * 1024 * 1024))
                .diskCache(new LimitedAgeDiskCache(getImageCacheDir(context), 60 * 60 * 24 * 7))
                .diskCacheSize(16 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .denyCacheImageMultipleSizesInMemory()
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .threadPoolSize(5)
                .build();
//				.writeDebugLogs().build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    private static File getImageCacheDir(Context context) {
        File file = new File(context.getCacheDir().getAbsolutePath() + "rcImageCache2");
        if (file.mkdirs()) {
            return file;
        } else {
            return context.getCacheDir();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initImageLoader(this);
    }
}
