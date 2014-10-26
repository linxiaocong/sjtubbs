package io.github.linxiaocong.sjtubbs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.activities.ImagePagerActivity;
import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;
import io.github.linxiaocong.sjtubbs.utilities.BitmapCache;
import io.github.linxiaocong.sjtubbs.utilities.Misc;

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
    private Context mContext;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
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
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
                intent.putExtra(ImagePagerActivity.EXTRA_PICTURES, mUploadedPictures);
                intent.putExtra(ImagePagerActivity.EXTRA_CURRENT_ITEM, i);
                intent.putExtra(ImagePagerActivity.EXTRA_NEXT_URL, mNextUrl);
                startActivity(intent);
            }
        });

        (new FetchUploadedPicturesTask()).execute(BBSUtils.BBS_INDEX + "/bbsfdoc2?board="
                + mBoard.getName());
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
            File f = new File(mContext.getCacheDir(), filename);
            if (!f.exists()) {
                Misc.savedToFile(source, f);
            }
            Bitmap bitmap;
            if ((bitmap = BitmapCache.getInstance().get(filename)) == null) {
                bitmap = Misc.getScaledBitmapFromFile(mContext, f, 320);
                BitmapCache.getInstance().put(filename, bitmap);
            }
            return Misc.getDrawableFromBitmap(mContext, bitmap);
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
