package io.github.linxiaocong.sjtubbs.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by linxiaocong on 2014/10/24.
 */
public class Misc {

    private static final String tag = "Misc";

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

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static Bitmap getScaledBitmapFromFile(Context context, File f, int scaledWidth) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            if (bitmap == null) {
                f.deleteOnExit();
                return null;
            }
            double scale = (double)scaledWidth / bitmap.getWidth();
            int height = (int) (bitmap.getHeight() * scale);
            bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, height, true);
            return bitmap;
        } catch (OutOfMemoryError err) {
            Toast.makeText(context, "Out of memory!", Toast.LENGTH_SHORT).show();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    public static Drawable getDrawableFromBitmap(Context context, Bitmap bitmap) {
        if (context == null) {
            return null;
        }
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }
}
