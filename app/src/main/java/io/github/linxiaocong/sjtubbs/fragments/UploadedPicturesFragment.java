package io.github.linxiaocong.sjtubbs.fragments;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;
import io.github.linxiaocong.sjtubbs.utilities.UrlImageGetter;

/**
 * Created by linxiaocong on 2014/10/23.
 */
public class UploadedPicturesFragment extends Fragment {

    public static final String EXTRA_BOARD = "extra_board";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> mUploadedPictures;
    private GridView mGridView;
    private Board mBoard;
    private String mNextUrl;
    private boolean mIsFetching = false;

    public static UploadedPicturesFragment newInstance(Board board) {
        UploadedPicturesFragment fragment = new UploadedPicturesFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOARD, board);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoard = (Board)getArguments().getSerializable(EXTRA_BOARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_uploaded_pictures, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UploadedPicturesAdapter adapter = (UploadedPicturesAdapter)mGridView.getAdapter();
                try {
                    mUploadedPictures.clear();
                    mNextUrl = null;
                    mSwipeRefreshLayout.setRefreshing(true);
                    adapter.notifyDataSetChanged();
                    (new FetchUploadedPicturesTask()).execute(BBSUtils.BBS_INDEX + "/bbsfdoc2?board=" + mBoard.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mGridView = (GridView)view.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount &&
                        totalItemCount > 1 && mNextUrl != null && !mIsFetching) {
                    mIsFetching = true;
                    (new FetchUploadedPicturesTask()).execute(mNextUrl);
                }
            }
        });

        (new FetchUploadedPicturesTask()).execute(BBSUtils.BBS_INDEX + "/bbsfdoc2?board=" + mBoard.getName());
        return view;
    }

    private void setupAdapter() {
        if (getActivity() == null || mUploadedPictures == null)
            return;
        if (mGridView != null) {
            mGridView.setAdapter(new UploadedPicturesAdapter());
        }
    }

    private class FetchUploadedPicturesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (mUploadedPictures == null) {
                mUploadedPictures = new ArrayList<String>();
            }
            return BBSUtils.getInstance().getUploadedPictures(params[0], mUploadedPictures);
        }

        @Override
        protected void onPostExecute(String result) {
            mNextUrl = result;
            mIsFetching = false;
            mSwipeRefreshLayout.setRefreshing(false);
            if (mGridView.getAdapter() == null) {
                setupAdapter();
            } else {
                UploadedPicturesAdapter adapter = (UploadedPicturesAdapter)mGridView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }
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
            if (f.exists()) {
                return UrlImageGetter.getDrawableFromFile(getActivity(), f);
            } else if (UrlImageGetter.savedToFile(source, f)) {
                return UrlImageGetter.getDrawableFromFile(getActivity(), f);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            mImageView.setImageDrawable(result);
        }
    }

    private class UploadedPicturesAdapter extends ArrayAdapter<String> {

        public UploadedPicturesAdapter() {
            super(getActivity(), 0, mUploadedPictures);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.grid_item_picture, parent, false);
            }
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            String pictureUrl = getItem(position);
            (new FetchPictureTask(imageView)).execute(pictureUrl);
            return convertView;
        }
    }
}
