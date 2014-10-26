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

    public static int BITMAP_WIDTH_THUMBNAIL = 300;
    public static String THUMBNAIL_PREFIX = "_thumbnail";

    private Context mContext;
    private int mScaledWidth;

    public OnImageDownloadedListener(Context context, int scaledWidth) {
        mScaledWidth = scaledWidth;
        mContext = context;
    }

    @Override
    public void onImageDownloaded(ImageView imageView, File file) {
        Bitmap bitmap = Misc.getScaledBitmapFromFile(mContext, file, mScaledWidth);
        if (bitmap != null) {
            /*
            if (mScaledWidth == BITMAP_WIDTH_THUMBNAIL) {
                BitmapCache.getInstance().put(THUMBNAIL_PREFIX + file.getName(), bitmap);
            } else {
                BitmapCache.getInstance().put(file.getName(), bitmap);
            }
            */
            Drawable drawable = Misc.getDrawableFromBitmap(mContext, bitmap);
            imageView.setImageDrawable(drawable);
        }
    }
}
