package io.github.linxiaocong.sjtubbs.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class UrlImageGetter implements Html.ImageGetter {

    private static final String tag = "URLImageGetter";

    private Context mContext;
    private TextView mTextView;

    public UrlImageGetter(Context c, TextView textView) {
        mContext = c;
        mTextView = textView;
    }

    @Override
    public Drawable getDrawable(String url) {
        UrlDrawable urlDrawable = new UrlDrawable();
        String filename = url.substring(url.lastIndexOf('/') + 1);
        File f = new File(mContext.getCacheDir(), filename);
        if (f.exists()) {
            Log.d(tag, "image exists in cache dir: " + filename);
            Bitmap bitmap = Misc.getScaledBitmapFromFile(mContext, f,
                    OnImageDownloadedListener.BITMAP_WIDTH_THUMBNAIL);
            return Misc.getDrawableFromBitmap(mContext, bitmap);
        } else {
            (new FetchImageTask(urlDrawable)).execute(url);
        }
        return urlDrawable;
    }

    public class FetchImageTask extends AsyncTask<String, Void, Drawable> {

        UrlDrawable mUrlDrawable;

        public FetchImageTask(UrlDrawable d) {
            mUrlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            String filename = source.substring(source.lastIndexOf('/') + 1);
            File f = new File(mContext.getCacheDir(), filename);
            if (Misc.savedToFile(params[0], f)) {
                Bitmap bitmap = Misc.getScaledBitmapFromFile(mContext, f,
                        OnImageDownloadedListener.BITMAP_WIDTH_THUMBNAIL);
                return Misc.getDrawableFromBitmap(mContext, bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            mUrlDrawable.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
            mUrlDrawable.setDrawable(result);
            TextView textView = UrlImageGetter.this.mTextView;
            textView.setText(textView.getText());
        }
    }
}
