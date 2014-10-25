package io.github.linxiaocong.sjtubbs.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.utilities.Misc;

/**
 * Created by linxiaocong on 2014/10/25.
 */
public class ImageViewFragment extends Fragment {

    public static final String EXTRA_SOURCE = "image_source";

    private String mSource;

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
        mSource = getArguments().getString(EXTRA_SOURCE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        (new FetchPictureTask(imageView)).execute(mSource);
        return view;
    }

    private class FetchPictureTask extends AsyncTask<String, Void, Drawable> {

        private ImageView mImageView;

        public FetchPictureTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            String filename = source.substring(source.lastIndexOf('/') + 1);
            if (getActivity() == null) {
                return null;
            }
            File f = new File(getActivity().getCacheDir(), filename);
            if (!f.exists()) {
                Misc.savedToFile(source, f);
            }
            Bitmap bitmap = Misc.getScaledBitmapFromFile(getActivity(), f,
                    Misc.getScreenWidth(getActivity()));
            return Misc.getDrawableFromBitmap(getActivity(), bitmap);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            mImageView.setImageDrawable(result);
        }
    }
}
