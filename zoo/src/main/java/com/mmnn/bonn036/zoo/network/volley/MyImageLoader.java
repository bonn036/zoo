package com.mmnn.bonn036.zoo.network.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class MyImageLoader extends ImageLoader {

    private static final int MAX_SIZE = 5 * 1024 * 1024;

    private static MyImageLoader sIntance;
    private ImageLruCache mCache;

    private MyImageLoader(RequestQueue queue, ImageLruCache cache) {
        super(queue, cache);
        mCache = cache;
    }

    public static MyImageLoader getInstance(Context context) {
        if (sIntance == null) {
            synchronized (MyImageLoader.class) {
                if (sIntance == null) {
                    ImageLruCache cache = new ImageLruCache(MAX_SIZE);
//                    sIntance = new MyImageLoader(VolleyHelper.getRequestQueue(), cache);
                    sIntance = new MyImageLoader(Volley.newRequestQueue(context.getApplicationContext()), cache);
                }
            }
        }
        return sIntance;
    }

    public void clearCache() {
        mCache.evictAll();
    }

    @Override
    public ImageContainer get(String requestUrl, ImageListener imageListener, int maxWidth, int maxHeight) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }
        return super.get(requestUrl, imageListener, maxWidth, maxHeight);
    }

    public static class ImageLruCache extends LruCache<String, Bitmap> implements ImageCache {

        public ImageLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getByteCount();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            // Not cache Bitmap when its byte count greater than the size of
            // cache.
            if (bitmap.getByteCount() < MAX_SIZE) {
                put(url, bitmap);
            }
        }

    }
}