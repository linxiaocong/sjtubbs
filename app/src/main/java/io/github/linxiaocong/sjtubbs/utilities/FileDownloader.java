package io.github.linxiaocong.sjtubbs.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linxiaocong on 2014/10/26.
 */
public class FileDownloader<Token> extends HandlerThread {

    private static final String tag = "FileDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    public interface OnFileDownloadedListener<Token> {
        void onImageDownloaded(Token token, String filename);
    }

    private Handler mHandler;
    private Map<Token, String> mRequestMap =
            Collections.synchronizedMap(new HashMap<Token, String>());
    Handler mResponseHandler;
    OnFileDownloadedListener<Token> mOnFileDownloadedListener;

    private Context mContext;

    public FileDownloader(Context context, Handler responseHandler) {
        super(tag);
        mContext = context;
        mResponseHandler = responseHandler;
    }

    public void queueFile(Token token, String url) {
        Log.d(tag, "Got an URL: " + url);
        mRequestMap.put(token, url);
        if (mHandler != null) {
            mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
                    .sendToTarget();
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    public void setOnFileDownloadedListener(OnFileDownloadedListener<Token> listener) {
        mOnFileDownloadedListener = listener;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_DOWNLOAD:
                        Token token = (Token)msg.obj;
                        String url = mRequestMap.get(token);
                        if (url != null) {
                            Log.i(tag, "Got a request for url: " + url);
                            handleRequest(token);
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    private void handleRequest(final Token token) {
        try {
            final String url = mRequestMap.get(token);
            final String filename = url.substring(url.lastIndexOf('/') + 1);
            File file = new File(mContext.getCacheDir(), filename);
            Misc.savedToFile(url, file);
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(token) != url)
                        return;
                    mRequestMap.remove(token);
                    mOnFileDownloadedListener.onImageDownloaded(token, filename);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(tag, e.toString());
        }
    }
}
