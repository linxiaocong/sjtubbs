package io.github.linxiaocong.sjtubbs.activities;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.fragments.ImageViewFragment;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

/**
 * Created by linxiaocong on 2014/10/25.
 */
public class ImagePagerActivity extends AppCompatActivity {

    public static final String EXTRA_PICTURES = "extra_pictures";
    public static final String EXTRA_CURRENT_ITEM = "extra_currentItem";
    public static final String EXTRA_NEXT_URL = "extra_nextUrl";
    public static final String EXTRA_TITLE = "extra_title";

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mFragmentStatePagerAdapter;
    private ArrayList<String> mPictures;
    private int mCurrentItem;
    private String mNextUrl;
    private boolean mIsFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPictures = (ArrayList<String>)getIntent().getSerializableExtra(EXTRA_PICTURES);
        mCurrentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        mNextUrl = getIntent().getStringExtra(EXTRA_NEXT_URL);
        setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return ImageViewFragment.newInstance(mPictures.get(i));
            }
            @Override
            public int getCount() {
                return mPictures.size();
            }
        };
        mViewPager.setAdapter(mFragmentStatePagerAdapter);

        mViewPager.setCurrentItem(mCurrentItem);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }
            @Override
            public void onPageSelected(int i) {
            }
            @Override
            public void onPageScrollStateChanged(int i) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem == mPictures.size() - 1 && mNextUrl != null && !mIsFetching) {
                    (new FetchUploadedPicturesTask()).execute(mNextUrl);
                    mIsFetching = true;
                }
            }
        });
    }

    private class FetchUploadedPicturesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (mPictures == null) {
                mPictures = new ArrayList<String>();
            }
            return BBSUtils.getInstance().getUploadedPictures(params[0], mPictures);
        }

        @Override
        protected void onPostExecute(String result) {
            mNextUrl = result;
            mIsFetching = false;
            if (mFragmentStatePagerAdapter != null) {
                mFragmentStatePagerAdapter.notifyDataSetChanged();
            }
        }
    }
}
