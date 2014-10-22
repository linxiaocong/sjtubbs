package io.github.linxiaocong.sjtubbs.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.R;
import io.github.linxiaocong.sjtubbs.dao.ReplyDAO;
import io.github.linxiaocong.sjtubbs.models.Reply;
import io.github.linxiaocong.sjtubbs.models.Topic;
import io.github.linxiaocong.sjtubbs.utilities.UrlImageGetter;

public class ReplyListFragment extends Fragment {

    public static final String EXTRA_TOPIC = "extra_topic";

    private static final String tag = "ListFragment";

    private ArrayList<Reply> mReplyList;
    private Topic mTopic;
    private String mNextUrl;
    private boolean mIsLoading = false;
    private View mFooterView = null;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static ReplyListFragment newInstance(Topic topic) {
        ReplyListFragment fragment = new ReplyListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TOPIC, topic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopic = (Topic) getArguments().getSerializable(EXTRA_TOPIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ReplyListAdapter adapter = (ReplyListAdapter) mListView.getAdapter();
                try {
                    mReplyList.clear();
                    adapter.notifyDataSetChanged();
                    mNextUrl = null;
                    (new FetchReplyListTask()).execute(mTopic.getUrl());
                    mSwipeRefreshLayout.setRefreshing(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mListView = (ListView) view.findViewById(R.id.list_view);

        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount &&
                        totalItemCount > 1 && !mIsLoading && mNextUrl != null) {
                    mIsLoading = true;
                    (new FetchReplyListTask()).execute(mNextUrl);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        (new FetchReplyListTask()).execute(mTopic.getUrl());
        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        if (getActivity() == null)
            return;
        if (mReplyList != null)
            mListView.setAdapter(new ReplyListAdapter(mReplyList));
        else
            mListView.setAdapter(null);
    }

    private class FetchReplyListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            if (params[0] != null) {
                if (mReplyList == null) {
                    mReplyList = new ArrayList<Reply>();
                }
                Log.d(tag, "getting replies for topic: " + mTopic.getTitle());
                ReplyDAO replyDAO = new ReplyDAO(getActivity());
                return replyDAO.getReplyList(params[0], mReplyList);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mIsLoading = false;
            mNextUrl = result;
            if (mListView.getAdapter() == null) {
                setupAdapter();
            } else {
                ReplyListAdapter adapter = (ReplyListAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged();
            }
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private class ReplyListAdapter extends ArrayAdapter<Reply> {
        ReplyListAdapter(ArrayList<Reply> replies) {
            super(getActivity(), 0, replies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_reply, parent, false);
            }

            Reply reply = getItem(position);

            TextView textViewAuthor = (TextView) convertView
                    .findViewById(R.id.textview_author);
            textViewAuthor.setText(reply.getUser());

            TextView textViewTime = (TextView) convertView
                    .findViewById(R.id.textview_time);
            textViewTime.setText(reply.getTime());

            TextView textViewTitle = (TextView) convertView
                    .findViewById(R.id.textview_title);
            textViewTitle.setText(reply.getTitle());

            TextView textViewContent = (TextView) convertView
                    .findViewById(R.id.textview_content);
            textViewContent.setText(Html.fromHtml(reply.getContent(),
                    new UrlImageGetter(getActivity(), textViewContent), null));

            return convertView;
        }
    }
}
