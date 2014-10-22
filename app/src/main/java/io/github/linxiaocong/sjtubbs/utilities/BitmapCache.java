package io.github.linxiaocong.sjtubbs.utilities;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache extends LruCache<String, Bitmap> {

    private static BitmapCache mInstance;

    private BitmapCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getByteCount() / 1024;
    }

    public synchronized static BitmapCache getInstance() {
        if (mInstance == null) {
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int maxSize = maxMemory / 8;
            mInstance = new BitmapCache(maxSize);
        }
        return mInstance;
    }
}
