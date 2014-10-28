package io.github.linxiaocong.sjtubbs.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by linxiaocong on 2014/10/26.
 */
public class OnImageDownloadedListener implements FileDownloader.OnFileDownloadedListener<ImageView> {

    public static int BITMAP_WIDTH_THUMBNAIL = 500;
    public static String THUMBNAIL_PREFIX = "_thumbnail";

    private Context mContext;
    private int mScaledWidth;

    public OnImageDownloadedListener(Context context, int scaledWidth) {
        mScaledWidth = scaledWidth;
        mContext = context;
    }

    @Override
    public void onImageDownloaded(ImageView imageView, String filename) {
        File file = new File(mContext.getCacheDir(), filename);
        Bitmap bitmap = Misc.getScaledBitmapFromFile(mContext, file, mScaledWidth);
        if (bitmap != null) {
            if (mScaledWidth == BITMAP_WIDTH_THUMBNAIL) {
                BitmapCache.getInstance().put(THUMBNAIL_PREFIX + file.getName(), bitmap);
            } else {
                BitmapCache.getInstance().put(file.getName(), bitmap);
            }
            imageView.setImageBitmap(bitmap);
        }
    }
}
