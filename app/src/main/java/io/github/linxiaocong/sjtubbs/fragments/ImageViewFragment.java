package io.github.linxiaocong.sjtubbs.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.utilities.FileDownloader;
import io.github.linxiaocong.sjtubbs.utilities.Misc;
import io.github.linxiaocong.sjtubbs.utilities.OnImageDownloadedListener;

/**
 * Created by linxiaocong on 2014/10/25.
 */
public class ImageViewFragment extends Fragment {

    public static final String EXTRA_SOURCE = "image_source";

    private String mPictureUrl;
    private FileDownloader<ImageView> mFileDownloader;

    public static ImageViewFragment newInstance(String filename) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SOURCE, filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPictureUrl = getArguments().getString(EXTRA_SOURCE);
        mFileDownloader = new FileDownloader<ImageView>(getActivity(), new Handler());
        mFileDownloader.setOnFileDownloadedListener(
                new OnImageDownloadedListener(getActivity(), Misc.getScreenWidth(getActivity())));
        mFileDownloader.start();
        mFileDownloader.getLooper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFileDownloader.quit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        String filename = mPictureUrl.substring(mPictureUrl.lastIndexOf('/') + 1);
        File f = new File(getActivity().getCacheDir(), filename);
        if (f.exists()) {
            Bitmap bitmap = Misc.getScaledBitmapFromFile(getActivity(), f,
                    Misc.getScreenWidth(getActivity()));
            Drawable drawable = Misc.getDrawableFromBitmap(getActivity(), bitmap);
            imageView.setImageDrawable(drawable);
        } else {
            mFileDownloader.queueFile(imageView, mPictureUrl);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFileDownloader.clearQueue();
    }
}
