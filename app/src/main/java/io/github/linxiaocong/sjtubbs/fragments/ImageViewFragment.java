package io.github.linxiaocong.sjtubbs.fragments;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.utilities.FileDownloader;
import io.github.linxiaocong.sjtubbs.utilities.Misc;
import io.github.linxiaocong.sjtubbs.utilities.OnImageDownloadedListener;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by linxiaocong on 2014/10/25.
 */
public class ImageViewFragment extends Fragment {

    public static final String EXTRA_PICTURE_URL = "extra_pictureUrl";

    private String mPictureUrl;
    private String mFilename;
    private FileDownloader<ImageView> mFileDownloader;
    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    public static ImageViewFragment newInstance(String pictureUrl) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PICTURE_URL, pictureUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPictureUrl = getArguments().getString(EXTRA_PICTURE_URL);
        mFilename = mPictureUrl.substring(mPictureUrl.lastIndexOf('/') + 1);
        mFileDownloader = new FileDownloader<ImageView>(getActivity(), new Handler());
        mFileDownloader.setOnFileDownloadedListener(
                new OnImageDownloadedListener(getActivity(), Misc.getScreenWidth(getActivity())));
        mFileDownloader.start();
        mFileDownloader.getLooper();
        setRetainInstance(true);
        setHasOptionsMenu(true);
        getActivity().setTitle(mFilename);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFileDownloader.quit();
        mAttacher.cleanup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);
        mImageView = (ImageView) view.findViewById(R.id.iv_photo);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setZoomable(true);
        mAttacher.setScaleType(ImageView.ScaleType.CENTER);

        File f = new File(getActivity().getCacheDir(), mFilename);
        if (f.exists()) {
            Bitmap bitmap = Misc.getScaledBitmapFromFile(getActivity(), f,
                    Misc.getScreenWidth(getActivity()));
            Drawable drawable = Misc.getDrawableFromBitmap(getActivity(), bitmap);
            mImageView.setImageDrawable(drawable);
        } else {
            mFileDownloader.queueFile(mImageView, mPictureUrl);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_imageview, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_item_saveImage:
                try {
                    saveImage();
                    Toast.makeText(getActivity(), R.string.info_imageSaved,
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.error_imageFailed,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFileDownloader.clearQueue();
    }

    private void saveImage() throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "SJTU BBS");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File fileOriginal = new File(getActivity().getCacheDir(), mFilename);
            File fileNew = new File(dir.getAbsolutePath(), mFilename);
            InputStream inputStream = new FileInputStream(fileOriginal);
            OutputStream outputStream = new FileOutputStream(fileNew);
            byte[] buffer = new byte[1025];
            int len;
            while ((len = inputStream.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, fileNew.getAbsolutePath());
            getActivity().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }
}
