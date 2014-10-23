package io.github.linxiaocong.sjtubbs.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class UrlImageGetter implements Html.ImageGetter {

    private static final String tag = "URLImageGetter";

    private Context mContext;
    private TextView mTextView;

    private static int sWidth = 0;

    public UrlImageGetter(Context c, TextView textView) {
        mContext = c;
        mTextView = textView;

        if (sWidth == 0) {
            WindowManager windowManager = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;
            sWidth = screenWidth;
        }
    }

    @Override
    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable();
        String filename = source.substring(source.lastIndexOf('/') + 1);
        File f = new File(mContext.getCacheDir(), filename);
        if (f.exists()) {
            Log.d(tag, "image exists in cache dir: " + source);
            return getDrawableFromFile(mContext, f);
        } else {
            (new FetchImageTask(urlDrawable)).execute(source);
        }
        return urlDrawable;
    }

    public static Drawable getDrawableFromFile(Context context, File f) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            if (bitmap == null) {
                f.deleteOnExit();
                return null;
            }
            //int width = Math.min(sWidth, bitmap.getWidth()) ;
            //int width = sWidth - 256;
            //double scale = (double) width / bitmap.getWidth();
            //int height = (int) (bitmap.getHeight() * scale);
            //bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        } catch (OutOfMemoryError err) {
            try {
                Toast.makeText(context, "Out of memory!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
            return null;
        }
    }

    public static boolean savedToFile(String source, File f) {
        try {
            URL url;
            InputStream in;
            OutputStream out;
            if (!source.startsWith("http")) {
                url = new URL(BBSUtils.BBS_INDEX + source);
            } else {
                url = new URL(source);
            }
            in = url.openStream();
            out = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            Log.d(tag, "finished downloading file from: " + source);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            if (savedToFile(params[0], f)) {
                return getDrawableFromFile(mContext, f);
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
